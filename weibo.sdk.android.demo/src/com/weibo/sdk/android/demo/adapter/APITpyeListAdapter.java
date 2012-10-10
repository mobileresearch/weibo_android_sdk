package com.weibo.sdk.android.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weibo.sdk.android.demo.R;

public class APITpyeListAdapter extends BaseAdapter {
	private String[] strArr;
	private Context context;

	public APITpyeListAdapter(Context context, String[] strArr) {
		this.strArr = strArr;
		this.context = context;
	}

	@Override
	public int getCount() {
		return strArr.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return strArr[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = null;
		if (null == convertView) {
			text = (TextView) LayoutInflater.from(context).inflate(R.layout.activity_typelist_item, null);
		} else {
			text = (TextView) convertView;
		}
		text.setText(strArr[position]);
		return text;
	}

}
