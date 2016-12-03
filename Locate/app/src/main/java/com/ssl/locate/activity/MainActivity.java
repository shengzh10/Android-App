package com.ssl.locate.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.ssl.locate.R;
import com.ssl.locate.util.CsvUtil;
import com.ssl.locate.util.LocationService;
import com.ssl.locate.util.MyApplication;
import com.ssl.locate.JavaBean.Record;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String info = "编号\t\t经度\t\t纬度\t\t地址\n"; // TextView显示信息
    private int num = 1;    // 记录编号
    private LocationService locationService;
    private TextView infoTv;
    private ArrayList<Record> records = new ArrayList<>();  // 记录列表
    private AlertDialog alertDialog;
    private TextView confirmTv;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTv = (TextView) findViewById(R.id.tv_main_info);
        Button getLocationBtn = (Button) findViewById(R.id.btn_main_get_location);
        Button writeFileBtn = (Button) findViewById(R.id.btn_main_write_to_file);
        Button readFileBtn = (Button) findViewById(R.id.btn_main_read_from_file);

        infoTv.setText(info);

        // 定位
        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirm();
                // 开启定位
                locationService.start();
            }
        });

        // 写文件
        writeFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CsvUtil.writeToFile(records);
                new AlertDialog.Builder(MainActivity.this).setTitle("写入完毕").
                        setPositiveButton("确定", null).show().setCancelable(false);
            }
        });

        // 读文件
        readFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                records = CsvUtil.readFromFile();
                num = records.size() + 1;
                info = "编号\t\t经度\t\t纬度\t\t地址\n";
                for (Record record : records) {
                    info = info + record.getNum() + "\t\t" + record.getLongitude() + "\t\t"
                            + record.getLatitude() + "\n\t\t" + record.getAddress() + "\n";
                }
                infoTv.setText(info);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationService = ((MyApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.unregisterListener(mListener);
        locationService.stop();
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                latitude = "" + location.getLatitude();      // 纬度
                longitude = "" + location.getLongitude();    // 经度
                String result;
                boolean isSuccess;

                switch (location.getLocType()) {
                    case BDLocation.TypeGpsLocation:
                        result = "gps定位成功";
                        isSuccess = true;
                        break;
                    case BDLocation.TypeNetWorkLocation:
                        result = "网络定位成功";
                        isSuccess = true;
                        break;
                    case BDLocation.TypeOffLineLocation:
                        result = "离线定位成功，离线定位结果也是有效的";
                        isSuccess = true;
                        break;
                    case BDLocation.TypeServerError:
                        result = "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因";
                        isSuccess = false;
                        break;
                    case BDLocation.TypeNetWorkException:
                        result = "网络不同导致定位失败，请检查网络是否通畅";
                        isSuccess = false;
                        break;
                    case BDLocation.TypeCriteriaException:
                        result = "无法获取有效定位依据导致定位失败，一般是由于手机的原因，" +
                                "处于飞行模式下一般会造成这种结果，可以试着重启手机";
                        isSuccess = false;
                        break;
                    default:
                        result = "定位失败, 请打开网络和GPS重新定位";
                        isSuccess = false;
                        break;
                }

                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();

                // 定位成功
                if (isSuccess) {
                    confirmTv.setText("经度："+ longitude + "\n纬度：" + latitude);
                    locationService.stop();
                }
            }
        }
    };

    // 确认提示框
    private void showConfirm() {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(new EditText(this));
        alertDialog.show();
        alertDialog.setCancelable(false);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setContentView(R.layout.alertdialog_confirm);
        }
        Button okBtn = (Button) alertDialog.getWindow().findViewById(R.id.btn_confirm_ok);
        Button cancelBtn = (Button) alertDialog.getWindow().findViewById(R.id.btn_confirm_cancel);
        confirmTv = (TextView) alertDialog.getWindow().findViewById(R.id.tv_confirm_title);
        confirmTv.setText("正在定位");
        final EditText addressEt = (EditText) alertDialog.getWindow().findViewById(R.id.et_confirm_address);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEt.getText().toString();
                // 输入地址不为空且已经获得定位
                if (!"".equals(address) && !"正在定位".equals(confirmTv.getText().toString())) {
                    info = info + num + "\t\t" + longitude + "\t\t" + latitude + "\n\t\t" + address + "\n";
                    infoTv.setText(info);
                    records.add(new Record(num, longitude, latitude, address));
                    num++;
                    alertDialog.dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("确定退出？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    }).show();
        }
        return false;
    }
}
