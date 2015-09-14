package com.blind.speech;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.iflytek.cloud.*;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Bundle;

public class VoiceVerifier extends Service {
	private static final int PWD_TYPE_TEXT = 1;
	private static final int PWD_TYPE_FREE = 2;
	private static final int PWD_TYPE_NUM = 3;
	//�����������ͣ�1��2��3�ֱ�Ϊ�ı�������˵����������
	private int pwdType = PWD_TYPE_NUM;
	private SpeakerVerifier mVerify;
	private String mAuthId = "";
	private String mTextPwd = "";
	private String mNumPwd = "";
	//������������Σ�Ĭ����5��
	private String[] mNumPwdSegs;
	
	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		VoiceVerifier getService() {
			return VoiceVerifier.this;
		}
	}
	
	public void register(String str) {
		this.mAuthId = str;//������ַ��û���
		
		//��ȡ����
		mVerify.setParameter(SpeechConstant.PARAMS, null);
		mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
		mVerify.getPasswordList(mPwdListener);
		
		mVerify.setParameter(SpeechConstant.PARAMS, null);
		//�ⲿ����
		//mVerify.setParameter(SpeechConstant.ISV_AUDIOPATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
		//��¼���������봦��
		//mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
		
		//ע�Ტѵ��
		mVerify.setParameter(SpeechConstant.ISV_PWD, mNumPwd);
		mVerify.setParameter(SpeechConstant.ISV_AUTHID, mAuthId);
		mVerify.setParameter(SpeechConstant.ISV_SST, "train");
		mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
		mVerify.startListening(mRegisterListener);
	}
	
	public void verify(String str) {
		if (str.equals(this.mAuthId)) {
			
		} else {
			
		}
		mVerify.setParameter(SpeechConstant.PARAMS, null);
		//�ⲿ����
		//mVerify.setParameter(SpeechConstant.ISV_AUDIOPATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
		mVerify = SpeakerVerifier.getVerifier();
		mVerify.setParameter(SpeechConstant.ISV_SST, "verify");
		//��¼���������봦��
		//mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
		String verifyPwd = mVerify.generatePassword(8);
		mVerify.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
		showTag("�������" + verifyPwd);
		mVerify.setParameter(SpeechConstant.ISV_AUTHID, str);
		mVerify.setParameter(SpeechConstant.ISV_PWDT, "" + pwdType);
		//������֤
		mVerify.startListening(mVerifyListener);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	VerifierListener mVerifyListener = new VerifierListener() {

		@Override
		public void onVolumeChanged(int volume) {
			showTip("��ǰ����˵����������С��" + volume);
		}

		@Override
		public void onResult(VerifierResult result) {
			showTag(result.source);
			
			if (result.ret == 0) {
				// ��֤ͨ��
				showTag("��֤ͨ��");
			}
			else{
				// ��֤��ͨ��
				switch (result.err) {
				case VerifierResult.MSS_ERROR_IVP_GENERAL:
					showTag("�ں��쳣");
					break;
				case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
					showTag("���ֽط�");
					break;
				case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
					showTag("̫������");
					break;
				case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
					showTag("¼��̫��");
					break;
				case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
					showTag("��֤��ͨ����������ı���һ��");
					break;
				case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
					showTag("����̫��");
					break;
				case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
					showTag("��Ƶ���ﲻ������˵��Ҫ��");
					break;
				default:
					showTag("��֤��ͨ��");
					break;
				}
			}
		}
		
		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onError(SpeechError error) {
			switch (error.getErrorCode()) {
			case ErrorCode.MSP_ERROR_NOT_FOUND:
				showTag("ģ�Ͳ����ڣ�����ע��");
				break;

			default:
				showTip("onError Code��"	+ error.getErrorCode());
				break;
			}
		}

		@Override
		public void onEndOfSpeech() {
			showTip("����˵��");
		}

		@Override
		public void onBeginOfSpeech() {
			showTip("��ʼ˵��");
		}
	};
	
	VerifierListener mRegisterListener =new VerifierListener() {

		@Override
		public void onVolumeChanged(int volume) {
			showTip("��ǰ����˵����������С��" + volume);
		}

		@Override
		public void onResult(VerifierResult result) {
			if (result.ret == ErrorCode.SUCCESS) {
				switch (result.err) {
				case VerifierResult.MSS_ERROR_IVP_GENERAL:
					//mShowMsgTextView.setTextshowTagbreak;
				case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
					showTag("ѵ���ﵽ������");
					break;
				case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
					showTag("���ֽط�");
					break;
				case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
					showTag("̫������");
					break;
				case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
					showTag("¼��̫��");
					break;
				case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
					showTag("ѵ��ʧ�ܣ���������ı���һ��");
					break;
				case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
					showTag("����̫��");
					break;
				case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
					showTag("��Ƶ���ﲻ������˵��Ҫ��");
				default:
					showTag("");
					break;
				}
				
				if (result.suc == result.rgn) {
					showTag("ע��ɹ�");
					
					if (PWD_TYPE_TEXT == pwdType) {
						showTag("����ı���������ID��\n" + result.vid);
					} else if (PWD_TYPE_FREE == pwdType) {
						showTag("�������˵����ID��\n" + result.vid);
					} else if (PWD_TYPE_NUM == pwdType) {
						showTag("���������������ID��\n" + result.vid);
					}
					
				} else {
					int nowTimes = result.suc + 1;
					int leftTimes = result.rgn - nowTimes;
					
					if (PWD_TYPE_TEXT == pwdType) {
					} else if (PWD_TYPE_NUM == pwdType) {
						showTag("�������" + mNumPwdSegs[nowTimes - 1]);
					}
					
					showTag("ѵ�� ��" + nowTimes + "�Σ�ʣ��" + leftTimes + "��");
				}

			}else {
				
				showTag("ע��ʧ�ܣ������¿�ʼ��");	
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			
		}

		@Override
		public void onError(SpeechError error) {
			
			if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST) {
				showTip("���û��Ѵ���");
			} else {
				showTip("onError Code��" + error.getErrorCode());
			}
		}

		@Override
		public void onEndOfSpeech() {
			showTip("����˵��");
		}

		@Override
		public void onBeginOfSpeech() {
			showTip("��ʼ˵��");
		}
	};
	
	SpeechListener mPwdListener = new SpeechListener() {
		@Override
		public void onEvent(int eventType, Bundle params) {
			
		}
		
		@Override
		public void onBufferReceived(byte[] buffer) {
			String result = new String(buffer);
			switch (pwdType) {
			case PWD_TYPE_TEXT:
				break;
			case PWD_TYPE_NUM:
				StringBuffer numberString = new StringBuffer();
				try {
					JSONObject object = new JSONObject(result);
					if (!object.has("num_pwd")) {
						return;
					}
					JSONArray pwdArray = object.optJSONArray("num_pwd");
					numberString.append(pwdArray.get(0));
					for (int i = 1; i < pwdArray.length(); i++) {
						numberString.append("-" + pwdArray.get(i));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mNumPwd = numberString.toString();
				mNumPwdSegs = mNumPwd.split("-");
				break;
			default:
				break;
			}

		}

		@Override
		public void onCompleted(SpeechError error) {
			if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
				showTip("��ȡʧ�ܣ�" + error.getErrorCode());
			}
		}
	};	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mVerify = SpeakerVerifier.createVerifier(this, new InitListener() {
			@Override
			public void onInit(int errorCode) {
				if (ErrorCode.SUCCESS == errorCode) {
					showTip("�����ʼ���ɹ�");
				} else {
					showTip("�����ʼ��ʧ�ܣ������룺" + errorCode);
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void showTip(String str) {
		Speech speech = new Speech(this);
		speech.show();
		speech.speech(str);
	}
	
	private void showTag(String str) {
		
	}

}
