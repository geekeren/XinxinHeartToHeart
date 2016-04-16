package cn.wangbaiyuan.translate;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;

import cn.wangbaiyuan.translate.view.HeartTableRowView;
import cn.wangbaiyuan.translate.view.Heart_Info_Item;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HeartToHeartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HeartToHeartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeartToHeartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int progress=0;
    private OnFragmentInteractionListener mListener;
    private Button btn_heart_start;
    private Runnable telepathicRunnable;
    private Handler handler;
    private boolean isPressd=false;
    private Runnable timerRunnable;
    private Thread telepathicThread;

    private View view;
    private double latitude;
    private double longitude;
    private String addrStr;
    private LocationClient locationClient;
    private String accuracy;
    private LinearLayout lv_heart_content;
    private BDLocation thisBdLocation;

    public HeartToHeartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HeartToHeartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HeartToHeartFragment newInstance(String param1, String param2) {
        HeartToHeartFragment fragment = new HeartToHeartFragment();
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
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 100:
                        Heart_Info_Item item=new Heart_Info_Item(getContext(),"经度：" + longitude);
                        lv_heart_content.addView(item);
                        break;
                    case 101:
                        lv_heart_content.addView(new Heart_Info_Item(getContext(),"纬度：" + latitude));
                        break;
                    case 102:
                        lv_heart_content.addView(
                                new Heart_Info_Item(getContext(),"位置："+addrStr));
                        break;
                    case 103:
                        lv_heart_content.addView(
                                new Heart_Info_Item(getContext(), "精度："
                                        +accuracy+"米 速度："+thisBdLocation.getSpeed()));
                        break;
//                    case 101:
//                        new Thread();
                }

            }
        };
        telepathicRunnable = new Runnable() {
            @Override
            public void run() {
                progress =0;
                    try {
                         handler.sendEmptyMessage(100);
                        Thread.sleep(1000);
                        handler.sendEmptyMessage(101);
                        Thread.sleep(1000);
                        handler.sendEmptyMessage(102);
                        Thread.sleep(1000);
                        handler.sendEmptyMessage(103);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        };
        telepathicThread=new Thread(telepathicRunnable);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null){
            view=inflater.inflate(R.layout.fragment_heart_to_heart, container, false);
            Button btnLocationDetail = (Button) view.findViewById(R.id.btn_location_detail);
            btnLocationDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), LocationDetailActivity.class));

                }
            });
            btn_heart_start=(Button)view.findViewById(R.id.btn_heart_start);
            lv_heart_content=(LinearLayout)view.findViewById(R.id.lv_heart_content);
            final TextView tv_heart_note=(TextView)view.findViewById(R.id.tv_heart_note);
            //heart_table=(TableLayout)view.findViewById(R.id.heart_table);

            AppCompatActivity activity=(AppCompatActivity)getActivity();
            final ActionBar actionbar=activity.getSupportActionBar();

            final String originalTitle=MainActivity.titles[2];


            locationClient=new LocationClient(getActivity().getBaseContext());
            LocationClientOption option=new LocationClientOption();
            option.setOpenGps(true);
            option.setCoorType("bd09II");
            option.setPriority(LocationClientOption.NetWorkFirst);
            option.setProdName("bylocation");
            option.setScanSpan(3000);
            option.setIsNeedAddress(true);
           // option.set
            locationClient.setLocOption(option);


            locationClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    latitude = bdLocation.getLatitude();
                    longitude = bdLocation.getLongitude();
                    addrStr = bdLocation.getAddrStr();
                    accuracy= bdLocation.getRadius()+"";
                    thisBdLocation= bdLocation;
                    if (telepathicThread.getState() == Thread.State.NEW)
                        telepathicThread.start();
                    locationClient.stop();
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }
            });

            btn_heart_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progress >= 10) {
                        Toast.makeText(getContext()
                                , "感应成功！", Toast.LENGTH_SHORT).show();

                    } else {
//                        if (heart_table.getChildCount() > 0) {
//                            heart_table.removeAllViews();
//                        }
                        //lv_heart_content.removeAllViews();
                        tv_heart_note.setVisibility(View.VISIBLE);
                        //lv_heart_content.addView(tv_heart_note);
                        actionbar.setTitle(originalTitle);
                        Toast.makeText(getContext(), "感应失败，请保持长按", Toast.LENGTH_SHORT).show();
                    }


                }
            });
            btn_heart_start.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (progress >= 10) {
                        Toast.makeText(getContext()
                                , "感应成功！", Toast.LENGTH_SHORT).show();

                    } else {
                        locationClient.start();
                        locationClient.requestLocation();
                        actionbar.setTitle("感应中……");
                        tv_heart_note.setVisibility(View.GONE);
                        //lv_heart_content.removeView(tv_heart_note);
                        //heart_table.addView(new HeartTableRowView(getContext(), "她的位置：", "兰考县").getView());

                    }
                    return true;
                }
            });

        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
