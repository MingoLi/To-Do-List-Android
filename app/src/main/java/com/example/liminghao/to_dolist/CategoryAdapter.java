package com.example.liminghao.to_dolist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by liminghao on 2017/7/31.
 */

public class CategoryAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public CategoryAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TaskFragment();
            case 1:
                return new NoteFragment();
            default:
                throw new UnsupportedOperationException("Error in CategoryAdapter");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch( position ) {
//            case 0: return "Tasks";
//            case 1: return "Notes";
            case 0: return "";
            case 1: return "";
            default: throw new UnsupportedOperationException("Error in getting page title");
        }
    }


}
