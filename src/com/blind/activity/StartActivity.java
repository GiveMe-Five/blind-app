package com.blind.activity;

import java.util.ArrayList;
import java.util.List;

import com.blind.R;
import com.blind.speech.Speech;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class StartActivity extends Activity {  
    private ViewPager mPager;
    private List<View> listViews; // Tab页面列表  
    private ImageView cursor;  
    private TextView t1, t2, t3;// 页卡头标  
    private int offset = 0;// 动画图片偏移量  
    private int currIndex = 0;// 当前页卡编号  
    private int bmpW;// 动画图片宽度  
    private Intent chatIntent;
    private Intent contactIntent;
    private Intent newsIntent;
    MyPagerAdapter adapter;  
    LayoutInflater mInflater;  
  
    RelativeLayout rel;  
  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);  
        
        // TODO
        newsIntent = new Intent(this, NewsActivity.class);
        contactIntent = new Intent(this, ContactActivity.class);
        
        
        initImageView();  
        initTextView();  
        initPageView();  
  
    }  
  
    private void initPageView() {  
        mInflater = getLayoutInflater();  
        listViews = new ArrayList<View>();  
        listViews.add(mInflater.inflate(R.layout.chat, null));  
        listViews.add(mInflater.inflate(R.layout.contact, null));  
        listViews.add(mInflater.inflate(R.layout.news, null));  
        adapter = new MyPagerAdapter(listViews);  
        mPager = (ViewPager) findViewById(R.id.page);  
        mPager.setAdapter(adapter);  
        mPager.setCurrentItem(0);  
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        speak("点击进入通讯界面");
    }  
  
    private void initTextView() {  
        t1 = (TextView) findViewById(R.id.tab1);  
        t2 = (TextView) findViewById(R.id.tab2);  
        t3 = (TextView) findViewById(R.id.tab3);  
        t1.setOnClickListener(new MyOnClickListener(0));  
        t2.setOnClickListener(new MyOnClickListener(1));  
        t3.setOnClickListener(new MyOnClickListener(2));  
    }  
  
    private void initImageView() {  
        cursor = (ImageView) findViewById(R.id.cursor);  
        rel = (RelativeLayout) findViewById(R.id.layout);  
          
  
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)  
                .getWidth();  
        DisplayMetrics dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        int screenW = dm.widthPixels;  
        offset = (screenW / 3 - bmpW) / 2;  

        cursor.setBackgroundResource(R.drawable.cursor); 
        rel.setPadding(offset, 0, 0, 0);  
  
    }  
  
    public class MyOnClickListener implements View.OnClickListener {  
        private int index = 0;  
  
        public MyOnClickListener(int i) {  
            index = i;  
        }  
  
        @Override  
        public void onClick(View v) {  
            mPager.setCurrentItem(index);  
        }  
    }  
  
    public class MyPagerAdapter extends PagerAdapter implements OnClickListener {  
        public List<View> mListViews;  
        public View v1;  
        public View v2;  
        public View v3;  
        public TextView tvChat;
        public TextView tvContact;
        public TextView tvNews;
  
        public MyPagerAdapter(List<View> mListViews) {  
            this.mListViews = mListViews;  
            getViewClickListener(mListViews);  
        }  
  
        public void getViewClickListener(List<View> listview) {  
            v1 = listview.get(0);  
            v2 = listview.get(1);  
            v3 = listview.get(2);  
            tvChat = (TextView) v1.findViewById(R.id.tvChat);
            tvContact = (TextView) v2.findViewById(R.id.tvContact);
            tvNews = (TextView) v3.findViewById(R.id.tvNews);
            
            tvChat.setOnClickListener(this);
            tvContact.setOnClickListener(this);
            tvNews.setOnClickListener(this);
        }  
  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            ((ViewPager) arg0).removeView(mListViews.get(arg1));  
        }  
  
        public void finishUpdate(View arg0) {  
        }  
  
        @Override  
        public int getCount() {  
            return mListViews.size();  
        }  
  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);  
            return mListViews.get(arg1);  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == (arg1);  
        }  
  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
        }  
  
        @Override  
        public Parcelable saveState() {  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
        }  
  
        @Override  
        public void onClick(View v) {
        	// TODO
        	switch (v.getId()) {
        	case R.id.tvChat:        		
        		contactIntent.putExtra("menustate", 1);
        		startActivity(contactIntent);
        		break;
        	case R.id.tvContact:
        		contactIntent.putExtra("menustate", 0);
        		startActivity(contactIntent);
        		break;
        	case R.id.tvNews:
        		startActivity(newsIntent);
        		break;
        	}
        }  
    }  
  
    public class MyOnPageChangeListener implements OnPageChangeListener {  
  
        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量  
        int two = one * 2;// 页卡1 -> 页卡3 偏移量  
  
        @Override  
        public void onPageSelected(int arg0) {  
            Animation animation = null;  
            switch (arg0) {  
            case 0:  
                if (currIndex == 1) {  
                    animation = new TranslateAnimation(one, 0, 0, 0);  
                } else if (currIndex == 2) {  
                    animation = new TranslateAnimation(two, 0, 0, 0);  
                }
                Toast.makeText(getBaseContext(), "1", Toast.LENGTH_SHORT).show();
                speak("点击进入通讯界面");
                break;  
            case 1:  
                if (currIndex == 0) {  
                    animation = new TranslateAnimation(offset, one, 0, 0);  
                } else if (currIndex == 2) {  
                    animation = new TranslateAnimation(two, one, 0, 0);  
                }
                Toast.makeText(getBaseContext(), "2", Toast.LENGTH_SHORT).show();
                speak("点击进入联系人界面");
                break;  
            case 2:  
                if (currIndex == 0) {  
                    animation = new TranslateAnimation(offset, two, 0, 0);  
                } else if (currIndex == 1) {  
                    animation = new TranslateAnimation(one, two, 0, 0);  
                }  
                Toast.makeText(getBaseContext(), "3", Toast.LENGTH_SHORT).show();
                speak("点击进入新闻界面");
                break;  
            }  
            currIndex = arg0;  
            animation.setFillAfter(true);  
            animation.setDuration(300);  
            rel.startAnimation(animation);  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
              
        }  
  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
              
        }  
    }  
	public void speak (String content) {
    	Speech speech = new Speech(StartActivity.this);
    	speech.show();
    	speech.speech(content);
    	speech.dismiss();
	}
}  