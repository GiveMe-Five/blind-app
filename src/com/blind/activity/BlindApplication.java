package com.blind.activity;

import com.iflytek.cloud.SpeechUtility;

import android.app.Application;

public class BlindApplication extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		// Ӧ�ó�����ڴ�����,�����ֻ��ڴ��С��ɱ����̨���,���SpeechUtility����Ϊnull
		// �����������Ӧ��appid
		SpeechUtility.createUtility(this, "appid=" + "54eed7e6");
	}
}
