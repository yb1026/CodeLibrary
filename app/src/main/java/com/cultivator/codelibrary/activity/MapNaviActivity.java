package com.cultivator.codelibrary.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.cultivator.codelibrary.R;
import com.cultivator.codelibrary.base.BaseActivity;
import com.cultivator.codelibrary.location.MLocation;
import com.cultivator.codelibrary.location.SimpleLocationResource;
import com.cultivator.codelibrary.util.Utils;


/**
 * Created by yb1026 on 2016/12/19.
 */
public class MapNaviActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapnavi_test);
        initView();
    }


    public void initView() {
        getToolBar().setTitle("第三方地图测试");

        final TextView lat = (TextView) findViewById(R.id.latitude);
        final TextView lng = (TextView) findViewById(R.id.longitude);
        final TextView addr = (TextView) findViewById(R.id.addr);


        SimpleLocationResource.getInstance().requestLocation(this, new Runnable() {
            @Override
            public void run() {

                MLocation location = SimpleLocationResource.getInstance().getLocation();
                lat.setText(String.valueOf(location.getSpecLatitude()));
                lng.setText(String.valueOf(location.getSpecLongitude()));
                addr.setText(String.valueOf(location.getAddress()));
            }
        });


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Utils.navigation(MapNaviActivity.this, lat.getText().toString(),
                        lng.getText().toString(), addr.getText().toString());
            }
        });
    }


    @Override
    public void onBack() {
        SimpleLocationResource.getInstance().unRegister();
        this.finish();
    }


}
