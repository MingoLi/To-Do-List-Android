package com.example.liminghao.to_dolist;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eftimoff.viewpagertransformers.FlipHorizontalTransformer;

public class MainActivity extends AppCompatActivity /*implements LoaderManager.LoaderCallbacks<Cursor>*/{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        CategoryAdapter cAdapter = new CategoryAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(cAdapter);

        // TODO 动画效果有问题
//        viewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        // another type of animation
//        viewPager.setPageTransformer(true, new ZoomInTransformer());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    // TODO delete
    // TODO forbid land layout
    // TODO add instruction for slide delete
    // TODO localization
    // TODO persona 5 theme
}
