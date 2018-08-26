package dmstlr90.co.kr.dailyjournal;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmstlr90.co.kr.dailyjournal.adapter.MainFragmentAdapter;
import dmstlr90.co.kr.dailyjournal.bus.BusProvider;
import dmstlr90.co.kr.dailyjournal.db.DBManager;
import dmstlr90.co.kr.dailyjournal.event.DoSaveJournalAndRefresh;
import dmstlr90.co.kr.dailyjournal.event.JournalToDBEnd;
import dmstlr90.co.kr.dailyjournal.event.JournalToDBStart;
import dmstlr90.co.kr.dailyjournal.event.SelectJournalData;
import dmstlr90.co.kr.dailyjournal.views.MyViewPager;

// 주된 페이지가 되는 ( 가제 : 카테고리, 리스트, 노트  )
// Fragment 3개를 ViewPager 로 합치는 Activity 이다.

public class Main extends AppCompatActivity {
    //STATIC Var
    static int RECENT = -1; // addOnPageChangeListener 의 position 과 함께 조건문에 사용
    static int START_PAGE = 1;
    static int BODY_PAGE = 2;

    //DB
    DBManager dbManager;
    //EventBus
    Bus bus = BusProvider.getInstance().getBus();
    //ButterKnife
    Unbinder unbinder;
    //Adapter
    MainFragmentAdapter mainFragmentAdapter;

    //View
    @BindView(R.id.viewPager) MyViewPager viewPager;
    @BindView(R.id.pageNavBar)LinearLayout pageNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        //DBManager
        dbManager = new DBManager(Main.this,"DailyJournal.db",null,1);

        //버터나이프
        unbinder =  ButterKnife.bind(this);
        //이벤트버스
        bus.register(this);

        //ViewPager 어댑터 설정
        mainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainFragmentAdapter);
        viewPager.setCurrentItem(START_PAGE);

        //페이지 변경 리스너
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {      // position 은 0 부터 시작한다.
                // 1) F3 으로 넘어온 경우
                if(position == 2){
                    RECENT = 2;
                }
                // 2) F3 -> F2 로 넘어온 경우
                if(position == 1 && RECENT == 2){

                    // 1. F3 로 " 데이터의 저장 / 업데이트 " 요청
                    bus.post(new DoSaveJournalAndRefresh());
                    // 2. F3 의 asyncTask 에서 수행한 뒤 -> F2로 리스트의 refresh 요청

                    // 3. F2 에서 refreshBookData()
                    // - DB insert & recycleView refresh 수행..

                    RECENT = -1;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }

    //일기 데이터 DB 에 저장하는 중에는 뷰의 이동을 제한한다.
    @Subscribe
    public void journalToDBStart(JournalToDBStart evt){
        viewPager.setPagingEnabled(false);
    }
    @Subscribe
    public void journalToDBEnd(JournalToDBEnd evt){
        viewPager.setPagingEnabled(true);
    }

    //F2 일기 리스트에서 일기를 클릭한 경우
    @Subscribe
    public void selectJournalData(SelectJournalData evt){
        viewPager.setCurrentItem(BODY_PAGE);
    }


    //로그찍기(임시)
    @OnClick(R.id.pageNavBar)
    public void tmpClickFunc(){
        dbManager.logTotalJournalDataList();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //이벤트버스
        bus.unregister(this);
        //버터나이프
        unbinder.unbind();
    }
}
