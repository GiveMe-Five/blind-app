package com.blind.news.utils;

import java.util.List;

import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class RecognitionDialog extends Dialog {
	private Context context;
	private static VoiceRecognitionClient RecognitionClient;
	private boolean isRecognition = false;
	private CallBackListener mListener = new CallBackListener();
	private String recognized_string = "";
	VoiceRecognitionConfig config = new VoiceRecognitionConfig();

	public RecognitionDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecognitionClient = VoiceRecognitionClient.getInstance(context.getApplicationContext());
        RecognitionClient.setTokenApis(Constants.API_KEY, Constants.SECRET_KEY);
        initializeConfigurations();
        //this.setContentView(R.layout.recognition_dialog);
    }
	
	private void initializeConfigurations() {
		this.config.setLanguage("LANGUAGE_CHINESE");
		//this.config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); ����ǿ���ѡ���
		
		//�������ѡ���Ƿ��ڿ�ʼʶ��ͽ���ʶ��ʱ������ʾ��������ä����˵������Ӧ���Ǳ�Ҫ��
		if (Config.PLAY_START_SOUND) {
            //this.config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start);
        }
        if (Config.PLAY_END_SOUND) {
            //this.config.enableEndSoundEffect(R.raw.bdspeech_speech_end);
        }
        //����ֱ�Ӳ��ðٶ��ṩ���ֳɵ���ʾ��
        this.config.enableVoicePower(Config.ENABLE_VOICE_POWER);
	}
	
	public String recognize() {
		recognized_string = "";
		int code = RecognitionClient.startVoiceRecognition(new CallBackListener(), config);
		if (code == VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
			isRecognition = true;
			//����ʶ��ɹ�����
		} else {
			//��Ϊ����ԭ������ʧ����
		}
		while (isRecognition) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		isRecognition = false;
		return recognized_string;
	}
	
	public void finishRecognization() {
		//������������ʱ������Ҫͨ����һ���ֶ�ֹͣ����ʶ��
		RecognitionClient.speakFinish();
	}
	
	public void cancelRecognization() {
		//�û��ֶ�ȡ������ʶ��
		RecognitionClient.stopVoiceRecognition();
	}
	
	public void release() {
		//�ڽ���ʶ�����ô˷����Է�ֹй©
		VoiceRecognitionClient.releaseInstance();
	}
	
	public String chat() {
		return "";
	}
	
	class CallBackListener implements VoiceClientStatusChangeListener {

        public void onClientStatusChange(int status, Object obj) {
            switch (status) {
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING://�����ʼ���������ʱ��ͻᴥ���������
                    isRecognition = true;//����������������ʶ����
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START: //��⵽�û�˵�ĵ�һ���ֵ�ʱ���
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END://ʶ������û��Ѿ�˵����
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH://��ʱ���Ѿ���������ˣ����ص�obj���������Ͼ���ʶ�������������
                    isRecognition = false;
                    processResult(obj);
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS://�м䷵�ؽ����ڳ���䣬��һ���ᱻ��ε���
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED://�û��ֶ������ȡ��
                    isRecognition = false;
                    break;
                default:
                    break;
            }

        }

        public void onError(int errorType, int errorCode) {
            isRecognition = false;
        }

        public void onNetworkStatusChange(int status, Object obj) {
        	//����ģʽ��WiFi������֮���л���ʱ�������Ҫ���ã�һ����˵������Ҫ�������
        }
        
    }
	
	private void processResult(Object result) {
		if (result != null && result instanceof List) {
            List results = (List) result;
            if (results.size() > 0) {
                if (results.get(0) instanceof List) {
                    List<List<Candidate>> sentences = (List<List<Candidate>>) result;
                    StringBuffer sb = new StringBuffer();
                    for (List<Candidate> candidates : sentences) {
                        if (candidates != null && candidates.size() > 0) {
                            sb.append(candidates.get(0).getWord());
                        }
                    }
                    recognized_string = sb.toString();
                } else {
                    recognized_string = results.get(0).toString();
                }
            }
        }
	}
	
}

class Constants {
	public static final String API_KEY = "6XIt9Xou3K2RHvm90G2Oj9xw";
	public static final String SECRET_KEY = "eyT74b3YXtAAGaobu0b0kDCqZaf64LGi";
}

class Config {
	public static final boolean PLAY_START_SOUND = true;
	public static final boolean PLAY_END_SOUND = true;
	public static final boolean ENABLE_VOICE_POWER = true;
}
