package com.livhong.connect;

import java.util.Calendar;

public class Constants {

	public static final int SERVER_PORT = 8089;
	public static final int MAX_CLIENT = 10;
	
	public static final String SERVER_IP = "192.168.1.104";
	
	public static final String SEPARATOR = ": ";
	
	public static final String MSG_IDENTIFER = "MSG";
	public static final String PEOPLE_IDENTIFER = "PEOPLE";
	public static final String CONNECT_IDENTIFER = "INFO";
	public static final String QUIT_IDENTIFER = "QUIT";
	public static final String REC_IDENTIFER = "REC";
	public static final String REC_END = "RecEnd";
	
	public static final String REC_PATH = "androidRudioRecorder/audio/cache";
	public static final String AUDIO_NAME = "audiotempASDERT.amr";
	
	public final static int HAS_CONNECT = 0x1;
	public final static int HAS_DISCONNECT = 0x11;
	public final static int HAS_SEND = 0x111;
	public final static int HAS_RECEIVED_REC = 0x131;
	public final static int HAS_RECEIVED_MSG = 0x10;
	public final static int SHOW_PEOPLE = 0x12;
	public final static int QUIT = 0x13;
	
	public final static String SHARED_SELF = "shared_self";
	public final static String SHARED_SELF_ID = "shared_self_id";
	public final static String SHARED_SELF_NAME = "shared_self_name";
	public static final String ENTRY_SEPARATOR = '\u0001'+"";
	public static final String FROM_CLIENT = "come from client!!!!!";
	
	public static String getTime(){
		Calendar cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);//�õ���
		int month=cal.get(Calendar.MONTH)+1;//�õ��£���Ϊ��0��ʼ�ģ�����Ҫ��1
		int day=cal.get(Calendar.DAY_OF_MONTH);//�õ���
		int hour=cal.get(Calendar.HOUR);//�õ�Сʱ
		int minute=cal.get(Calendar.MINUTE);//�õ�����
		int second=cal.get(Calendar.SECOND);
		return year+"."+month+"."+day+"."+hour+"-"+minute+"-"+second;
	}

}
