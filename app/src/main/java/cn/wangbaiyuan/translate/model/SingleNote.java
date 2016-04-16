package cn.wangbaiyuan.translate.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BrainWang on 2016/1/27.
 */
public class SingleNote {
    public long id=0;
    public long cloud_id=0;
    public long author_id=1000;
    public String publish_time;
    public int statusIndex;
    public String title;
    public int type;
    public String content;

    public static int StatusDraftIndex=0;
    public static int StatusDbIndex=1;
    public static int StatusCloudIndex=2;
    public static String[] noteStatuss=new String[]{
            "草稿",
            "本地",
            "云"
    };
    public static int TypeallIndex = 0;
    public static int TypeLifeIndex = 1;
    public static int TypeNoteIndex = 3;
    public static int TypeDreamIndex = 2;
    public static int TypeIdeaIndex = 4;
    public static int TypeCollectIndex = 5;
    public static String[] noteTypes = new String[]{
            "全部",
            "生活",
            "记梦",
            "笔记",
            "灵感",
            "收藏"
    };
    public static String ITEM_ID="id";
    public static String ITEM_CLOUD_ID="cloud_id";
    public static String ITEM_TIME="publish_time";
    public static String ITEM_STATUS="status";
    public static String ITEM_TITLE="title";
    public static String ITEM_TYPE="type";
    public static String ITEM_CONTENT="content";

    private Context context;
    public SingleNote(Bundle bundle){
        this(bundle.getLong(SingleNote.ITEM_ID,0), bundle.getString(SingleNote.ITEM_TIME, ""),
                bundle.getInt(SingleNote.ITEM_STATUS, 0),
                bundle.getString(SingleNote.ITEM_TITLE, ""),
                bundle.getInt(SingleNote.ITEM_TYPE, 0),
                bundle.getString(SingleNote.ITEM_CONTENT, ""));

    }

    public SingleNote(long id, String publish_time, int statusIndex, String title, int type, String content){
        this.id=id;
        this.publish_time=publish_time;
        this.statusIndex=statusIndex;
        this.title=title;
        this.type=type;
        this.content=content;
    }

    public SingleNote(Context context, int type, int sIndex, String title, String content){
        this.context=context;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        this.publish_time=time;
        this.statusIndex=sIndex;
        this.title=title;
        this.type= type;
        this.content=content;

    }

    public void save(){
        if(id==0){
            DbAdapter dbnote = new DbAdapter(context);
            dbnote.createNewNote(this);
        }

    }

    public void uploadToCloud(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbAdapter dbnote = new DbAdapter(context);

                if (statusIndex== SingleNote.StatusDbIndex) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("title", title);
                    params.put("type", type + "");
                    params.put("content", content);
                    params.put("author_id", 1001 + "");
                    params.put("publish_time", publish_time);
                    params.put("status", statusIndex + "");
                    String response = null;
                    try {
                        response = BYhttpPost.sendHttpClientPOSTRequest("note/create", params);
                        JSONObject jso = new JSONObject(response);
                        long cloud_id = jso.getLong("id");
                        dbnote.bindCloudId(id, cloud_id);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    public Bundle convertToBundle(){
        Bundle bundle=null;
        if(id!=0){
            bundle=new Bundle();
            bundle.putLong(SingleNote.ITEM_ID, id);
            bundle.putString(SingleNote.ITEM_TIME, publish_time);
            bundle.putInt(SingleNote.ITEM_STATUS, statusIndex);
            bundle.putString(SingleNote.ITEM_TITLE, title);
            bundle.putInt(SingleNote.ITEM_TYPE, type);
            bundle.putString(SingleNote.ITEM_CONTENT, content);

        }
        return bundle;
    }

}
