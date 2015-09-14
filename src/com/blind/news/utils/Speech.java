package com.blind.news.utils;

import com.iflytek.cloud.*;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class Speech extends Dialog {
	Context context;
	SpeechSynthesizer mTts;
	public Speech(Context context) {
		super(context);
		this.context = context;
	}
	
	private void initialize() {
		mTts.setParameter(SpeechConstant.VOICE_NAME, Settings.VOICE_NAME);
		mTts.setParameter(SpeechConstant.SPEED, Settings.SPEED);
		mTts.setParameter(SpeechConstant.VOLUME, Settings.VOLUME);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        initialize();
	}    
	
	public void speech(String str) {
		mTts.startSpeaking(str, MyListener);
	}
	
	private SynthesizerListener MyListener = new SynthesizerListener(){
		public void onCompleted(SpeechError error) {} 
		public void onBufferProgress(int percent, int beginPos, int endPos, String info) {} 
		public void onSpeakPaused() {}
		public void onSpeakProgress(int percent, int beginPos, int endPos) {}
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
		public void onSpeakBegin() {}
		public void onSpeakResumed() {}
	};
}

class Settings {
	public static String VOICE_NAME = "xiaoyan";
	public static String SPEED = "70";
	public static String VOLUME = "80";
	
	
}
