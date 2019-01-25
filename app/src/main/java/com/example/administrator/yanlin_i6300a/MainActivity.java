package com.example.administrator.yanlin_i6300a;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity {
    BroadcastReceiver scanReceiver;
    IntentFilter intentFilter;
    private static final String RES_ACTION = "android.intent.ACTION_DECODE_DATA";
//    private static final String RES_ACTION = "android.intent.action.SCANRESULT";
    @BindView(R.id.main_tv)
    TextView mainTv;
    @BindView(R.id.main_btn_chongzhi)
    Button mainBtnChongzhi;
    @BindView(R.id.main_btn)
    Button mainBtn;
    @BindView(R.id.main_lrv)
    LRecyclerView mainLrv;

    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<SrcBean> adapter;
    private List<SrcBean> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    private SweetAlertDialog sweetAlertDialog;
    private File file;
    private ArrayList<ArrayList<String>> recordList;
    private static String[] title = {"条码","扫码时间"};
    SrcBean srcBean;
    private SweetAlertDialog MEIDDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MEIDDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        MEIDDialog.setCancelable(false);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        Log.d("feng",tm.getDeviceId());
//        if (tm.getDeviceId().equals("864021031498422")) {//864021031498349 864021031498398  864021031498430  864021031498422
//
//        } else {
//            initDialog();
//        }

        //扫描结果的意图过滤器的动作一定要使用"android.intent.action.SCANRESULT"
        intentFilter = new IntentFilter(RES_ACTION);
        //注册广播接受者
        scanReceiver = new ScannerResultReceiver();
        registerReceiver(scanReceiver, intentFilter);
        intiAdapter();
    }
    private void initDialog() {
        MEIDDialog
                .setTitleText("请联系商家");
        MEIDDialog.setConfirmText("确定");
        MEIDDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                MEIDDialog.dismiss();
                System.exit(0);
            }
        });
        MEIDDialog.show();
    }

    private class ScannerResultReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RES_ACTION)) {
                //获取扫描结果
                final String scanResult = intent.getStringExtra("barcode_string");
                mainTv.setText(scanResult);
                srcBean=new SrcBean(scanResult,DateUtils.getCurrentTime4());
//                srcBean.setSrc(scanResult);
//                srcBean.setTime(DateUtils.getCurrentTime4());
                datas.add(srcBean);
                lRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void intiAdapter() {
        adapter = new CommonAdapter<SrcBean>(this, R.layout.adapter_main, datas) {
            @Override
            public void setData(ViewHolder holder, SrcBean srcBean) {
                holder.setText(R.id.adapter_src, srcBean.getSrc());
                holder.setText(R.id.adapter_time,srcBean.getTime() );
            }
        };
        mainLrv.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mainLrv.setAdapter(lRecyclerViewAdapter);
        mainLrv.setLoadMoreEnabled(false);
        mainLrv.setPullRefreshEnabled(false);
        lRecyclerViewAdapter.setOnItemClickListener(new com.github.jdsjlzx.interfaces.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelText("取消");
                sweetAlertDialog.setTitleText("确定删除此条信息?");
                sweetAlertDialog.setConfirmText("确定");
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        datas.remove(position);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.show();
            }
        });
    }

    @OnClick({R.id.main_btn_chongzhi, R.id.main_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_btn_chongzhi:
                mainTv.setText("");
                datas.clear();
                lRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case R.id.main_btn:
                exportExcel(DateUtils.getCurrentTime1());
                mainTv.setText("");
                datas.clear();
                lRecyclerViewAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 导出excel
     *
     * @param
     */
    public void exportExcel(String excelName) {
        file = new File(getSDPath() + "/Record");
        makeDir(file);
//        String fileName = (String) SPUtils.get(getActivity(), "fileName", "");
//        if (fileName.equals("")) {
//            String excelFile = file.toString() + "/" + excelName + ".xls";
//            ExcelUtils.initExcels(getRecordData(), excelFile, title, excelName, getActivity());
////            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName,  getActivity());
//        } else {
//            ExcelUtils.writeObjListToExcels(getRecordData(), fileName, excelName, getActivity());
//        }
        ExcelUtils.initExcel(file.toString() + "/" + excelName + ".xls", title);
        ExcelUtils.writeObjListToExcel(getRecordData(), file.toString() + "/" + excelName + ".xls", this);
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     *
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData() {
        recordList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            ArrayList<String> beanList = new ArrayList<String>();
//            beanList.add("1");
            beanList.add(datas.get(i).getSrc());
            beanList.add(datas.get(i).getTime());
            recordList.add(beanList);

        }
        return recordList;
    }

    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }
}