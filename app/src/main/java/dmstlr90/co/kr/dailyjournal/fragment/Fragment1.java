package dmstlr90.co.kr.dailyjournal.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dmstlr90.co.kr.dailyjournal.R;

public class Fragment1 extends Fragment{
    //싱글톤
    private static Fragment1 curr = null;
    public static Fragment1 getInstance(){
        if(curr == null){
            curr = new Fragment1();
        }
        return curr;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view  = inflater.inflate(R.layout.frag_1,container,false);




        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
