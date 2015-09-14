package com.blind.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.livhong.connect.Item;
import com.livhong.connect.User;

public class JSONtrans {
	
	public static JSONObject ItemToJSON(Item item){
		JSONObject json = new JSONObject();
		try {
			json.put("sId", item.sId);
			json.put("rId", item.rId);
			json.put("sName", item.sName);
			json.put("rName", item.rName);
			json.put("time", item.time);
			json.put("Identifer", item.Identifer);
			json.put("msg", item.msg);
			json.put("length", item.length);
			json.put("recfile", Database.saveItemRecfile(item));
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Item JSONToItem(JSONObject json){
		Item item = new Item();
		item.sId = json.optInt("sId");
		item.rId = json.optInt("rId");
		item.sName = json.optString("sName");
		item.rName = json.optString("rName");
		item.time = json.optString("time");
		item.Identifer = json.optString("Identifer");
		item.msg = json.optString("msg");
		item.length = json.optLong("length");
		item.recFile = new File(json.optString("recfile"));
		return item;
	}
	
	public static User JSONToUser(JSONObject json){
		User user = new User();
		user.setId(json.optInt("id"));
		user.setName(json.optString("name"));
		user.setPassword(json.optString("password"));
		user.setPending(json.optString("pending"));
		return user;
	}
	
	public static JSONObject UserToJSON(User user){
		try {
			JSONObject json = new JSONObject();
			json.put("id", user.getId());
			json.put("name", user.getName());
			json.put("password", user.getPassword());
			json.put("pending", user.getPending());
			return json;
		} catch (JSONException e) {
			return null;
		}
	}
}
