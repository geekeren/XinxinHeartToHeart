package cn.wangbaiyuan.translate.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

/**
 * Created by BrainWang on 2016/1/27.
 */
public class DbAdapter {
    private static final String DB_NAME="xin.sqlite3";
    private final String T_NOTE="note";
    private  SQLiteDatabase mSqLiteDatabase = null;
    private  final Context context;
    static{

    }



    public DbAdapter(Context context){
        this.context=context;
        DataBaseHelper myDbHelper = new DataBaseHelper(this.context, DB_NAME);
        try {
            if (mSqLiteDatabase != null) {
                mSqLiteDatabase.close();
            } else {
                mSqLiteDatabase = myDbHelper.openOrCreateDataBase("");
            }
        } catch (IOException e) {
            mSqLiteDatabase = null;
            e.printStackTrace();
        }
    }


    // 关闭数据库
    public void close() {
        mSqLiteDatabase.close();
    }

    //添加记事记录
    public long createNewNote(SingleNote note){
        ContentValues values = new ContentValues();
        //values.put("id",note.id);
        values.put("publish_time",note.publish_time);
        values.put("status",note.statusIndex);
        values.put("title",note.title);
        values.put("type",note.type);
        values.put("content", note.content);
        return mSqLiteDatabase.insert(T_NOTE, null, values);
    }


    //添加记事记录
    public long deleteNote(long id){

        return mSqLiteDatabase.delete(T_NOTE, "id=?", new String[]{id + ""});
    }

    //添加记事记录
    public long modifySingleNote(SingleNote note){
        ContentValues values = new ContentValues();
        long id=note.id;
        //values.put("id",note.id);
        values.put("publish_time",note.publish_time);
        values.put("status",note.statusIndex);
        values.put("title",note.title);
        values.put("type",note.type);
        values.put("content", note.content);
        return mSqLiteDatabase.update(T_NOTE, values, "id=?", new String[]{id + ""});
    }


    // modifyNote
    public long modifyNoteItem(long id,String item,String value){
        ContentValues values = new ContentValues();
        values.put(item,value);
        return mSqLiteDatabase.update(T_NOTE, values,"id=?", new String[]{id+""});
    }

    // mark draft as saved
    public void markDraftSaved(long id){
        modifyNoteItem(id, SingleNote.ITEM_STATUS, SingleNote.StatusDbIndex + "");
    }

    public Cursor getAllNote(int type) {
        String where=(type==0? " ":" where type='"+type+"'");
        String Order=" ORDER BY datetime(publish_time) DESC";
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + T_NOTE+where+Order, null);
        return cursor;
    }
    public void bindCloudId(long id,long cloud_id){
        modifyNoteItem(id, SingleNote.ITEM_STATUS, SingleNote.StatusCloudIndex + "");
        modifyNoteItem(id, SingleNote.ITEM_CLOUD_ID, cloud_id + "");
    }
}
