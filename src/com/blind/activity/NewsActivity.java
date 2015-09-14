package com.blind.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.blind.R;
import com.blind.news.utils.Configure;
import com.blind.news.utils.DateAdapter;
import com.blind.news.utils.DragGrid;
import com.blind.news.utils.HttpDownloader;
import com.blind.news.utils.NetUtil;
import com.blind.news.utils.ScrollLayout;
import com.blind.news.utils.UrlUtil;
import com.blind.speech.Speech;
import com.iflytek.cloud.SpeechUtility;


public class NewsActivity extends Activity {
	
    private String iname;
    private ProgressDialog progressDialog;
    private MyHandler mHandler;
    private Thread thread;
    private ListView list;
    private ArrayList<HashMap<String,Object>> listItem;
    private SimpleAdapter listItemAdapter;
    private String url;
    private String[] urls;
    private DateAdapter dateAdapter;
    private String[] data;
    private Speech speech;
    
    private double mLastTime, mCurTime;

	/** GridView. */
	private LinearLayout linear;
	private RelativeLayout relate;
	private DragGrid gridView;
	private ScrollLayout lst_views;
	TextView tv_page;// int oldPage=1;
	private ImageView runImage, delImage;
	LinearLayout.LayoutParams param;

	TranslateAnimation left, right;
	Animation up, down;

	public static final int PAGE_SIZE = 4;
	ArrayList<DragGrid> gridviews = new ArrayList<DragGrid>();

	ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();// 全部数据的集合集lists.size()==countpage;
	ArrayList<String> lstDate = new ArrayList<String>();// 每一页的数据

	SensorManager sm;
	SensorEventListener lsn;
	boolean isClean = false;
	Vibrator vibrator;
	int rockCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_main);
		
		/* Speech */
    	
    	
        iname = "sina";
        url = "http://rss.sina.com.cn/news/marquee/ddt.xml";


        if (NetUtil.NetWorkStatus(this)){
            progressDialog = ProgressDialog.show(NewsActivity.this,null,"获取数据中……", true);
            thread = new MyThread();
            thread.start();
        }
        
	}
	
	
    private void LoadView(ArrayList<?> resultList){


        if (resultList.size()>0){
            String name = (String)resultList.get(0);
            Log.e("content",name);
            String[] names = name.split(",");
            data = names;

            String url = (String)resultList.get(1);
            urls = url.split(",");
            
            
    		setView(names.length);
    		initView();
    		initData();
            
        }
    }
	
	
	
	public void setView(int num) {		
		
		// set the number of item
		for (int i = 0; i < num; i++) {
			lstDate.add("" + i);
		}

	}

	public void initView() {
		relate = (RelativeLayout) findViewById(R.id.relate);
		lst_views = (ScrollLayout) findViewById(R.id.views);
		tv_page = (TextView) findViewById(R.id.tv_page);
		tv_page.setText("1");
		Configure.init(NewsActivity.this);
		param = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		param.rightMargin = 20;
		param.leftMargin = 20;
		if (gridView != null) {
			lst_views.removeAllViews();
		}

		initData();

		for (int i = 0; i < Configure.countPages; i++) {
			lst_views.addView(addGridView(i));
		}

		lst_views.setPageListener(new ScrollLayout.PageListener() {
			@Override
			public void page(int page) {
				setCurPage(page);
			}
		});

		runImage = (ImageView) findViewById(R.id.run_image);
		runAnimation();
		delImage = (ImageView) findViewById(R.id.dels);
		relate.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				System.out.println("LongClick");
				return false;
			}
		});
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		lsn = new SensorEventListener() {
			public void onSensorChanged(SensorEvent e) {
				if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					if (!isClean && rockCount >= 10) {
						isClean = true;
						rockCount = 0;
						vibrator.vibrate(100);
						CleanItems();
						return;
					}
					float newX = e.values[SensorManager.DATA_X];
					float newY = e.values[SensorManager.DATA_Y];
					float newZ = e.values[SensorManager.DATA_Z];
					
					if ((newX >= 18 || newY >= 20 || newZ >= 20)
							&& rockCount % 2 == 0) {
						rockCount++;
						return;
					}
					if ((newX <= -18 || newY <= -20 || newZ <= -20)
							&& rockCount % 2 == 1) {
						rockCount++;
						return;
					}

				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};

		sm.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
	}


	public void initData() {
		Configure.countPages = (int) Math.ceil(lstDate.size()
				/ (float) PAGE_SIZE);

		lists = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < Configure.countPages; i++) {
			lists.add(new ArrayList<String>());
			for (int j = PAGE_SIZE * i; j < (PAGE_SIZE * (i + 1) > lstDate
					.size() ? lstDate.size() : PAGE_SIZE * (i + 1)); j++)
				lists.get(i).add(lstDate.get(j));
		}
		boolean isLast = true;
		for (int i = lists.get(Configure.countPages - 1).size(); i < PAGE_SIZE; i++) {
			if (isLast) {
//				lists.get(Configure.countPages - 1).add(null);
				isLast = false;
			} else
				lists.get(Configure.countPages - 1).add("none");
		}
	}

	public void CleanItems() {
		lstDate = new ArrayList<String>();
		for (int i = 0; i < lists.size(); i++) {
			for (int j = 0; j < lists.get(i).size(); j++) {
				if (lists.get(i).get(j) != null
						&& !lists.get(i).get(j).equals("none")) {
					lstDate.add(lists.get(i).get(j).toString());
					System.out.println("-->" + lists.get(i).get(j).toString());
				}
			}
		}
		System.out.println(lstDate.size());
		initData();
		lst_views.removeAllViews();
		gridviews = new ArrayList<DragGrid>();
		for (int i = 0; i < Configure.countPages; i++) {
			lst_views.addView(addGridView(i));
		}
		isClean = false;
		lst_views.snapToScreen(0);
	}

	public int getFristNonePosition(ArrayList<String> array) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) != null && array.get(i).toString().equals("none")) {
				return i;
			}
		}
		return -1;
	}

	public int getFristNullPosition(ArrayList<String> array) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) == null) {
				return i;
			}
		}
		return -1;
	}

	public LinearLayout addGridView(int i) {

		linear = new LinearLayout(NewsActivity.this);
		gridView = new DragGrid(NewsActivity.this);
		dateAdapter = new DateAdapter(NewsActivity.this, lists
				.get(i));
		dateAdapter.setData(data);
		gridView.setAdapter(dateAdapter);
		gridView.setNumColumns(1);
		gridView.setHorizontalSpacing(0);
		gridView.setVerticalSpacing(0);
		final int ii = i;
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
                mLastTime = mCurTime;  
                mCurTime = System.currentTimeMillis();  
                int pos = Integer.parseInt(lists.get(ii).get(arg2));
                if (mCurTime - mLastTime < 300) {  
                     Toast.makeText(getBaseContext(),  
                     "双击事件", Toast.LENGTH_SHORT).show(); 
                } else {
                    speak(data[pos]);
                }
			}
		});
		gridView.setSelector(R.anim.news_grid_light);
		gridView.setPageListener(new DragGrid.G_PageListener() {
			@Override
			public void page(int cases, int page) {
				switch (cases) {
				case 0:// 滑动页面
					lst_views.snapToScreen(page);
					setCurPage(page);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Configure.isChangingPage = false;
						}
					}, 800);
					break;
				case 1:// 删除按钮上来
					delImage.setBackgroundResource(R.drawable.news_item_del);
					delImage.setVisibility(0);
					delImage.startAnimation(up);
					break;
				case 2:// 删除按钮变深
					delImage.setBackgroundResource(R.drawable.news_item_del_check);
					Configure.isDelDark = true;
					break;
				case 3:// 删除按钮变淡
					delImage.setBackgroundResource(R.drawable.news_item_del);
					Configure.isDelDark = false;
					break;
				case 4:// 删除按钮下去
					delImage.startAnimation(down);
					break;
				case 5:// 松手动作
					delImage.startAnimation(down);
					// Configure.isDelRunning = false;
					lists.get(Configure.curentPage).add(Configure.removeItem,
							null);
					lists.get(Configure.curentPage).remove(
							Configure.removeItem + 1);
					((DateAdapter) ((gridviews.get(Configure.curentPage))
							.getAdapter())).notifyDataSetChanged();
					break;
				}
			}
		});
		gridView.setOnItemChangeListener(new DragGrid.G_ItemChangeListener() {
			@Override
			public void change(int from, int to, int count) {
				String toString = (String) lists.get(
						Configure.curentPage - count).get(from);

				lists.get(Configure.curentPage - count).add(from,
						(String) lists.get(Configure.curentPage).get(to));
				lists.get(Configure.curentPage - count).remove(from + 1);
				lists.get(Configure.curentPage).add(to, toString);
				lists.get(Configure.curentPage).remove(to + 1);

				((DateAdapter) ((gridviews.get(Configure.curentPage - count))
						.getAdapter())).notifyDataSetChanged();
				((DateAdapter) ((gridviews.get(Configure.curentPage))
						.getAdapter())).notifyDataSetChanged();
			}
		});
		gridviews.add(gridView);
		linear.addView(gridviews.get(i), param);
		return linear;
	}

	public void runAnimation() {
		down = AnimationUtils.loadAnimation(NewsActivity.this,
				R.anim.news_del_down);
		up = AnimationUtils.loadAnimation(NewsActivity.this,
				R.anim.news_del_up);
		down.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				delImage.setVisibility(8);
			}
		});

		right = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f);
		left = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 0f);
		right.setDuration(25000);
		left.setDuration(25000);
		right.setFillAfter(true);
		left.setFillAfter(true);

		right.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				runImage.startAnimation(left);
			}
		});
		left.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				
				runImage.startAnimation(right);
			}
		});
		runImage.startAnimation(right);
	}

	public void setCurPage(final int page) {
		Animation a = AnimationUtils.loadAnimation(NewsActivity.this,
				R.anim.news_scale_in);
		a.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_page.setText((page + 1) + "");
				tv_page.startAnimation(AnimationUtils
						.loadAnimation(NewsActivity.this,
								R.anim.news_scale_out));
			}
		});
		tv_page.startAnimation(a);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sm.unregisterListener(lsn);
		System.out.println("dismiss");
		speak("");
		
	}

	public void speak (String content) {
		speech = new Speech(NewsActivity.this);
    	speech.show();
    	speech.speech(content);
    	speech.dismiss();
	}
	
	
    /**
     *主线程
     * **/
    private class MyHandler extends Handler{
        public MyHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //message handler
            System.out.print("MyHandler--->"+Thread.currentThread().getId());
            LoadView((ArrayList<?>) msg.obj);
        }
    }



    /**
     * 子线程
     * */
    private class MyThread extends Thread{
        @Override
        public void run() {
            System.out.print("MyThread--->"+Thread.currentThread().getId());
            Looper curlooper = Looper.myLooper();
            Looper mainlooper = Looper.getMainLooper();

            if (curlooper == null){
                mHandler = new MyHandler(mainlooper);
            }else{
                mHandler = new MyHandler(curlooper);
            }

            ArrayList<?> resultList = new ArrayList<Object>();
            try{
                /*get data*/
                HttpDownloader hd = new HttpDownloader();
                String html = hd.download(url,"UTF-8",false);

                resultList = UrlUtil.getSinaNews(html);

                mHandler.removeMessages(0);
                Message m = mHandler.obtainMessage(1, 1, 1, resultList);
                mHandler.sendMessage(m);

                progressDialog.dismiss();

            }catch (Exception e){

            }
        }
    }
    
}
