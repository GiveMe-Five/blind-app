package com.blind.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

import com.livhong.connect.Item;
import com.livhong.connect.User;

public class Database {

	private JSONArray friend_menu;
	private JSONObject chat_history, index_history;
	private static User myinfo;
	private static String storage_path;
	public static int REGISTER = 0, LOGIN = 1;
	
	
	public void setPathname(String name){
		storage_path = Environment.getExternalStorageDirectory().getPath() + "/data/clover/" + name;
	}
	
	public String getPathname(){
		return storage_path;
	}
	
	public Database(int method, User user){
		if (method == REGISTER){
			setPathname(user.getName());
			friend_menu = new JSONArray();
			chat_history = new JSONObject();
			index_history = new JSONObject();
			myinfo = user;
			registerHost(user);
		}
		else if (method == LOGIN){
			setPathname(user.getName());
			readInfofromFile(user.getName());
			readChatfromFile();
		}
	}
	/*
	 * Ŀ¼���
	 */
	
	public boolean writeInfotoFile(){
		try {
			JSONObject json = new JSONObject();
			json.put("host", JSONtrans.UserToJSON(myinfo));
			json.put("friend", friend_menu);
			json.put("index", index_history);
			BufferedWriter writer = 
					new BufferedWriter(new FileWriter(storage_path + "/" + myinfo.getName() + ".info"));
			writer.write(json.toString());
			writer.newLine();
			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean readInfofromFile(String name){
		try {
			BufferedReader reader =
					new BufferedReader(new FileReader(storage_path + "/" + name + ".info"));
			JSONObject json = new JSONObject(reader.readLine());
			myinfo = JSONtrans.JSONToUser(json.getJSONObject("host"));
			friend_menu = json.getJSONArray("friend");
			index_history = json.getJSONObject("index");
			reader.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean registerHost(User host){
		File file = new File(storage_path);
		if (!file.exists()) file.mkdirs();
		try {
			BufferedWriter writer = 
					new BufferedWriter(new FileWriter(storage_path + "/" + host.getId() + ".info"));
			JSONObject json = new JSONObject();
			json.put("host", JSONtrans.UserToJSON(host));
			json.put("friend", friend_menu);
			writer.write(json.toString());
			writer.newLine();
			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean registerFriend(User user){
		String path = storage_path + "/" + user.getName();
		File folder = new File(path);
		if (!folder.exists()) folder.mkdirs();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + user.getId() + ".hist"));
			writer.write(new JSONArray().toString());
			writer.newLine();
			writer.flush();
			writer.close();
			friend_menu.put(JSONtrans.UserToJSON(user));
			chat_history.put(user.getName(), new JSONArray());
			index_history.put(user.getName(), -1);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean readChatfromFile(){
		for (int i = 0; i < friend_menu.length(); i++){
			try {
				String name = JSONtrans.JSONToUser(friend_menu.getJSONObject(i)).getName();
				BufferedReader reader = new BufferedReader(new FileReader(storage_path + "/" 
						+ name + "/" + name + ".hist"));
				chat_history.put(name, new JSONArray(reader.readLine()));
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
		return true;
	}
	
	public boolean writeChattoFile(){
		for (int i = 0; i < friend_menu.length(); i++){
			try {
				String name = JSONtrans.JSONToUser(friend_menu.getJSONObject(i)).getName();
				BufferedWriter writer = new BufferedWriter(new FileWriter(storage_path + "/" 
						+ name + "/" + name + ".hist"));
				writer.write(chat_history.getJSONArray(name).toString());
				writer.newLine();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
		return true;
	}
	
	/*
	 * chat��Ϣ���
	 */
	
	public boolean updateChat(Item item){
		try {
			if (item.sName == myinfo.getName()) chat_history.getJSONArray(item.rName).put(JSONtrans.ItemToJSON(item));
			else chat_history.getJSONArray(item.sName).put(JSONtrans.ItemToJSON(item));
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Item getChatItem(String name, int index){
		return JSONtrans.JSONToItem(chat_history.optJSONArray(name).optJSONObject(index));
	}
	
	public Item getChatItem(String name){
		JSONArray json = chat_history.optJSONArray(name);
		return JSONtrans.JSONToItem(json.optJSONObject(json.length() - 1));
	}
	
	public int getChatCount(String name){
		return chat_history.optJSONArray(name).length();
	}
	
	
	public static String saveItemRecfile(Item item){
		String path;
		if (item.sName == myinfo.getName()) path = storage_path + "/" + item.rName + "/" + item.time + "-" + "IDENTIFYINGCODE.rec";
		else path = storage_path + "/" + item.sName + "/" + item.time + "-" + "IDENTIFYINGCODE.rec";
		File file = new File(path);
		try {
			if(!file.exists()) file.createNewFile(); 
			fileChannelCopy(item.recFile, file);
			return path;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("debug", "1111 - X");
			return null;
		}
	}
	
	public JSONArray getFriend_menu(){
		return friend_menu;
	}
	
	public JSONArray getChatArray(String name){
		return chat_history.optJSONArray(name);
	}
	
	public User getMyinfo(){
		return myinfo;
	}
	
	public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
	
	public int queryChatIndex(String name){
		return index_history.optInt(name);
	}
	
	public int getChatIndex(String name){
		int index = index_history.optInt(name);
		index_history.remove(name);
		return index;
	}
	
	public void optChatIndex(String name, int index){
		try {
			index_history.put(name, index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static User createUser(int id, String name, String password, String pending){
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setPassword(password);
		user.setPending(pending);
		return user;
	}
	
	public void saveInfo(){
		writeInfotoFile();
		writeChattoFile();
	}
}