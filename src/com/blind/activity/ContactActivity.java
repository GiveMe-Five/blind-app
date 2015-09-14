package com.blind.activity;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.blind.R;
import com.livhong.connect.*;
import com.livhong.gesture.MyGestureDetector;
import com.livhong.gesture.OnGestureListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ContactActivity extends Activity implements OnGestureListener{
	
	//Menu State
	final static int MENU_USER = 0, MENU_CHAT = 1, ROOM_CHAT = 2;
	
	private ChatClient chatClient;
	private MyGestureDetector mygesturedetector;
	
	private int state, index;
	private Database datastore;
	private ArrayList<User> message_list = new ArrayList<User>();
	private Object list;
	private ChatterInfo chatter;
	
	private TextView v_title, v_uparrow, v_name, v_message, v_tip, v_downarrow;
	
	private void setTextViewSize(TextView view, int height, int width, int text){
		view.setHeight(height);
		//view.setWidth(width);
		view.setTextSize(text);
	}
	
	private Comparator<User> comparator_list = new Comparator<User>(){
		@Override
		public int compare(User arg0, User arg1){
			String time0 = datastore.getChatItem(arg0.getName()).time;
			String time1 = datastore.getChatItem(arg1.getName()).time;
			return time0.compareTo(time1);
		}
	};
	
	private void setScreenSize(){
		Display display = this.getWindowManager().getDefaultDisplay();
	    int width = 646;//;display.getWidth();
	    int height = 916;//display.getHeight();
	    
		v_title = (TextView)findViewById(R.id.chat_title);
		v_uparrow = (TextView)findViewById(R.id.chat_uparrow);
		v_name = (TextView)findViewById(R.id.chat_name);
		v_message = (TextView)findViewById(R.id.chat_message);
		v_tip = (TextView)findViewById(R.id.chat_tip);
		v_downarrow = (TextView)findViewById(R.id.chat_downarrow);
		
		
//		setTextViewSize(v_title, (int)(height * 0.15), width, (int)(height * 0.08));
//		setTextViewSize(v_uparrow, (int)(height * 0.10), width, (int)(height * 0.10));
//		setTextViewSize(v_name, (int)(height * 0.15), width, (int)(height * 0.12));
//	    setTextViewSize(v_message, (int)(height * 0.20), width, (int)(height * 0.06));
//	    setTextViewSize(v_tip, (int)(height * 0.10), width, (int)(height * 0.06));
//	    setTextViewSize(v_downarrow, (int)(height * 0.10), width, (int)(height * 0.10));
	}
	
	private void updateColor(){
		if (state == MENU_USER){
			v_title.setTextColor(Color.WHITE);
			v_title.setBackgroundColor(Color.rgb(255, 102, 102));
			v_tip.setTextColor(Color.WHITE);
			v_tip.setBackgroundColor(Color.rgb(255, 102, 102));
		}
		else if (state == MENU_CHAT){
			v_title.setTextColor(Color.BLACK);
			v_title.setBackgroundColor(Color.rgb(255, 255, 153));
			v_tip.setTextColor(Color.BLACK);
			v_tip.setBackgroundColor(Color.rgb(255, 255, 153));
		}
		else if (state == ROOM_CHAT){
			v_title.setTextColor(Color.WHITE);
			v_title.setBackgroundColor(Color.rgb(51, 153, 102));
			v_tip.setTextColor(Color.WHITE);
			v_tip.setBackgroundColor(Color.rgb(51, 153, 102));
		}
	}
	
	private void setMenu_user(){
		state = MENU_USER;
		updateColor();
		index = 0;
		releaseChatter(chatter);
		v_title.setText("myFriend");
		list = datastore.getFriend_menu();
		updateScreen();
	}
	
	private void setMenu_chat(){
		state = MENU_CHAT;
		updateColor();
		index = 0;
		releaseChatter(chatter);
		v_title.setText("messageList");
		list = message_list;
		updateScreen();
	}
	
	private void setRoom_chat(User user){
		if (!message_list.contains(user)) message_list.add(user);
		state = ROOM_CHAT;
		updateColor();
		index = 0;
		chatter = new ChatterInfo(user, datastore.getChatIndex(user.getName()));
		v_title.setText("chatRoom");
		list = datastore.getChatArray(user.getName());
		updateScreen();
	}
	
	void releaseChatter(ChatterInfo chatter){
		if (chatter != null) datastore.optChatIndex(chatter.getName(), chatter.getIndex());
		chatter = null;
	}
	
	private int getLength(Object list){
		if (list instanceof JSONArray) return ((JSONArray) list).length();
		else return ((ArrayList<User>)list).size();
	}
	
	private User getUser(Object list, int index){
		if (list instanceof JSONArray) return JSONtrans.JSONToUser(((JSONArray)list).optJSONObject(index));
		else return ((ArrayList<User>)list).get(index);
	}
	
	private boolean hasNew(Object list, int index){
		if ((chatter != null) && (index > chatter.maxIndex)) chatter.maxIndex = index;
		if (state == MENU_CHAT){
			String name = ((ArrayList<User>)list).get(index).getName();
			return (datastore.queryChatIndex(name) + 1 < datastore.getChatCount(name))? true: false;
		}
		else if (state == ROOM_CHAT){
			return (chatter.maxIndex + 1 < getLength(list))? true: false;
		}
		return false;
	}
	
	private boolean arrowOption(int upordown, int index, int size){
		if (size == 0) return false;
		if (upordown * (size - 1) != index) return true; else return false;
	}
	
	private void updateArrow(Object list, int index){
		if (arrowOption(0, index, getLength(list))) v_uparrow.setTextColor(Color.BLACK);
		else v_uparrow.setTextColor(Color.GRAY);
		if (arrowOption(1, index, getLength(list))) v_downarrow.setTextColor(Color.BLACK);
		else v_downarrow.setTextColor(Color.GRAY);
	}
	
	Item getItem(User user, int index){
		if (datastore.getChatCount(user.getName()) > 0)
			return datastore.getChatItem(user.getName(), index);
		else
			return null;
	}
	
	Item getItem(User user){
		return getItem(user, datastore.getChatCount(user.getName()) - 1);
	}
	
	private void updateScreen(){
		User user = null;
		if ((state == MENU_USER) || (state == MENU_CHAT)){
			if (getLength(list) > 0) user = getUser(list, index);
			else user = null;
		}
		else if (state == ROOM_CHAT) user = chatter.getUser();
		if (user != null){
			v_name.setText(user.getName());
			Item item = null;
			if ((state == MENU_USER) || (state == MENU_CHAT)) item = getItem(user);
			else if (state == ROOM_CHAT) item = getItem(user, index);
			if (item != null){
				v_message.setText(item.time);
				if ((state == MENU_CHAT) || (state == ROOM_CHAT)){
					if (hasNew(list, index)){
						v_tip.setText("有新未读消息");
					}
					else{
						v_tip.setText("无未读新消息");
					}
				}
				else if (state == MENU_USER){
					v_tip.setText("显示是否在线");
				}
			}
			else{
				v_message.setText("无聊天记录");
				if ((state == MENU_CHAT) || (state == ROOM_CHAT)) v_tip.setText("--");
				else if (state == MENU_USER) v_tip.setText("显示是否在线");
			}
			
		}
		else{
			if (state == MENU_USER){
				v_name.setText("暂未添加任何好友");
				v_message.setText("--");
				v_tip.setText("--");
			}
			else if (state == MENU_CHAT){
				v_name.setText("暂未正在通讯的好友");
				v_message.setText("进入联系人菜单开始通话吧");
				v_tip.setText("--");
			}
		}
		updateArrow(list, index);
	}

	
	Handler handler = new Handler() {
		//TODO �������������Ƶ����
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.HAS_CONNECT:
				break;
			case Constants.HAS_DISCONNECT:
				break;
			case Constants.HAS_SEND:
				break;
			case Constants.HAS_RECEIVED_REC:
				datastore.updateChat((Item) msg.obj);
				break;
			case Constants.HAS_RECEIVED_MSG:
				datastore.updateChat((Item) msg.obj);
				break;
			case Constants.SHOW_PEOPLE:
				break;
			case Constants.QUIT:
				datastore.saveInfo();
				datastore = null;
				disconnect();
				break;
			}
			updateScreen();
		}
	};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main);
        mygesturedetector = new MyGestureDetector(getApplicationContext(), this);
        setScreenSize();
        
        String mName = "yuan";
        int mId = 2;
        String nName = "livhong";
        int nId = 1;
        /*
         * Test
         */
        //datastore = new Database(Database.REGISTER, Database.createUser(10001, "Test-Keyan", "123456", "pending"));
        //datastore.registerFriend(Database.createUser(10002, "Test-Lvhong", "123456", "pending"));
        //datastore.registerFriend(Database.createUser(10003, "Test-YuanJ", "123456", "pending"));
        
//        datastore = new Database(Database.REGISTER, Database.createUser(1, "livhong", "123", ""));
//        datastore.registerFriend(Database.createUser(2, "yuan", "123", ""));
        //
        datastore = new Database(Database.REGISTER, Database.createUser(mId, mName, "123", ""));
        datastore.registerFriend(Database.createUser(nId, nName, "123", ""));
        
        SharedPreferences mySharedPreferences= getSharedPreferences(Constants.SHARED_SELF, 
        		Activity.MODE_PRIVATE); 
        SharedPreferences.Editor editor = mySharedPreferences.edit(); 
	      //用putString的方法保存数据 
	      editor.putInt(Constants.SHARED_SELF_ID, mId); 
	      editor.putString(Constants.SHARED_SELF_NAME, mName); 
	      //提交当前数据 
	      editor.commit(); 
        
        //TODO �������
        int menu = 0;
        if ((savedInstanceState != null) && (savedInstanceState.containsKey("menustate")))
        	menu = savedInstanceState.getInt("menustate");
        if (menu == MENU_USER) setMenu_user();
        else if (menu == MENU_CHAT) setMenu_chat();
        else setMenu_user(); //default
        updateColor();
        updateScreen();
        connect(datastore.getMyinfo().getName());
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return mygesturedetector.onTouchEvent(event);
	}
    
    private void connect(String name){
    	if ((name == null) || (name.equals(""))) name = "user";
    	chatClient = new ChatClient(name, this);
    	new Thread(){
    		public void run(){
    			chatClient.connect();
    			Message msg = new Message();
    			msg.what = Constants.HAS_CONNECT;
    			handler.sendMessage(msg);
    		}
    	}.start();
    }
    
    private void disconnect(){
    	new Thread(){
    		public void run(){
    			chatClient.disconnect();
    			Message msg = new Message();
    			msg.what = Constants.HAS_DISCONNECT;
    			handler.sendMessage(msg);
    		}
    	}.start();
    }
	
	public class MyClientListener extends ClientListener{
		
		//DON'T change or delete
		public MyClientListener(ChatClient p, String n, Socket s) {
			super(p, n, s);
		}

		public void onQuit(){
			Message msg1 = new Message();
			msg1.what = Constants.QUIT;
			handler.sendMessage(msg1);
		}
		
		public void onReceivedMsg(Item item){
			Message msg1 = new Message();
			msg1.what = Constants.HAS_RECEIVED_MSG;
			msg1.obj = item;
			handler.sendMessage(msg1);
		}

		@Override
		public void onReceivedRec(Item item) {
			Message msg = new Message();
			msg.what = Constants.HAS_RECEIVED_REC;
			msg.obj = item;
			handler.sendMessage(msg);
		}
	}
	
	class ChatterInfo{
		User chat;
		int maxIndex;
		
		ChatterInfo(User chat, int maxIndex){
			this.chat = chat;
			this.maxIndex = maxIndex;
		}
		
		User getUser(){
			return chat;
		}
		
		int getIndex(){
			return maxIndex;
		}
		
		String getName(){
			return chat.getName();
		}
	}
	
	@Override
	public boolean onDown(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleClick(MotionEvent ev) {
		new Thread(){
			public void run(){
				System.out.println("Double Click!!!!!!!!!!!!!!!!");
				Message msg1 = new Message();
				msg1.what = Constants.HAS_RECEIVED_REC;
				Item item = new Item();
				item.sId = 1;
				item.sName = "livhong";
				item.rId = 2;
				item.rName = "yuan";
				item.Identifer = Constants.REC_IDENTIFER;
				item.msg = Constants.FROM_CLIENT;
				item.time = Constants.getTime();
				//Log.i("DEBUG", "1111C" + item.time);
				File file = new File(datastore.getPathname() + "/Test.wav");
				if (!file.exists())
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				item.recFile = file;
				item.length = file.length();
				msg1.obj = item;
				System.out.println(item.toString());
				chatClient.sendMsg(item);
				System.out.println("SEND OK!!!!!!!!!!!!!!!!!!!!!!!");
			}
		}.start();
		return true;
	}

	@Override
	public boolean onLongPress(MotionEvent ev) {
		if (state == MENU_USER) setMenu_chat();
		else if (state == MENU_CHAT) setMenu_user();
		return true;
	}

	@Override
	public boolean onSlipUp() {
		if (index - 1 >= 0){
			index--;
			updateScreen();
		}
		return true;
	}

	@Override
	public boolean onSlipDown() {
		if (index + 1 < getLength(list)){
			index++;
			updateScreen();
		}
		return true;
	}

	@Override
	public boolean onSlipLeft() {
		if (state == ROOM_CHAT){
			setMenu_chat();
		}
		return true;
	}

	@Override
	public boolean onSlipRight() {
		if ((state != ROOM_CHAT) && (getUser(list, index) != null)){
			setRoom_chat(getUser(list, index));
		}
		return true;
	}

	@Override
	public boolean onSlipDoubleUp() {
		// TODO Auto-generated method stub
		return false;
	}

	//TODO ���Ŀ¼
	@Override
	public boolean onSlipDoubleDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSlipDoubleLeft() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSlipDoubleRight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSlipFastUp() {
		return onSlipUp();
	}

	@Override
	public boolean onSlipFastDown() {
		return onSlipDown();
	}
	
	void addMessage_list(User user){
		message_list.add(user);
	}
	
	void sortMessage_list(){
		Collections.sort(message_list, comparator_list);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		disconnect();
		super.onDestroy();
	}
}
