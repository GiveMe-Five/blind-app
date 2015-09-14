package com.livhong.gesture;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Toast;

public class MyGestureDetector {

	
    private int mTouchSlopSquare;
    private int mDoubleTapSlopSquare;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;
    
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
//    private static final int DOUBLE_SLIP_TIMEOUT = LONGPRESS_TIMEOUT;
    
    private static final int LONG_PRESS = 2;
    private static final int TAP = 3;
    
    private static final int SINGLE_CLICK = 5;
    
    private Handler mHandler;
    private OnGestureListener mListener;
    
    private boolean mInLongPress;

    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;
    
    private boolean mIsDoubleTapping;
    private boolean mIsSlip;

    
    private VelocityTracker mVelocityTracker;
    
    
    private class GestureHandler extends Handler{
    	
    	@Override
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case LONG_PRESS:
    			dispatchLongPress();
    			break;
    		case SINGLE_CLICK:
    			dispatchSingleClick();
    			break;
    		default:
    			break;
    		}
    	}
    	
    }
    
    public MyGestureDetector(Context context, OnGestureListener listener){
    	
    	mHandler = new GestureHandler();
    	mListener = listener;
    	this.context = context;
    	
    	init(context);
    }
    
    private void init(Context context) {
        if (mListener == null) {
            throw new NullPointerException("OnGestureListener must not be null");
        }

        // Fallback to support pre-donuts releases
        int touchSlop = 0, doubleTapSlop = 0;
        if (context == null) {
        	throw new NullPointerException("Context must not be null");
        } else {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            touchSlop = configuration.getScaledTouchSlop();
            doubleTapSlop = configuration.getScaledDoubleTapSlop();
            mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
            mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        }
        mTouchSlopSquare = touchSlop * touchSlop;
        mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }
    
    public boolean onTouchEvent(MotionEvent ev){
    	
    	final int action = ev.getAction();
    	final float y = ev.getY();
    	final float x = ev.getX();
    	
    	if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        boolean handled = false;
        
        switch(action & MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_POINTER_DOWN:
        	mHandler.removeMessages(LONG_PRESS);
            mHandler.removeMessages(TAP);
        	break;
        case MotionEvent.ACTION_POINTER_UP:
        	
        	final int primaryX = (int) (x - mCurrentDownEvent.getX());
            final int primaryY = (int) (y - mCurrentDownEvent.getY());
            final int primaryDistance = primaryX * primaryX + primaryY * primaryY;
//            System.out.println("primaryDistance is "+primaryDistance);
//            System.out.println("mTouchSlopSquare is "+mTouchSlopSquare);
        	
        	int pointerId = 1;
        	final VelocityTracker secondVelocityTracker = mVelocityTracker;
        	secondVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
        	final float firstVelocityX = secondVelocityTracker.getXVelocity();
        	final float firstVelocityY = secondVelocityTracker.getYVelocity();
            final float secondVelocityY = secondVelocityTracker.getYVelocity(pointerId);
            final float secondVelocityX = secondVelocityTracker.getXVelocity(pointerId);
            if(primaryDistance < mTouchSlopSquare){
                if(Math.abs(secondVelocityY) > mMinimumFlingVelocity){
                	if(secondVelocityY > 0){
                		mListener.onSlipFastDown();
                	}else{
                		mListener.onSlipFastUp();
                	}
                	mIsSlip = true;
                }
            }else if(Math.abs(firstVelocityX)>Math.abs(firstVelocityY)&&Math.abs(secondVelocityX)>Math.abs(secondVelocityY)){
            	if((firstVelocityX>mMinimumFlingVelocity)&&(secondVelocityX>mMinimumFlingVelocity)){
            		mListener.onSlipDoubleRight();
                	mIsSlip = true;
                }else if((firstVelocityX<-mMinimumFlingVelocity)&&(secondVelocityX<-mMinimumFlingVelocity)){
                	mListener.onSlipDoubleLeft();
                	mIsSlip = true;
                }
            }else if(Math.abs(firstVelocityX)<=Math.abs(firstVelocityY)&&Math.abs(secondVelocityX)<=Math.abs(secondVelocityY)){
            	if((firstVelocityY>mMinimumFlingVelocity)&&(secondVelocityY>mMinimumFlingVelocity)){
            		mListener.onSlipDoubleDown();
                	mIsSlip = true;
                }else if((firstVelocityY<-mMinimumFlingVelocity)&&(secondVelocityY<-mMinimumFlingVelocity)){
                	mListener.onSlipDoubleUp();
                	mIsSlip = true;
                }
            }
            
        	break;
        case MotionEvent.ACTION_DOWN:
        	
        	boolean hadTapMessage = mHandler.hasMessages(TAP);
        	if(hadTapMessage) mHandler.removeMessages(TAP);
        	if ((mCurrentDownEvent != null) && (mPreviousUpEvent != null) && hadTapMessage &&
                    isConsideredDoubleTap(mCurrentDownEvent, mPreviousUpEvent, ev)){
        		// This is a second tap
                mIsDoubleTapping = true;
                mHandler.removeMessages(SINGLE_CLICK);
        		//handled |= mListener.onDoubleClick(ev);
        	} else {
        		mHandler.sendEmptyMessageDelayed(TAP, DOUBLE_TAP_TIMEOUT);
        	}
        	
        	if(mCurrentDownEvent != null){
        		mCurrentDownEvent.recycle();
        	}
        	mCurrentDownEvent = MotionEvent.obtain(ev);
        	mInLongPress = false;
        	
        	mHandler.removeMessages(LONG_PRESS);
        	if(!mIsDoubleTapping)
        		mHandler.sendEmptyMessageAtTime(LONG_PRESS, mCurrentDownEvent.getDownTime()
                    + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
            
            break;
        case MotionEvent.ACTION_MOVE:
        	final int dX = (int) (x - mCurrentDownEvent.getX());
            final int dY = (int) (y - mCurrentDownEvent.getY());
            int dis = (dX * dX) + (dY * dY);
            if(dis > mTouchSlopSquare){
            	mHandler.removeMessages(LONG_PRESS);
            }
            break;
        case MotionEvent.ACTION_UP:
        	if(mIsSlip){
        		mIsSlip = false;
        		break;
        	}
            MotionEvent currentUpEvent = MotionEvent.obtain(ev);
            
            final int deltaX = (int) (x - mCurrentDownEvent.getX());
            final int deltaY = (int) (y - mCurrentDownEvent.getY());
            int distance = (deltaX * deltaX) + (deltaY * deltaY);
            final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
            final float velocityY = velocityTracker.getYVelocity();
            final float velocityX = velocityTracker.getXVelocity();
            
            if (mIsDoubleTapping) {
                // Finally, give the up event of the double-tap
                handled |= mListener.onDoubleClick(ev);
            } else {

				if ((distance > mTouchSlopSquare) && (Math.abs(velocityY) > mMinimumFlingVelocity)
                        || (Math.abs(velocityX) > mMinimumFlingVelocity)) {
					
					if (Math.abs(velocityX) > Math.abs(velocityY)) {
						if (velocityX > 0) {
							mListener.onSlipRight();
						} else {
							mListener.onSlipLeft();
						}
					} else {
						if (velocityY > 0) {
							mListener.onSlipDown();
						} else {
							mListener.onSlipUp();
						}
					}
					mHandler.removeMessages(TAP);
				} else if(distance <= mTouchSlopSquare){
					//single click
//onDown
					if(!mInLongPress)
//						mListener.onDown(ev);
						mHandler.sendEmptyMessageDelayed(SINGLE_CLICK, DOUBLE_TAP_TIMEOUT);
					else {
			            mHandler.removeMessages(TAP);
			            mInLongPress = false;
			        }
				}

			}
            
            if (mPreviousUpEvent != null) {
                mPreviousUpEvent.recycle();
            }
            
         // Hold the event we obtained above - listeners may have changed the original.
            mPreviousUpEvent = currentUpEvent;
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mIsDoubleTapping = false;
            //mIsSingleTapping = false;
            mHandler.removeMessages(LONG_PRESS);
            break;
        case MotionEvent.ACTION_CANCEL:
            cancel();
            break;
        }
    	
    	return handled;
    }
    
    private void cancel() {
        mHandler.removeMessages(LONG_PRESS);
        mHandler.removeMessages(TAP);
        mVelocityTracker.recycle();
        mVelocityTracker = null;
        mIsDoubleTapping = false;
        if (mInLongPress) {
            mInLongPress = false;
        }
    }
    
    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp,
            MotionEvent secondDown) {

        if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }

        int deltaX = (int) firstDown.getX() - (int) secondDown.getX();
        int deltaY = (int) firstDown.getY() - (int) secondDown.getY();
        return (deltaX * deltaX + deltaY * deltaY < mDoubleTapSlopSquare);
    }
    
    private void dispatchLongPress() {
        mHandler.removeMessages(TAP);
        mInLongPress = true;
        mListener.onLongPress(mCurrentDownEvent);
    }
    
    private void dispatchSingleClick(){
    	mListener.onDown(mCurrentDownEvent);
    }
    
    private Context context;
    private void showMessage(String string){
    	Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }
    
}
