package com.photobomb;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ArenaScreen extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_screen);


        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        pager.setAdapter(new BattleAdapter(getSupportFragmentManager()));
//pager.setOffscreenPageLimit(9);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

class ZoomOutPageTransformer implements  ViewPager.PageTransformer{

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }





}


    class BattleAdapter extends FragmentPagerAdapter{

        List<Fragment> mFragments;


public BattleAdapter(android.support.v4.app.FragmentManager mgr){
    super(mgr);
    mFragments = new ArrayList<Fragment>();
    for(int i = 0; i < 10; i++){
        mFragments.add(BattleFragment.newInstance(i));


    }



}

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Fragment getItem(int position) {

    if(mFragments == null){
        return null;
    }
    return  mFragments.get(position);


        }
    }
}
