package com.livhong.connect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.blind.activity.ContactActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;

public class ChatClient {

	int myid = -1;
	String myname = "Livhong";
	String mymsg = "I am livhong, I am testing my client!";
	User myself;
	
	public static final String QUIT = "quit";
	public static final String CONNECT = "connect";
	public static final String SEND = "send";
	public static final String SHOW_PEOPLE_LIST = "show_people_list";
	ContactActivity activity;
	
	Socket soc = null;
	PrintStream ps = null;
	ClientListener listener = null;
	public static List peopleList = new ArrayList(10);
	
	private static boolean running = true;
	
	public ChatClient(){}
	
	public ChatClient(String name, ContactActivity activity){
		this.myname = name;
		this.activity = activity;
		SharedPreferences sp = activity.getSharedPreferences(Constants.SHARED_SELF, Activity.MODE_PRIVATE);
		myid = sp.getInt(Constants.SHARED_SELF_ID, -1);
		myname = sp.getString(Constants.SHARED_SELF_NAME, "");
		myself = new User();
		myself.setId(myid);
		myself.setName(myname);
	}
	
	public void close(){
		disconnect();
	}
	
	public void connect(){
		if(soc==null){
			try {
				System.out.println("Begin to connect!.............");
				soc = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
				System.out.println(soc);
				ps = new PrintStream(soc.getOutputStream());
				StringBuffer info = new StringBuffer(Constants.CONNECT_IDENTIFER).append(Constants.SEPARATOR);
				String userinfo = myname + Constants.SEPARATOR +myid + Constants.SEPARATOR + InetAddress.getLocalHost().getHostAddress();
				//i.e. CONNECT: livhong: 0: 127.0.0.1
				ps.println(info.append(userinfo));
				ps.flush();
				listener = activity.new MyClientListener(this, myname, soc);
				listener.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void disconnect(){
		if(soc!=null){
			try{
				running = false;
				String msg = "dd"+Constants.ENTRY_SEPARATOR+0+Constants.ENTRY_SEPARATOR+
						"dd"+Constants.ENTRY_SEPARATOR+0+Constants.ENTRY_SEPARATOR+Constants.getTime()+
						Constants.ENTRY_SEPARATOR+Constants.QUIT_IDENTIFER+Constants.ENTRY_SEPARATOR+
						"quit"+Constants.ENTRY_SEPARATOR+"-1";
				ps.println(msg);
				ps.flush();
//				soc.close();
				listener.toStop();
//				soc = null;
			} catch(Exception e){
				System.out.println("Error:"+e);
			}
		}
	}
	
	private void sendMsg(){
		if(soc!=null){
			StringBuffer msg = new StringBuffer(Constants.MSG_IDENTIFER).append(Constants.SEPARATOR);
			ps.println(msg.append(mymsg));
			ps.flush();
		}
	}
	
	private void sendMsg(String msg){
		this.mymsg = msg;
		sendMsg();
	}
	
	public void sendMsg(Item item){
		
		if(soc==null){
			return;
		}
		if(item.Identifer.equals(Constants.MSG_IDENTIFER)){
			String msg = item.sName+Constants.ENTRY_SEPARATOR+item.sId+Constants.ENTRY_SEPARATOR+
					item.rName+Constants.ENTRY_SEPARATOR+item.rId+Constants.ENTRY_SEPARATOR+item.time+
					Constants.ENTRY_SEPARATOR+item.Identifer+Constants.ENTRY_SEPARATOR+item.msg+Constants.ENTRY_SEPARATOR+"-1";
			ps.println(msg);
			ps.flush();
		}else if(item.Identifer.equals(Constants.REC_IDENTIFER)){
			if(item.recFile == null){
				return;
			}
			File file = item.recFile;
			if(file.exists()){
				try {
					String msg = item.sName+Constants.ENTRY_SEPARATOR+item.sId+Constants.ENTRY_SEPARATOR+
							item.rName+Constants.ENTRY_SEPARATOR+item.rId+Constants.ENTRY_SEPARATOR+item.time+
							Constants.ENTRY_SEPARATOR+item.Identifer+Constants.ENTRY_SEPARATOR+Constants.FROM_CLIENT+
							Constants.ENTRY_SEPARATOR+item.length;
					System.out.println("msg : "+msg);
					ps.println(msg);
					FileInputStream input = new FileInputStream(file);
					byte[] bytes = new byte[2048];
					int len = -1;
					for(;(len = input.read(bytes))!=-1;){
						ps.write(bytes, 0, len);
					}
					ps.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
//	public static void main(String[] args) {
//		System.out.println("Please input your name!");
//		Scanner sca = new Scanner(System.in);
//		myname = sca.next();
//		System.out.println("Your name is "+myname);
//		ChatClient chatClient = new ChatClient();
//		while(running){
//			String command = sca.nextLine();
//			System.out.println("command : "+command);
//			String[] cmd = command.trim().split(" ");
//			for(int i = 0; i< cmd.length;i++){
//				System.out.println(cmd[i]);
//			}
//			if(cmd.length==0){
//				System.out.println("Input your command!");
//				continue;
//			}
//			String header = cmd[0];
//			if(header.equals(CONNECT)){
//				chatClient.connect();
//			}else if(header.equals(QUIT)){
//				chatClient.close();
//			}else if(header.equals(SEND)){
//				if(cmd.length>1){
//					mymsg = command.replaceFirst(header+" ", "");
//				} else {
//					mymsg = "";
//				}
//				chatClient.sendMsg();
//			}else if(header.equals(SHOW_PEOPLE_LIST)){
//				System.out.println("-------------------------------");
//				for(int i = 0; i < peopleList.size(); i++){
//					System.out.println(peopleList.get(i));
//				}
//				System.out.println("-------------------------------");
//			}else {
//				System.out.println("Please input valid command!");
//			}
//		}
//		System.out.println("ChatClient has been terminated!");
//	}
	
}
