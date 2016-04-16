package cn.wangbaiyuan.translate.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 *
 */
public class HttpClient {

    private String urlString;
    private String userAgent = "BYBlog";

    public HttpClient() {

    }

    public HttpClient(String url) {
        urlString = url;
    }

    /**
     * 获取指定URL的响应字符串
     *
     * @return
     */
    public String getURLResponse() throws IOException {
        HttpURLConnection conn = null; //连接对象
        InputStream is = null;
        String resultData = "";
        try {
            URL url = new URL(Translate.APIURL + urlString); //URL对象
            conn = (HttpURLConnection) url.openConnection(); //使用URL打开一个链接
            conn.setDoInput(true); //允许输入流，即允许下载
            conn.setDoOutput(true); //允许输出流，即允许上传
            conn.setUseCaches(true); //不使用缓冲
            conn.setRequestMethod("GET"); //使用get请求
            conn.setReadTimeout(10000);
            //conn.setRequestProperty("User-Agent", userAgent);
            is = conn.getInputStream();   //获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine = "";
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resultData;
    }

    public static String post(String urlString,StringBuffer params){
        HttpURLConnection conn = null; //连接对象
        InputStream is = null;
        String resultData = "";
        try {
            URL url = new URL(Translate.APIURL + urlString); //URL对象
            conn = (HttpURLConnection) url.openConnection(); //使用URL打开一个链接
            conn.setDoInput(true); //允许输入流，即允许下载
            conn.setDoOutput(true); //允许输出流，即允许上传
            conn.setUseCaches(true); //不使用缓冲
            conn.setRequestMethod("POST"); //使用get请求
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            //conn.setRequestProperty("User-Agent", userAgent);
            conn.getOutputStream().write(params.toString().getBytes());
            is = conn.getInputStream(); //获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine = "";
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resultData;

    }
}