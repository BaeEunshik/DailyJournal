package dmstlr90.co.kr.dailyjournal.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmstlr90.co.kr.dailyjournal.R;
import dmstlr90.co.kr.dailyjournal.bus.BusProvider;
import dmstlr90.co.kr.dailyjournal.data.Journal;
import dmstlr90.co.kr.dailyjournal.db.DBManager;
import dmstlr90.co.kr.dailyjournal.event.DoSaveJournalAndRefresh;
import dmstlr90.co.kr.dailyjournal.event.F2DataRefresh;
import dmstlr90.co.kr.dailyjournal.event.JournalToDBEnd;
import dmstlr90.co.kr.dailyjournal.event.JournalToDBStart;
import dmstlr90.co.kr.dailyjournal.event.SelectJournalData;

//**날짜설정
//  날짜 설정의 경우 :
//      1. 첫 ( 오늘자 일기 ) 작성
//      2. 일기 선택하여 들어온 경우
//          - 1) F2 에서 이벤트 버스를 사용
//          - 2) 여기에서 "아이디" or "날짜"를 받아
//          - 3) 해당 데이터들을 뿌려준다.

// 글을 쓰고 저장된다.
//      1. 빠져나가는 경우 ( currentPosition 바뀌는 경우 ) 저장되어 db 에 넘어간다.


//  *** 일단 구현할 것
//  1. 빠져나가는 경우 데이터 저장                  [ O ]
//  2. 다시 오는 경우 오늘 데이터 보여주기 -        [ O ]


public class Fragment3 extends Fragment{
    //싱글톤
    private static Fragment3 curr = null;
    public static Fragment3 getInstance(){
        if(curr == null){
            curr = new Fragment3();
        }
        return curr;
    }
    public EditText getInputText() {
        return inputText;
    }

    //날짜
    Calendar calendar = Calendar.getInstance();
    //DB
    DBManager dbManager;
    //버스
    Bus bus = BusProvider.getInstance().getBus();
    //버터나이프 Unbinder
    private Unbinder unbinder;
    @BindView(R.id.inputText)EditText inputText;
    @BindView(R.id.textContent)TextView textContent;
    @BindView(R.id.textDate)TextView textDate;
    @BindView(R.id.dateBox)LinearLayout dateBox;
    @BindView(R.id.textBox)LinearLayout textBox;
    @BindView(R.id.inputBox)LinearLayout inputBox;

    @BindView(R.id.btnOpenGallery)Button btnOpenGallery;
    @BindView(R.id.btnDelJournal)Button btnDelJournal;

    //AsyncTask
    SaveJournalDBTask saveJournalDBTask;

    //사용할 일기 ( 오늘날짜 ) 객체
    Journal f3Journal = null;

    //사용할 날짜 ( 오늘날짜 )
    int f3Day = 0;

    /* ******************* onCreateView ******************* */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view  = inflater.inflate(R.layout.frag_3,container,false);
        Log.d("은식","Frag3 onCreateView");

        //DBManager
        dbManager = new DBManager(container.getContext(),"DailyJournal.db",null,1);
        //버스
        bus.register(this);
        //버터나이프
        unbinder = ButterKnife.bind(this, view);
        //Init
        f3Day = getTodayDataInt();  //오늘기준
        initJournal(f3Day);         //전역객체 journal 에 객체 할당 및 View 에 뿌려줌

        //      OnCreateView ( 기본 )

        //      이벤트 버스에서 받는다 ( 구현 해야함 )

        //  OnCreateView
        //      initJournal( todayInt );
        //      1. 전역 Calendar 에서 얻은 오늘 날짜를 가진 객체를
        //      2. DB 에서 검색한다.
        //      3. 있는 경우 해당 객체의 데이터를 뿌린다.
        //      4. 없는 경우 빈 화면을 출력한다.

        //  EvtBus ***
        //  F2 에서 Main 과 F3 으로 보낸 EvtBus 캐치
        //  ( F2 에서 Evt 객체에 날짜를 담아 보낸다. )
        //  initJournal( evt.get??Date() );  - ( 뭔말??????? )

        return view;
    }
    /* ******************* onDestroy ******************* */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //버스
        bus.unregister(this);
        //버터나이프 - unbind
        unbinder.unbind();
        Log.d("은식","Frag3 onDestroy");
    }


    //일기저장 ( 임시 )
    /* 날짜를 텍스트로 입력후 ( ex. 20180727 ) 버튼 클릭시
    *  해당 날짜의 데이터로 " 20180727 의 데이터 " 라는 텍스트 저장 */
    @OnClick(R.id.btnOpenGallery)
    public void a(){
        String strDate = inputText.getText().toString();
        Integer date = Integer.parseInt(strDate);
        dbManager.insertJournalData(strDate + " 의 데이터 ",date);
    }
    //일기삭제 ( 임시 )
    /* 날짜를 텍스트로 입력후 ( ex. 20180727 ) 버튼 클릭시
     * 해당 날짜의 일기 데이터 삭제 - 다시 지워야함 */
    @OnClick(R.id.btnDelJournal)
    public void DelJournal(){
        String strDate = inputText.getText().toString();
        Integer date = Integer.parseInt(strDate);
        dbManager.deleteJournalData(date);
    }


    /* ******************* OnClick ******************* */
    @OnClick(R.id.textBox)
    public void onClickBtnEdit(View view){
        //  수정 / 작성 ( 활성화 )
        String str = textContent.getText().toString(); //  1. 텍스트뷰의 텍스트를 가져온다.
        textBox.setVisibility(View.GONE); //  2. 텍스트 레이아웃을 비활성화 시킨다.
        inputBox.setVisibility(View.VISIBLE);    //  3. 인풋 레이아웃 활성화 시킨다.
        inputText.setText(str);   //  4. 에딧텍스트에 셋텍스트 시킨다.
    }

    /* ******************* EVT_BUS ******************* */
    @Subscribe
    public void doSaveJournalAndRefresh(DoSaveJournalAndRefresh evt) {

       /*   " F3 > F2 로 이동하였을 때 Main 에서 실행하는 EVT_BUS "
             input 창이 활성화 되었을 경우 ( 일기를 작성한 흔적이 있는 경우에만 )
             DBTask 가 실행되도록 처리                      */

        if(inputBox.getVisibility() == View.VISIBLE){
            String content = inputText.getText().toString();// 적힌 일기 내용
            // 일기장에 있는 내용을 조건에 따라 DB에
            // [ Insert / Update ] 하는 스레드를 실행
            saveJournalDBTask = new SaveJournalDBTask(content,f3Day);   //f3Day : 현재 일기의 날짜로 사용할 변수
            saveJournalDBTask.execute();
        }
    }
    @Subscribe
    public void selectJournalData(SelectJournalData evt) {
        // F2 의 일기 리스트 ( RVAdapter ) 에서 일기를 클릭한 경우 -
        // 해당 일기의 날짜를 전달받아 - F3 에서 사용할 날짜로 지정한 후
        // F3의 View 에 뿌려주도록 init 시킨다.
        f3Day = evt.getDate();
        initJournal(f3Day);
    }

    /* ******************* METHOD ******************* */
    //For Use Correct Journal ( in this Fragment )
    public void initJournal(int day) {
        //  ** 오늘날짜 일기를 작성 후 나갔다 들어왔을 때도 아래 로직을 검사해야 한다. -  F2 로 보내고 Bus 다시 올 때 실행 (????? 구현 안되었나 ?????)
        //  ** 오늘날짜 일기를 한번으로 제한.

        //  DB 에서 day 날짜와 일치하는 일기가 있는지 검사한다.
        //      - 있으면 그것으로 데이터 뿌려준다.
        //      - 없으면 빈 화면 ( 오늘의 경우 해당 )

        GetJournalTask getJournalTask = new GetJournalTask();
        getJournalTask.execute(day);
    }
    //SetText Date
    public void setDateText(String date){
        textDate.setText(date);
    }
    //Return Today String
    public int getTodayDataInt(){
        String yearData = Integer.toString(calendar.get(Calendar.YEAR));
        String monthData = Integer.toString(calendar.get(Calendar.MONTH)+1);
        String dayData = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if(monthData.length() == 1){
            monthData = "0" + monthData;
        }
        if(dayData.length() == 1){
            dayData = "0" + dayData;
        }
        return Integer.parseInt(yearData + monthData + dayData);
    }

    /* ******************* AsyncTask ******************* */
    // 전역객체에 일기를 할당시키고,
    // View 에 뿌려주는 스레드
    public class GetJournalTask extends AsyncTask<Integer, Integer, Integer>{
        @Override
        protected Integer doInBackground(Integer... days) {
            f3Journal = dbManager.getJournalData(days[0]);// 해당 날짜의 일기를 전역객체에 할당 - 없으면 null

            return days[0]; //onPostExecute 로 전달
        }
        @Override
        protected void onPostExecute(Integer day) {
            super.onPostExecute(day);   //DoInBackground 로부터의 상속
            //  일기 객체가 할당되었으면 화면에 데이터를 뿌려준다.
            //  1.글
            //  2.이미지 ( 아직 )
            //  3.....태그..... ( 아직 )
            if(f3Journal != null){
                //일기내용
                textContent.setText(f3Journal.getContent());
                textBox.setVisibility(View.VISIBLE);
                inputBox.setVisibility(View.GONE);  //편집창 비활성화
            } else {
                //일기 없으면 빈 화면
                textContent.setText("");
                textBox.setVisibility(View.VISIBLE);
                inputBox.setVisibility(View.GONE);
            }
            setDateText(Integer.toString(day));     //해당하는 날짜 보여줌
        }
    }

    // DB 저장 스레드 AsyncTask
    // 1. DB Insert
    //  (1) 날짜의 데이터가 없다면
    //  (2) 날짜의 데이터가 있다면 content ( 와 이미지 경로 ) 를 update 한다.
    //      데이터 유무 체크는 DB 가 아닌 arrayList 에서 하는 걸로? -

    // 2. DB 접근 ( 저장 ) 중에
    //  1안) frag3 에 접근하지 못하도록 한다.
    //  2안) DB 작업 취소하고 새로운 스레드 실행?

    // 3. F2 리스트 최신화 ( 리사이클 뷰 )
    public class SaveJournalDBTask extends AsyncTask<String,String,Boolean> {

        private String content;
        private Integer date;

        public SaveJournalDBTask(String content, Integer date) {
            this.content = content;
            this.date = date;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // " DB에 Journal Insert 과정을 시작. " ( 끝날 때까지 Main 에서 viewPager 이동제한 )
            bus.post(new JournalToDBStart());
        }
        @Override
        protected Boolean doInBackground(String... strings) {   // String... 은 execute 시 인자로 넣으면 들어오는 배열 값이다.
            //  새 일기 or 수정 구분
            //  현재 날짜의 일기가 DB 에 존재하는지 체크
            // ( 선언 기본 null - OnCreateView 에서 오늘날짜로 검색해 DB 에 존재하면 가져옴 )
            if( f3Journal == null ){
                // 일기존재(X) : Insert - 결과적으로 오늘날짜의 경우에만 1회 작동하도록 되어있음
                dbManager.insertJournalData(content, date);
            } else {
                // 일기존재(O) : update
                dbManager.updateJournalData(content, date);
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean isSave) {
            super.onPostExecute(isSave);
            // " DB에 Journal Insert 마침. "
            // init ( f3Journal, View )
            initJournal(getTodayDataInt()); //F3
            //  1) Main 에 F3 접근 허가
            bus.post(new JournalToDBEnd());
            //  2) F2 에 Data 를 refresh 요청
            bus.post(new F2DataRefresh());
        }
    }
}