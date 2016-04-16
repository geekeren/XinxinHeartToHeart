package cn.wangbaiyuan.translate.model;

/**
 * Created by BrainWang on 2016/2/13.
 */

import android.os.AsyncTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.wangbaiyuan.translate.R;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;

    public UserLoginTask(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        try {
            // Simulate network access.
           // BYhttpPost client=new BYhttpPost("http://hm.wangbaiyuan.cn");
            Map<String, String> param = new HashMap<String, String>();

            param.put("email", mEmail);
            param.put("password", mPassword);
            param.put("autologin", "false");
            String reply=BYhttpPost.sendHttpClientPOSTRequest("logincheck",param);
            JSONObject Json=new JSONObject(reply);
            String code=Json.getString("code");
            return code.equals("true");
        }  catch (ClientProtocolException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            return false;
        }

    }

}