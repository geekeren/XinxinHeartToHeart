package cn.wangbaiyuan.translate;

import android.app.Service;
import android.graphics.Point;
import android.location.Location;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.*;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;

import cn.wangbaiyuan.translate.tools.Gps;
import cn.wangbaiyuan.translate.tools.PositionUtil;


public class LocationDetailActivity extends AppCompatActivity {

    private MapView mMapView;
    private UiSettings mUisettings;
    private BaiduMap mMap;
    private double latitude;
    private double longitude;
    private String addrStr;
    private LocationClient locationClient;
    private boolean isFirst=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
  
        setContentView(R.layout.activity_location_detail);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
//        setSupportActionBar(toolbar);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMap=mMapView.getMap();
        mMapView.removeViewAt(1);
        mMapView.showZoomControls(false);
        mMapView.getMap().showMapPoi(true);
        mMap.setMyLocationEnabled(true);

        mUisettings=mMapView.getMap().getUiSettings();
        mUisettings.setCompassEnabled(true);


        locationClient=new LocationClient(getBaseContext());
        LocationClientOption option=new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("gcj02");
        option.setPriority(LocationClientOption.GpsFirst);
        option.setProdName("bylocation");
        option.setScanSpan(3000);
        option.setIsNeedAddress(true);
        //option.set
        // option.set
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.requestLocation();

        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Gps location=PositionUtil.gcj02_To_Bd09(bdLocation.getLatitude(),bdLocation.getLongitude());
                latitude = location.getWgLat();
                longitude = location.getWgLon();
                addrStr = bdLocation.getAddrStr();

                    MyLocationData data=new MyLocationData.Builder()
                            .accuracy(bdLocation.getRadius())
                            .latitude(latitude)
                            .longitude(longitude)
                            .direction(100)
                            .speed(bdLocation.getSpeed())
                            .build();
             mMap.setMyLocationData(data);
                LatLng ll=new LatLng(latitude,longitude);
                float f=mMap.getMaxZoomLevel();
                MapStatusUpdate up=MapStatusUpdateFactory.newLatLngZoom(ll, f - 6);
                if(isFirst){
                    mMap.animateMapStatus(up);
                    isFirst=false;
                }


            }
        });
       // MapController mMapController=mMap.getController();
       // mUisettings.setCompassPosition(LogoPosition);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
