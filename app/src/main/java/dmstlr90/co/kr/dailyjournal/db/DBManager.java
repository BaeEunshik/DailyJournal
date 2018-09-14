package dmstlr90.co.kr.dailyjournal.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import dmstlr90.co.kr.dailyjournal.data.Journal;
import dmstlr90.co.kr.dailyjournal.data.Picture;

public class DBManager extends SQLiteOpenHelper{
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //1. 일기
        String query1 = "CREATE TABLE Journal (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, date INTEGER);";
        db.execSQL(query1);
        //2. 사진
        String query2 = "CREATE TABLE Picture (id INTEGER PRIMARY KEY AUTOINCREMENT, address INTEGER, width INTEGER, height INTEGER, journal_id);";
        db.execSQL(query2);
        //3. 북
        String query3 = "CREATE TABLE Book (id INTEGER PRIMARY KEY AUTOINCREMENT, date_start INTEGER, date_end INTEGER, is_current_use INTEGER);";
        db.execSQL(query3);
    }
    // ------------------------------------------------------------------------------
    // -------------------------------- SAVE 함수 -----------------------------------
    // ------------------------------------------------------------------------------

    public void insertJournalData( String content, Integer date ) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "insert into Journal values (null,'" + content +  "', '"+ date +"');";
        db.execSQL(query);
    }
    public void updateJournalData( String content, Integer date ) {
        SQLiteDatabase db = getReadableDatabase();
        //1. 날짜에 부합하는 일기 데이터를 가져와
        //2. 해당 일기 데이터에 content 를 update 한다.
        String query =  "update Journal set content = '"+ content +"' where date = '"+ date +"';";
        db.execSQL(query);
    }

    // ------------------------------------------------------------------------------
    // -------------------------------- GET 함수 ------------------------------------
    // ------------------------------------------------------------------------------

    //F2 의 사진 리스트 생성에 사용
    public ArrayList<Picture> getPictureDataList(){
        //TODO: 나중엔 getPictureDataListFromBook 이 되어야 함

        // 일기의 ID 를 foreignKey 로 가진 이미지를 담는다.
        // 이미지가 존재하지 않는 경우 객체에 [ date / journal_id ] 만 값을 담고, 객체의 나머지 멤버는 null 로 처리한다.
        ArrayList<Picture> pictures = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String idQuery = "select id, date from Journal;";   //~ where date >= 20180000 and date <= 20189999
        Cursor idCursor = db.rawQuery(idQuery,null);

        Integer journal_id, date;
        Integer imgId=null, imgAddr=null, imgWidth=null, imgHeight=null;

        while(idCursor.moveToNext()) {
            journal_id = idCursor.getInt(0);
            date = idCursor.getInt(1);

            String imgQuery = "select * from Picture where journal_id = '"+journal_id+"';";
            Cursor imgCursor = db.rawQuery(imgQuery,null);

            if(imgCursor.moveToNext()){
                imgId = imgCursor.getInt(0);
                imgAddr = imgCursor.getInt(1);
                imgWidth = imgCursor.getInt(2);
                imgHeight = imgCursor.getInt(3);
            }

            Picture tmpPic = new Picture(imgId,imgAddr,imgWidth,imgHeight,date,journal_id);
            pictures.add(tmpPic);
            imgCursor.close();
        }
        idCursor.close();

        return pictures;
    }
    //F3 의 일기 객체 생성에 사용하는 함수
    public Journal getJournalData(int date){
        Journal journal = null;

        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from Journal where date = '"+ date +"';";

        Cursor cursor = db.rawQuery(query,null);
        while(cursor.moveToNext()){
            Integer id = cursor.getInt(0);
            String content = cursor.getString(1);

            journal = new Journal(id,content,date);
        }
        return journal;
    }


    /*
    public ArrayList<Journal> getTotalJournalDataList() {

        ArrayList<Journal> tmpJournals = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from Journal;";

        Cursor cursor = db.rawQuery(query,null);
        while(cursor.moveToNext()) {

            Integer id = cursor.getInt(0);
            String content = cursor.getString(1);
            Integer date = cursor.getInt(2);

            Journal tmpJournal = new Journal(id,content,date);

            tmpJournals.add(tmpJournal);
        }
        cursor.close();

        return tmpJournals;
    }
    */

    // ------------------------------------------------------------------------------
    // -------------------------------- DELETE 함수 ----------------------------------
    // ------------------------------------------------------------------------------

    //날짜에 해당하는 journal 삭제
    public void deleteJournalData(int date){
        SQLiteDatabase db = getReadableDatabase();
        String query = "delete from Journal where date = '"+ date +"';";
        db.execSQL(query);
    }





    // ------------------------------------------------------------------------------
    // -------------------------------- Log 함수 ------------------------------------
    // ------------------------------------------------------------------------------

    //Log - Journal Table
    public void logTotalJournalDataList() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from Journal;";
        Cursor cursor = db.rawQuery(query,null);
        while(cursor.moveToNext()) {
            Integer id = cursor.getInt(0);
            String content = cursor.getString(1);
            Integer date = cursor.getInt(2);
            Log.d("은식","id : " + id + "  content : " + content +"  date : " + date);
        }
        cursor.close();
    }


    @Override   //업그레이드시
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
