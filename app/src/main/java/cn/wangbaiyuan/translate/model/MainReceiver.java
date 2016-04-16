package cn.wangbaiyuan.translate.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.wangbaiyuan.translate.services.MainService;

public class MainReceiver extends BroadcastReceiver {
    static final String  ACTION="android.intent.action.BOOT_COMPLETED";
    public MainReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      if(intent.getAction().equals(ACTION)){
          context.startService(new Intent(context, MainService.class));
      }
    }
}
