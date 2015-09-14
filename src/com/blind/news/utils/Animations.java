package com.blind.news.utils;





import com.blind.R;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class Animations{
	Animation DelDown,DelUp;
	public Animation getDownAnimation(Context context){
		return AnimationUtils.loadAnimation(context, R.anim.news_del_down);
	}
}