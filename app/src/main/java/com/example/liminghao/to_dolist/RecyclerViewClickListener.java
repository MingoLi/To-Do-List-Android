package com.example.liminghao.to_dolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by liminghao on 2017/8/1.
 */

public class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener {

    private int mLastDownX,mLastDownY;
    //该值记录了最小滑动距离
    private int touchSlop ;
    private OnItemClickListener mListener;
    //是否是单击事件
    private boolean isSingleTapUp = false;
    //是否是长按事件
    private boolean isLongPressUp = false;
    private boolean isMove = false;
    private long mDownTime;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public RecyclerViewClickListener(Context context, OnItemClickListener listener){
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (e.getAction()){

            case MotionEvent.ACTION_DOWN:
                mLastDownX = x;
                mLastDownY = y;
                mDownTime = System.currentTimeMillis();
                isMove = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if(Math.abs(x - mLastDownX)>touchSlop || Math.abs(y - mLastDownY)>touchSlop){
                    isMove = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if(isMove){
                    break;
                }
                if(System.currentTimeMillis()-mDownTime > 1000){
                    isLongPressUp = true;
                }else {
                    isSingleTapUp = true;
                }
                break;
        }
        if(isSingleTapUp ){
            //根据触摸坐标来获取childView
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            isSingleTapUp = false;
            if(childView != null){
                //回调mListener#onItemClick方法
                mListener.onItemClick(childView,rv.getChildLayoutPosition(childView));
                return true;
            }
            return false;
        }
        if (isLongPressUp ){
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            isLongPressUp = false;
            if(childView != null){
                mListener.onItemLongClick(childView, rv.getChildLayoutPosition(childView));
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

