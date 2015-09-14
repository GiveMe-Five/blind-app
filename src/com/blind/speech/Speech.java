package com.blind.speech;

import com.iflytek.cloud.*;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class Speech extends Dialog {
	private Context context;
	private SpeechSynthesizer mTts;
	
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	
	private String[] cloudVoicersEntries;
	private String[] cloudVoicersValue ;
	private int mPercentForBuffering = 0;//ª∫≥ÂΩ¯∂»
	private int mPercentForPlaying = 0;//≤•∑≈Ω¯∂»
	
	public Speech(Context context) {
		super(context);
		this.context = context;
	}
	
	public Speech(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	
	private void initialize() {
		mTts.setParameter(SpeechConstant.VOICE_NAME, Settings.VOICE_NAME);
		mTts.setParameter(SpeechConstant.SPEED, Settings.SPEED);
		mTts.setParameter(SpeechConstant.VOLUME, Settings.VOLUME);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setContentView();
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        initialize();
	}    
	
	public void speech(String str) {
		int code = mTts.startSpeaking(str, MyListener);
		if (code != ErrorCode.SUCCESS) {
			//”Ô“Ù∫œ≥… ß∞‹
		}
	}
	
	public void cancel() {
		mTts.stopSpeaking();
	}
	
	public void pause() {
		mTts.pauseSpeaking();
	}
	
	public void resume() {
		mTts.resumeSpeaking();
	}
	
	private void showTip(String str) {
		
	}
	
	private InitListener mTtsInitListener = new InitListener() {
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
        		showTip("≥ı ºªØ ß∞‹, ¥ÌŒÛ¬Î£∫" + code);
        	}		
		}
	};
	
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


