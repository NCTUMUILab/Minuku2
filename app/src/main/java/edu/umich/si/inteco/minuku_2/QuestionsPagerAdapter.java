package edu.umich.si.inteco.minuku_2;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by neerajkumar on 11/8/16.
 */

public class QuestionsPagerAdapter extends PagerAdapter {

    private Context mContext;

    public QuestionsPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        QuestionsPagerEnum customPagerEnum = QuestionsPagerEnum.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(),
                collection, false);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return QuestionsPagerEnum.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        QuestionsPagerEnum customPagerEnum = QuestionsPagerEnum.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }

}
