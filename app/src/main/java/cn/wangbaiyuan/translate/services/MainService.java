package cn.wangbaiyuan.translate.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.wangbaiyuan.translate.MainActivity;
import cn.wangbaiyuan.translate.R;
import cn.wangbaiyuan.translate.model.BYhttpPost;
import cn.wangbaiyuan.translate.model.DbAdapter;
import cn.wangbaiyuan.translate.model.SingleNote;
import cn.wangbaiyuan.translate.model.Translate;

public class MainService extends Service {
    private Intent intent;
    private Thread thread;
    private Handler handler;
    public MainService() {
    }

    public static String ACTION = "cn.wangbaiyuan.MainService";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        this.intent = intent;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Translate.addIconToStatusBar(getBaseContext(), R.mipmap.ic_launcher, "昕翻译"
                , new Intent(getBaseContext(), MainActivity.class));
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                DbAdapter dbnote = new DbAdapter(getBaseContext());
                Cursor cursor = dbnote.getAllNote(0);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex("id"));
                            String publish_time = cursor.getString(cursor.getColumnIndex("publish_time"));
                            int status = cursor.getInt(cursor.getColumnIndex("status"));
                            String title = cursor.getString(cursor.getColumnIndex("title"));
                            int type = cursor.getInt(cursor.getColumnIndex("type"));
                            String content = cursor.getString(cursor.getColumnIndex("content"));

                            if (status== SingleNote.StatusDbIndex) {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("title", title);
                                params.put("type", type + "");
                                params.put("content", content);
                                params.put("author_id", 1001 + "");
                                params.put("publish_time", publish_time);
                                params.put("status", status + "");
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

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
            }
        });
        thread.start();
    }

    public void addRunnableToThread(Runnable run){
        handler.post(run);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbAdapter dbnote = new DbAdapter(getBaseContext());
                Cursor cursor = dbnote.getAllNote(0);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex("id"));
                            String publish_time = cursor.getString(cursor.getColumnIndex("publish_time"));
                            int status = cursor.getInt(cursor.getColumnIndex("status"));
                            String title = cursor.getString(cursor.getColumnIndex("title"));
                            int type = cursor.getInt(cursor.getColumnIndex("type"));
                            String content = cursor.getString(cursor.getColumnIndex("content"));

                            if (status== SingleNote.StatusDbIndex) {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("title", title);
                                params.put("type", type + "");
                                params.put("content", content);
                                params.put("author_id", 1001 + "");
                                params.put("publish_time", publish_time);
                                params.put("status", status + "");
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

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
            }
        }).start();
    }
}
