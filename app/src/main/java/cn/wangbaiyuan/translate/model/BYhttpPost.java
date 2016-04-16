package cn.wangbaiyuan.translate.model;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
/**
 * 一个httppost的获取网络数据工具类，完成提交post请求并获取服务器应答的字符串
 * @author 王柏元
 *
 */
public class BYhttpPost {

    /**
     * @param path   相对于根地址的文件地址，你向哪个URL发起请求
     * @param params MAP格式的键值对
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendHttpClientPOSTRequest(String path, Map<String, String> params) throws ClientProtocolException, IOException {
        List<NameValuePair> pairs = new ArrayList<>();//
        BufferedReader in = null;
        Log.e("BYhttpPost", Translate.APIURL + path);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        //防止客户端传递过去的参数发生乱码，需要对此重新编码成UTF-8
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "utf-8");
        HttpPost httpPost = new HttpPost(Translate.APIURL + path);
        Log.e("BYhttpPost", Translate.APIURL + path);

        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
       // httpPost.addHeader("Host", "hm.wangbaiyuan.cn");
        httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        HttpResponse response = client.execute(httpPost);
        Log.e("BYhttpPost", response.getAllHeaders().toString());
        String result = "";
        if (response.getStatusLine().getStatusCode() == 200) {
            in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            result = sb.toString();

        } else if (response.getStatusLine().getStatusCode() == 404) {

        }else{
            in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            result = sb.toString();
        }
        Log.e("BYhttpPost", result);
        return result;
    }


}
