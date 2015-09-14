package com.blind.news.utils;

import java.util.ArrayList;

import com.blind.R;




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class DateAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> lstDate;
	private TextView txtAge;
	private String[] data = {}; 

	public DateAdapter(Context mContext, ArrayList<String> list) {
		this.context = mContext;
		lstDate = list;
	}

	@Override
	public int getCount() {
		return lstDate.size();
	}

	@Override
	public Object getItem(int position) {
		return lstDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void exchange(int startPosition, int endPosition) {
		Object endObject = getItem(endPosition);
		Object startObject = getItem(startPosition);
		lstDate.add(startPosition, (String) endObject);
		lstDate.remove(startPosition + 1);
		lstDate.add(endPosition, (String) startObject);
		lstDate.remove(endPosition + 1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.activity_news_item, null);
		txtAge = (TextView) convertView.findViewById(R.id.txt_userAge);
		if(lstDate.get(position)==null){
//			txtAge.setText("+");
//			txtAge.setBackgroundResource(R.drawable.news_item_green);
		}
		else if(lstDate.get(position).equals("none")){
			txtAge.setText("");
			txtAge.setBackgroundDrawable(null);
		}else {
			int num = Integer.parseInt(lstDate.get(position));
			if (num % 2 != 0)
				txtAge.setBackgroundResource(R.drawable.news_item_green);
			
			txtAge.setText(data[num]);
		}
		return convertView;
	}
	
	public void setData (String[] data) {
		this.data = data;
	}

}
