package com.example.administrator.yanlin_i6300a;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.widget.Button;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TextActivity extends AppCompatActivity {

    @BindView(R.id.text_btn)
    Button textBtn;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        ButterKnife.bind(this);
    }

    //默认是没有换行的
    public static void initSettings(final File settings) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(settings);
                    XmlSerializer serializer = Xml.newSerializer();
                    serializer.setOutput(fos, "UTF-8");
                    serializer.startDocument("UTF-8", true);
                    serializer.startTag(null, "config");
                    serializer.startTag(null, "category");
                    serializer.attribute(null, "name", "hot");
                    // server
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "server");
                    serializer.attribute(null, "value", "");
                    serializer.endTag(null, "item");
                    // hid
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "hotel");
                    serializer.attribute(null, "value", "");
                    serializer.endTag(null, "item");
                    // room
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "room");
                    serializer.attribute(null, "value", "");
                    serializer.endTag(null, "item");

                    serializer.endTag(null, "category");
                    serializer.endTag(null, "config");
                    serializer.endDocument();
                    serializer.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    @OnClick(R.id.text_btn)
    public void onViewClicked() {
//        file = new File(getSDPath() + "/Record"+"/feng"+".xml");
//        initSettings(file);

        aa();
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


    private void aa() {
        String name = "fengge";
        String age = "23";
        String Id = "123456";

//   String  s1="\""+name+"\"";
        File file = new File(getSDPath() + "/Record" + "/feng3" + ".xml");
        try {
            FileOutputStream fos = new FileOutputStream(file);

            StringBuffer sb = new StringBuffer();
            //开始写入xml里的内容
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<Document>\n");
            sb.append("<Events>\n");
            sb.append("<Event Name=\"SalesWareHouseOut\" MainAction= \"WareHouseOut\">\n");
            sb.append("<DataField>\n");
            for (int i = 0; i <10 ; i++) {
                sb.append("<Data Code=\"" + name + "\" CorpOrderID=\"" + age + "\" Actor=\"" + Id + "\"/>");
            }
            sb.append("</DataField>\n");
            sb.append("</Event>\n");
            sb.append("</Events>\n");
            sb.append("</Document>\n");
            fos.write(sb.toString().getBytes());
            fos.close();//关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
