package com.example.administrator.yanlin_i6300a;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.*;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class N5Activity extends BaseActivity {

    @BindView(R.id.main_tv)
    TextView mainTv;
    @BindView(R.id.main_btn_chongzhi)
    Button mainBtnChongzhi;
    @BindView(R.id.main_btn)
    Button mainBtn;
    @BindView(R.id.main_lrv)
    LRecyclerView mainLrv;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.main_sum_tv)
    TextView mainSumTv;
    private LRecyclerViewAdapter lRecyclerViewAdapter = null;
    private CommonAdapter<String> adapter;
    private List<String> datas = new ArrayList<>(); //PDA机屏幕上的List集合
    private SweetAlertDialog sweetAlertDialog;
    private File file;
    private ArrayList<ArrayList<String>> recordList;
    private static String[] title = {"条码", "扫描时间"};
    private SweetAlertDialog MEIDDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n5);
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
        Log.d("feng",tm.getDeviceId());
        if (tm.getDeviceId().equals("86109403035420")) {  //86109403061387   86109403035420
            intiAdapter();
        } else {
            initDialog();
        }
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

    @Override
    public void updateCount() {

    }

    @Override
    public void updateList(String data) {
        timeTv.setText(DateUtils.getCurrentTime3());
        mainTv.setText(data);
//        srcBean=new SrcBean(data,DateUtils.getCurrentTime4());
//                srcBean.setSrc(scanResult);
//                srcBean.setTime(DateUtils.getCurrentTime4());
        datas.add(data);
        lRecyclerViewAdapter.notifyDataSetChanged();
        mainSumTv.setText("数量：" + datas.size());
    }

    @OnClick({R.id.main_btn_chongzhi, R.id.main_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_btn_chongzhi:
                initReset();
                break;
            case R.id.main_btn:
                exportExcel(DateUtils.getCurrentTime1());
                break;
        }
    }

    private void initReset() {
        mainTv.setText("");
        timeTv.setText("");
        datas.clear();
        lRecyclerViewAdapter.notifyDataSetChanged();
        mainSumTv.setText("数量：" + datas.size());
    }


    private void intiAdapter() {
        adapter = new CommonAdapter<String>(this, R.layout.adapter_n5, datas) {
            @Override
            public void setData(ViewHolder holder, String s) {
                holder.setText(R.id.adapter_src, s);
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
                sweetAlertDialog = new SweetAlertDialog(N5Activity.this, SweetAlertDialog.WARNING_TYPE);
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
                        mainSumTv.setText("数量：" + datas.size());
                    }
                });
                sweetAlertDialog.show();
            }
        });
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
        initReset();
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
            beanList.add(datas.get(i));
            if (i > 0) {
                beanList.add("");
            } else {
                beanList.add(timeTv.getText().toString());
            }
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
