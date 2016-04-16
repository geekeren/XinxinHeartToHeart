package cn.wangbaiyuan.translate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.wangbaiyuan.translate.model.SingleNote;
import cn.wangbaiyuan.translate.view.translateItemView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TranslateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TranslateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String clientID = "wangbaiyuan";
    private String clientSecret = "852287472";
    private EditText editToTranslate;
    private Button translateBtn;
    private Button resetBtn;

    private String keywords;
    private String rawContent="";
    private String imgUrl;

    private String translateContent;
    private android.support.v7.app.ActionBar ActionBar;
    private LinearLayout translatelv;

    private ArrayList<translateItemView> translateItemViews;
    private translateItemView dailyENView=null;
    private translateItemView dailyCNView=null;
    private translateItemView queryView=null;
    private translateItemView translationView=null;
    private translateItemView phoneticView=null;
    private translateItemView explainsView=null;
    private String dailyCN=null;
    private String dailyEN=null;
    private SharedPreferences.Editor localNotesEditor;
    private SharedPreferences localNotes;


    private OnFragmentInteractionListener mListener;
    private View view=null;

    public TranslateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TranslateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TranslateFragment newInstance(String param1, String param2) {
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null){
            view=inflater.inflate(R.layout.fragment_translate, container, false);
            localNotes = this.getActivity().getSharedPreferences("localNotes", 0);
            localNotesEditor = localNotes.edit();
            translateBtn = (Button) view.findViewById(R.id.translatebtn);
            resetBtn = (Button) view.findViewById(R.id.resetbtn);
            editToTranslate = (EditText) view.findViewById(R.id.editText);
            // toString = (TextView) view.findViewById(R.id.textView);
            translatelv = (LinearLayout) view.findViewById(R.id.translateContent);
            translateItemViews = new ArrayList<translateItemView>();
            setHasOptionsMenu(true);

            resetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editToTranslate.setText("");
                    translateItemViews.clear();

                    displayDailyNote();
                }
            });
            translateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(editToTranslate.getText().toString())) {
                        keywords = editToTranslate.getText().toString();
                        translateItemViews.clear();
                        translatelv.removeAllViews();
                        new Thread(askForYouDao).start();
                    } else {
                        Toast.makeText(getActivity(), "输入不能为空！", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            ClipboardManager clip=(ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipdata=clip.getPrimaryClip();
            if(clipdata!=null){
                if(clipdata.getItemCount()>0){
                    editToTranslate.setText(clipdata.getItemAt(0).getText()==null? "":clipdata.getItemAt(0).getText().toString());
                    Toast.makeText(getActivity(), "昕翻译自动加载了你当前复制的内容", Toast.LENGTH_SHORT).show();
                }

            }

        }
        translateItemViews.clear();

        displayDailyNote();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    Handler errHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String notice = "";
            switch (msg.what) {
                case 20:
                    notice = "要翻译的文本过长";
                    break;
                case 30:
                    notice = "无法进行有效的翻译";
                    break;
                case 40:
                    notice = "不支持的语言类型";
                    break;
                case 50:
                    notice = "无效的key";
                    break;
                case 60:
                    notice = "无词典结果";
                    break;
            }
            Toast.makeText(getActivity(), notice, Toast.LENGTH_SHORT);
        }
    };
    Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {

       if (msg.what == 0x121) {
                Iterator<translateItemView> e= translateItemViews.iterator();
                while(e.hasNext()) {
                    translatelv.addView(e.next().getView());
                }
            }
            else if (msg.what == 0x124) {
                Toast.makeText(getActivity(), "查询失败，请检查输入或联网", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void displayDailyNote(){
        if(translatelv.getChildCount()>0)
            translatelv.removeAllViews();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String dataStr=df.format(new Date());// new Date()为获取当前系统时间

        dailyEN=localNotes.getString("content","");
        dailyCN=localNotes.getString("note","");
        String picUrl = localNotes.getString("picture","");
        String date=localNotes.getString("date","");
        rawContent=dailyEN;
        translateContent=dailyCN;
        imgUrl = picUrl;

            if((dailyEN!=null&&dailyCN!=null&&date.equals(dataStr))||!checkNetworkAvailable(getActivity())){
                dailyENView=new translateItemView(getActivity(),"每日一句:"+dataStr,dailyEN);
                dailyCNView=new translateItemView(getActivity(),"译文：",dailyCN);
                translateItemViews.add(dailyENView);
                translateItemViews.add(dailyCNView);
                hander.sendEmptyMessage(0x121);
            }else{
                new Thread(askForDailyNote).start();
            }
    }
    private Runnable askForDailyNote = new Runnable() {
        @Override
        public void run() {

            try {
                String url_path = "http://open.iciba.com/dsapi/";
                URL getUrl = new URL(url_path);
                HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
                connection.setConnectTimeout(3000);
                connection.connect();
                BufferedReader replyReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"));//约定输入流的编码
                String reply = replyReader.readLine();
                JSONObject replyJson = new JSONObject(reply);
                dailyEN=replyJson.getString("content");
                dailyCN=replyJson.getString("note");
                String picUrl = replyJson.getString("picture");
                rawContent=dailyEN;
                translateContent=dailyCN;
                imgUrl = picUrl;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String dataStr=df.format(new Date());// new Date()为获取当前系统时间
                HashMap<String,String> dailyNotes=new HashMap<>();
//                dailyNotes.put("content",dailyEN);
//                dailyNotes.put("note",dailyCN);
//                dailyNotes.put("date",dataStr);
//                dailyNotes.put("picture", picUrl);
//                String notesJson=hashMapToJson(dailyNotes);
                localNotesEditor.putString("content", dailyEN);
                localNotesEditor.putString("note", dailyCN);
                localNotesEditor.putString("date", dataStr);
                localNotesEditor.putString("picture", picUrl);
                //String notesJson=hashMapToJson(dailyNotes);
               // localNotesEditor.putString("notes", notesJson);
                localNotesEditor.commit();
                dailyENView=new translateItemView(getActivity(),"每日一句:"+dataStr,dailyEN);
                dailyCNView=new translateItemView(getActivity(),"译文：",dailyCN);
                translateItemViews.add(dailyENView);
                translateItemViews.add(dailyCNView);
                hander.sendEmptyMessage(0x121);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    };

    private Runnable askForYouDao = new Runnable() {
        @Override
        public void run() {
            try {
                String url_path = "http://fanyi.youdao.com/openapi.do?keyfrom=" + clientID
                        + "&key=" + clientSecret + "&type=data&doctype=json&version=1.1&q="
                        + URLEncoder.encode(keywords, "utf8");
                URL getUrl = new URL(url_path);
                HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
                connection.setConnectTimeout(3000);
                connection.connect();
                BufferedReader replyReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"));//约定输入流的编码
                String reply = replyReader.readLine();
                JSONObject replyJson = new JSONObject(reply);
                String errorCode = replyJson.getString("errorCode");
                if (errorCode.equals("0")) {
                    String query = replyJson.getString("query");
                    queryView=new translateItemView(getActivity(),"原文",query);
                    rawContent=query;

                    translateItemViews.add(queryView);
                    JSONArray translation
                            = replyJson.has("translation") ? replyJson.getJSONArray("translation") : null;
                    JSONObject basic
                            = replyJson.has("basic") ? replyJson.getJSONObject("basic") : null;
                    JSONArray web
                            = replyJson.has("web") ? replyJson.getJSONArray("web") : null;
                    String phonetic=null;
                    String uk_phonetic=null;
                    String us_phonetic=null;
                    JSONArray explains=null;

                    if(basic!=null){
                        phonetic=basic.has("phonetic")? basic.getString("phonetic"):null;
                        uk_phonetic=basic.has("uk-phonetic")? basic.getString("uk-phonetic"):null;
                        us_phonetic=basic.has("us-phonetic")? basic.getString("us-phonetic"):null;
                        explains=basic.has("explains")? basic.getJSONArray("explains"):null;
                    }
//                    if(web!=null){
//                        JSONArray webs=web.getJSONObject()
//                    }
                    String translationStr="";
                    if(translation!=null){

                        for(int i=0;i<translation.length();i++){
                            translationStr+="\t【"+(i+1)+"】"+translation.getString(i)+"\n";
                        }
                        translationView=new translateItemView(getActivity(),"翻译",translationStr);
                        translateContent=translationStr;
                        translateItemViews.add(translationView);
                    }
                    if(phonetic!=null){
                        String phoneticStr="\n发音："+phonetic
                                + (uk_phonetic!=null? "\n\t【英式发音】："+uk_phonetic:"")
                                +(us_phonetic!=null? "\n\t【美式发音】："+us_phonetic:"");
                        phoneticView=new translateItemView(getActivity(),"发音",phoneticStr);
                        translateItemViews.add(phoneticView);
                    }

                    String explainStr="";
                    if(explains!=null){
                        for(int i=0;i<explains.length();i++){
                            explainStr+="\t【"+(i+1)+"】"+explains.getString(i)+"\n";
                        }
                        explainsView=new translateItemView(getActivity(),"释义",explainStr);
                        translateItemViews.add(explainsView);
                    }

                    hander.sendEmptyMessage(0x121);
                } else {
//                    Message errorMsg=new Message();
                    int what = Integer.parseInt(errorCode);
//                    errorMsg.what=what;
                    errHander.sendEmptyMessage(what);
                }


            } catch (Exception e) {
                Log.e("errss", e.getMessage());
                hander.sendEmptyMessage(0x124);
            }

        }

    };

    // 检测网络
    private  boolean checkNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return true;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;

    }

    private  String hashMapToJson(HashMap map) {
        String string = "{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();
            string += "'" + e.getKey() + "':";
            string += "'" + e.getValue() + "',";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";
        return string;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        menuInflater.inflate(R.menu.menu_main, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id == R.id.action_share) {

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);

            String shareText = rawContent + " " + translateContent + " \n——分享自【昕翻译】";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);


            shareIntent.setType("text/plain");
            //设置分享列表的标题，并且每次都显示分享列表
            startActivity(Intent.createChooser(shareIntent, "分享昕翻译到"));
        }else if(id == R.id.action_collect){
            if(dailyENView!=null&&!rawContent.isEmpty()) {
                String title=dailyENView.getTitleView().getText().toString();
                new SingleNote(getContext()
                        , SingleNote.TypeCollectIndex, SingleNote.StatusDbIndex
                        ,title
                        ,rawContent + " " + translateContent).save();
                Toast.makeText(getContext(), "成功收藏到昕记事", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "收藏失败，内容为空", Toast.LENGTH_SHORT).show();
            }


        }

        return true;
    }
    
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
