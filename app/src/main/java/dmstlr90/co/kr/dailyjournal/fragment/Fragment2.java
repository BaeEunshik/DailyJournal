package dmstlr90.co.kr.dailyjournal.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmstlr90.co.kr.dailyjournal.R;
import dmstlr90.co.kr.dailyjournal.adapter.RecyclerViewAdapter;
import dmstlr90.co.kr.dailyjournal.bus.BusProvider;
import dmstlr90.co.kr.dailyjournal.data.Picture;
import dmstlr90.co.kr.dailyjournal.db.DBManager;
import dmstlr90.co.kr.dailyjournal.event.F2DataRefresh;

// 목록을 나열하는 그리드뷰 페이지
// 나열 기준 : 현재 사용중인 북

public class Fragment2 extends Fragment{
    //싱글톤
    private static Fragment2 curr = null;
    public static Fragment2 getInstance(){
        if(curr == null){
            curr = new Fragment2();
        }
        return curr;
    }

    //DBManager
    DBManager dbManager;
    //버스
    Bus bus = BusProvider.getInstance().getBus();
    //버터나이프 Unbinder
    private Unbinder unbinder;
    @BindView(R.id.recyclerView)RecyclerView recyclerView;
    //일기 데이터
    ArrayList<Picture> pictures = new ArrayList<>();
    //어댑터
    RecyclerViewAdapter recyclerViewAdapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view  = inflater.inflate(R.layout.frag_2,container,false);

        //DBManager
        dbManager = new DBManager(container.getContext(),"DailyJournal.db",null,1);
        //버스
        bus.register(this);
        //버터나이프
        unbinder = ButterKnife.bind(this, view);
        //데이터Set
        refreshBookData();

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //버스
        bus.unregister(this);
        //버터나이프 - unbind
        unbinder.unbind();
    }
// ------------------------------------------------------------------------------
// -------------------------------- METHOD  -------------------------------------
// ------------------------------------------------------------------------------
    //데이터 init
    public void refreshBookData(){
        GetCurrBookJournalsTask getCurrBookJournalsTask = new GetCurrBookJournalsTask();
        getCurrBookJournalsTask.execute();
    }
    // 레이아웃 매니저
    private void setRecyclerViewLayoutManager(RecyclerView recyclerView,  int orientation) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, orientation);  //컬럼갯수, 레이아웃 방향.
        recyclerView.setLayoutManager(layoutManager);
    }


// ------------------------------------------------------------------------------
// -------------------------------- AsyncTask -----------------------------------
// ------------------------------------------------------------------------------
    //Data get Thread ( refresh 에도 사용 )
    // - 초기 설정되어있는 Book 정보에 의해 ( 기간 ) ** - 나중
    // - 일기를 추려온다.
    public class GetCurrBookJournalsTask extends AsyncTask<String, String ,String>{
        @Override
        protected String doInBackground(String... strings) {
            // ( 현재의 Book 기간으로 검색 로직 추후 작성 )
            pictures = dbManager.getPictureDataList();
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //방향설정 및 매니저 Set
            int orientation = StaggeredGridLayoutManager.VERTICAL;
            setRecyclerViewLayoutManager(recyclerView, orientation);

            //어댑터 Set
            recyclerViewAdapter = new RecyclerViewAdapter(pictures,getContext());
            recyclerView.setAdapter(recyclerViewAdapter);

            Log.d("은식 F2 - pictures.size",Integer.toString(pictures.size()));
            Log.d("은식 F2 - getItemCount",Integer.toString(recyclerViewAdapter.getItemCount()));
        }
    }

// ------------------------------------------------------------------------------
// -------------------------------- EVT BUS  ------------------------------------
// ------------------------------------------------------------------------------
    @Subscribe
    public void f2F3DataRefresh(F2DataRefresh evt){
        // F3 에서 일기가 저장이 완료되었다.
        // 이 저장 정보를 F2 에서 리스트에 반영 시키기 위해 DB 에서 다시 받아온다.
        // 성능저하 이슈로 다른 로직 ( 다시 받지 않는 ) 작성이 필요하다고 보여진다. ****
        refreshBookData();
    }
}
