package dmstlr90.co.kr.dailyjournal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dmstlr90.co.kr.dailyjournal.R;
import dmstlr90.co.kr.dailyjournal.fragment.Fragment1;
import dmstlr90.co.kr.dailyjournal.fragment.Fragment2;
import dmstlr90.co.kr.dailyjournal.fragment.Fragment3;

public class MainFragmentAdapter extends FragmentStatePagerAdapter{
    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            return Fragment1.getInstance();
        } else if (position == 1){
            return Fragment2.getInstance();
        } else {
            return Fragment3.getInstance();
        }

    }
    @Override
    public int getCount() {
        return 3;
    }

}
