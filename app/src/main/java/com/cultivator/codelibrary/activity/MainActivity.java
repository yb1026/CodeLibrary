package com.cultivator.codelibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cultivator.codelibrary.R;
import com.cultivator.codelibrary.base.BaseActivity;
import com.cultivator.codelibrary.log.MyLog;
import com.cultivator.draggridview.DragCallback;
import com.cultivator.draggridview.DragGridView;
import com.cultivator.draggridview.DragGridViewAdapter;
import com.cultivator.draggridview.HomeGridItem;
import com.google.zxing.client.android.QRActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private DragGridView mDragGridView;
    private DragGridViewAdapter mAdapter;
    private List<HomeGridItem> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    public void initView() {
        getToolBar().setBarVisibility(false);
        mDragGridView = (DragGridView) findViewById(R.id.home_grideview);
        initDragGrid();


        findViewById(R.id.qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去扫码
                startActivityForResult(new Intent(MainActivity.this, MyQrActivity.class), 0);

            }
        });


        findViewById(R.id.sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignActivity.class);
            }
        });
    }

    public void initDragGrid() {
        initData();
        MyLog.d(getClass(), "initDragGrid...：" + list.size());
        if (mAdapter == null) {
            mAdapter = new DragGridViewAdapter(this);
            mAdapter.setlist(list);
            mDragGridView.setAdapter(mAdapter);
            mDragGridView.setDragCallback(new DragCallback() {
                @Override
                public void startDrag(int position) {
                    MyLog.i("start drag at " + position);
                }

                @Override
                public void endDrag(int position) {
                    MyLog.i( "end drag at " + position);
                }
            });
            mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HomeGridItem item = list.get(position);
                    if (item.getMipmapId() != 0) {
                        startActivity(item.getActivity());
                        mDragGridView.clicked(position);
                    }
                }
            });
            mDragGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    HomeGridItem item = list.get(position);
                    if (item.getMipmapId() != 0) {
                        mDragGridView.startDrag(position);
                    }
                    return false;
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void initData() {

        String[] names = {
                "音屏震动",
                "打开地图",
                "自定义1"
        };

        ArrayList<Class> classes = new ArrayList<>();

        classes.add(VibratorRingActivity.class);
        classes.add(MapNaviActivity.class);
        classes.add(ViewDemoActivity.class);


        for (int i = 0; i < names.length; i++) {
            HomeGridItem item = new HomeGridItem();
            item.setMipmapId(R.mipmap.ic_launcher);
            item.setName(names[i]);
            item.setActivity(classes.get(i));
            list.add(item);
        }

        HomeGridItem item = new HomeGridItem();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null) {
            String result = data.getStringExtra("result");
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }

    }

}
