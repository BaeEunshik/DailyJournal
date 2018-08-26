package dmstlr90.co.kr.dailyjournal.adapter;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dmstlr90.co.kr.dailyjournal.R;
import dmstlr90.co.kr.dailyjournal.bus.BusProvider;
import dmstlr90.co.kr.dailyjournal.data.Picture;
import dmstlr90.co.kr.dailyjournal.event.SelectJournalData;

/**
 * Created by Jerry on 12/16/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Holder> {

    private ArrayList<Picture> pictures;
    private int screenWidth = 0;

    private Context context;

    public RecyclerViewAdapter(ArrayList<Picture> pictures, Context context) {
        this.pictures = pictures;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //----------------------- 홀더 객체를 리턴하는 함수 -----------------------

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_frag_2, parent, false);

       /* // 비대칭 레이아웃 만들기 위해
        // item 의 width, height 을 변경하는 함수 호출
        this.ChangeItemViewSize(parent, itemView); */
        this.ChangeItemViewSize(parent, itemView);

        // parent.setBackgroundColor(Color.parseColor("#ff0000"));


        //----------------------- 여기서 item 레이아웃 속성들을 특정하여 HOLDER 객체로 생성하여 리턴 -----------------------
        Holder ret = new Holder(itemView);
        return ret;
    }
    @Override
    public void onBindViewHolder(Holder holder, int position) {

      //1) arr 에서 아이템 가져오기
        final Picture picture = pictures.get(position);
        final Integer date = picture.getDate();

      //2) 아이템의 소스 적용 - 글라이드

        //1. 비율 설정 ( 아직 )
        ImageView img = holder.itemImageView;
        TextView text = holder.itemTextView;
        LayoutParams layoutParams = img.getLayoutParams();

        //2. 이미지 적용

        // - 이미지가 없다면
        if(picture.getAddr() == null){
            layoutParams.height = 500;
            layoutParams.width = 500;
            img.setLayoutParams(layoutParams);
            img.setBackgroundColor(Color.parseColor("#666666"));
            Log.d("은식","null");
        } else {

            layoutParams.height = 300;      //높이값 구해야 함 ( ex. 테이블에 size 저장...) ****

            //GlideApp.with(context)
            //        .load(journal.get~()) //키값으로 이미지 arrList 에서 가져온다?
            //        .into(img);
        }

        //3. 텍스트 적용
        text.setText(Integer.toString( date ));

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("은식","ImgView 클릭");
                //1. F3 으로 이동
                //2. F3 에서 클릭한 이미지에 해당하는 일기 데이터 가져오도록 해야 함
                BusProvider.getInstance().getBus().post(new SelectJournalData( date ));        //날짜 데이터를 보내줌
            }
        });


    }
    @Override
    public int getItemCount() {
        // Return the data list count that will be displayed in RecyclerView.
        int ret = 0;
        if(this.pictures != null) {
            ret = this.pictures.size();
        }
        return ret;
    }

    private void ChangeItemViewSize(ViewGroup parent, View itemView) {

        LayoutParams layoutParams = itemView.getLayoutParams();

        if(screenWidth == 0){
            Display display = parent.getDisplay();
            screenWidth = display.getWidth();                       //디스플레이의 width 값을 가져온다. ( 최초 1회 인듯 )
        }

        // 해당 예제는
        // 3 열이기 때문에 3개
        //Main 의 setRecyclerViewLayoutManager 함수에서 sapnCount 를 3으로 사용

        //layoutParams.height = layoutParams.notify();
        layoutParams.width = screenWidth / 3;                       //layoutParams.width = 디스플레이 가로폭 / 3
        itemView.setLayoutParams(layoutParams);                     //item 레이아웃의 사이즈 적용

    }

    /******************** HOLDER ********************/
    public class Holder extends RecyclerView.ViewHolder {

        private ImageView itemImageView = null;
        private TextView itemTextView = null;

        public Holder(View itemView) {
            super(itemView);
            if(itemView != null) {
                itemImageView = itemView.findViewById(R.id.img);
                itemTextView = itemView.findViewById(R.id.textDate);
            }
        }

    }
}
