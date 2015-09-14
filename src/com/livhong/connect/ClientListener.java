package com.livhong.connect;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.os.Environment;

public abstract class ClientListener extends Thread{

	String name = null;
//	InputStream is;
	DataInputStream dis = null;
	PrintStream ps = null;
	Socket socket = null;
	ChatClient parent = null;
	boolean running = true;
	public final String REC_PATH = "androidRudioRecorder/audio/cache";
	
	private String strTempFile = "audiorecorder";
	
	public ClientListener(ChatClient p, String n, Socket s){
		this.parent = p;
		this.name = n;
		this.socket = s;
		
		try{
			dis = new DataInputStream(s.getInputStream());
			ps = new PrintStream(s.getOutputStream());
		}catch(Exception e){
			System.out.println("Error:"+e);
			parent.disconnect();
		}
	}
	
	public void toStop(){
		this.running = false;
	}
	
	public void run(){
		String line = null;
		while(running){
			line = null;
			try{
				line = dis.readLine();
				System.out.println("Receive msg: "+line);
			}catch(Exception e){
				System.out.println("Error:"+e);
				parent.disconnect();
			}
			if(line==null){
				parent.listener = null;
				parent.soc = null;
				parent.peopleList.clear();
				running = false;
				return;
			}
			
			String[] entries = line.split(Constants.ENTRY_SEPARATOR);
			Item msg = new Item();
			msg.sName = entries[0];
			msg.sId = Integer.parseInt(entries[1]);
			msg.rName = entries[2];
			msg.rId = Integer.parseInt(entries[3]);
			msg.time = entries[4];
			msg.Identifer = entries[5];
			msg.msg = entries[6];
			msg.length = Long.parseLong(entries[7]);
			
			if(msg.Identifer.equals(Constants.MSG_IDENTIFER)){
				//refresh the list of the chat record!
				onReceivedMsg(msg);
			} else if(msg.Identifer.equals(Constants.REC_IDENTIFER)){
				boolean sdcardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				File myRecAudioDir = null;
				if(sdcardExit){
					myRecAudioDir = new File(Environment.getExternalStorageDirectory(), REC_PATH);
					if(!myRecAudioDir.exists()){
						myRecAudioDir.mkdirs();
					}
				}else{
					return;
				}
				
				//you will get the name who send the message
				//DON'T delete the following code even though you will use the item.
				long len = msg.length;
				File recFile = null;
				byte[] bytes = new byte[2048];
				try {
					recFile = File.createTempFile(strTempFile, ".mp3", myRecAudioDir);
					FileOutputStream output = new FileOutputStream(recFile);
					int cLen = 0;
					int read = (len>2048)?2048:(int)len;
					int count = 0;
					for(;;){
						if((cLen = dis.read(bytes, 0, read))==-1){
							continue;
						}
						len -= cLen;
						output.write(bytes, 0, cLen);
						if(len <= 0){
							break;
						}
						read = (len>2048)?2048:(int)len;
					}
					output.flush();
					output.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg.recFile = recFile;
				onReceivedRec(msg);
			} else if(msg.Identifer.equals(Constants.QUIT_IDENTIFER)){
				try{
					running = false;
					parent.listener = null;
					if (parent.soc != null) {
						parent.soc.close();
						parent.soc = null;
					}
				} catch(Exception e){
						System.out.println("Error:"+e);
				} finally{
					parent.soc = null;
					parent.peopleList.clear();
				}
				onQuit();
				break;
			}
		}
		parent.peopleList.clear();
	}
	
	public abstract void onReceivedMsg(Item it);
	public abstract void onQuit();
	public abstract void onReceivedRec(Item it);
}
