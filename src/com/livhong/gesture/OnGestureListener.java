package com.livhong.gesture;

import android.view.MotionEvent;

public interface OnGestureListener {

	public boolean onDown(MotionEvent ev);
	
	public boolean onDoubleClick(MotionEvent ev);
	
	public boolean onLongPress(MotionEvent ev);
	
	public boolean onSlipUp();
	
	public boolean onSlipDown();
	
	public boolean onSlipLeft();
	
	public boolean onSlipRight();
	
	public boolean onSlipDoubleUp();
	
	public boolean onSlipDoubleDown();
	
	public boolean onSlipDoubleLeft();
	
	public boolean onSlipDoubleRight();
	
	public boolean onSlipFastUp();
	
	public boolean onSlipFastDown();
}
