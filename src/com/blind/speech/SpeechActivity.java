package com.blind.speech;

import com.blind.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class SpeechActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t);
        boolean con = true;
        if (con) {
        	Speech speech = new Speech(this);
        	speech.show();
        	speech.speech("我阿萨德飞阿萨德发送地方暗杀测试测试测试");
        	speech.dismiss();
        	VoiceVerifier vv = new VoiceVerifier();
        	
        	
        }
        
        
    }



}
