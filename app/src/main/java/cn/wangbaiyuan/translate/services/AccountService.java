package cn.wangbaiyuan.translate.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import cn.wangbaiyuan.translate.Authenticator;

public class AccountService extends Service {
    private Authenticator authenticator;
    public AccountService() {

    }
@Override
public  void onCreate(){
    super.onCreate();
    authenticator=new Authenticator(this);

}
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
     return authenticator.getIBinder();
    }
}
