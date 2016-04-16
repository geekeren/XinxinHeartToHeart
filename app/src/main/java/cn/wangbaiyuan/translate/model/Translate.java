package cn.wangbaiyuan.translate.model;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.TextView;

import cn.wangbaiyuan.translate.MainActivity;
import cn.wangbaiyuan.translate.R;

/**
 * Created by BrainWang on 2016/1/30.
 */
public class Translate extends Application  {
private static final int NOTIFY_ID=0X15916;
    public static final String APIURL = "http://note.wangbaiyuan.cn/";

    public static void creatShortcut(Context context,String name,Intent intent){
Intent shortcut=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        //shortcut.putExtra("duplicate", false);
         intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent.ShortcutIconResource iconres =Intent.ShortcutIconResource.fromContext(context,R.mipmap.bg_nwpu);

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,iconres);
        context.sendBroadcast(shortcut);
    }

    public static void addIconToStatusBar(Context context,int resId,String notify,Intent intent){
        RemoteViews contentview=new RemoteViews(context.getPackageName(),R.layout.notify_content);
    // contentview.sets;
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n=new Notification(resId,notify,System.currentTimeMillis());
        n.flags|=Notification.FLAG_ONGOING_EVENT;
        n.flags|=Notification.FLAG_NO_CLEAR;
        PendingIntent pi=PendingIntent.getActivity(context,0,intent,0);
        n.contentIntent=pi;
        //n.setLatestEventInfo();
        n.contentView=contentview;
        nm.notify(NOTIFY_ID, n);

    }
}
