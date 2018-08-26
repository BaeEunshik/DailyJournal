package dmstlr90.co.kr.dailyjournal.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import dmstlr90.co.kr.dailyjournal.R;

public class CalendarAdapter extends BaseAdapter{
    ArrayList<Integer> dateList;
    Calendar calendar;
    public CalendarAdapter(ArrayList<Integer> dateList, Calendar calendar) {
        this.dateList = dateList;
        this.calendar = calendar;
    }

    @Override
    public int getCount() {
        return dateList.size();
    }
    @Override
    public Object getItem(int position) {
        return dateList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_date,parent,false);
            //홀더
            holder.dateText = convertView.findViewById(R.id.dateText);
            holder.item = convertView.findViewById(R.id.item);

            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        //getDateList
        int date = dateList.get(position);

        //0이 아닌 경우
        if (date > 0) {
            //날짜입력
            holder.dateText.setText(Integer.toString(date));
        }

        Calendar todayCal = Calendar.getInstance();
        int today = todayCal.get(Calendar.DAY_OF_MONTH);
        int nowMonth = todayCal.get(Calendar.MONTH);
        int getMonth = calendar.get(Calendar.MONTH);
        //Log.d("달",Integer.toString(getMonth));

        //초기화
        holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));
        holder.dateText.setTextColor(Color.parseColor("#000000"));
        //해당 월
        if(nowMonth == getMonth){
            //해당 일
            if(today == date){
                holder.item.setBackgroundColor(Color.parseColor("#5b18ce"));
                holder.dateText.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        return convertView;
    }
    private class Holder{
        TextView dateText;
        RelativeLayout item;
    }
}
