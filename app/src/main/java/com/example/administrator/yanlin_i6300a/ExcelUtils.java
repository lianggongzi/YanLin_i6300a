package com.example.administrator.yanlin_i6300a;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Administrator on 2018\9\5 0005.
 */

public class ExcelUtils {
    public static WritableFont arial14font = null;

    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";


    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    public static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);
            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);
            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);//对齐格式
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN); //设置边框

        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Excel
     * @param fileName
     * @param colName
     */
    public static void initExcel(String fileName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("出库明细", 0);
            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, fileName,arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            sheet.setRowView(0,340); //设置行高

            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList,String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName),workbook);
                WritableSheet sheet = writebook.getSheet(0);

//              sheet.mergeCells(0,1,0,objList.size()); //合并单元格
//              sheet.mergeCells()

                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i),arial12format));
                        if (list.get(i).length() <= 5){
                            sheet.setColumnView(i,list.get(i).length()+8); //设置列宽
                        }else {
                            sheet.setColumnView(i,list.get(i).length()+5); //设置列宽
                        }
                    }
                    sheet.setRowView(j+1,350); //设置行高
                }

                writebook.write();
                Toast.makeText(c, "导出到手机存储中文件夹Record成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }





    /**
     * 初始化Excel
     *
     * @param fileName
     * @param colName
     */
    public static <T> void initExcels(List<T> objList, String fileName, String[] colName, String excelName, Context
            c) {
        format();
        WritableWorkbook workbook = null;

        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(excelName, 0);

            //创建标题栏
//            sheet.addCell((WritableCell) new Label(0, 0, fileName, arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
//            sheet.setRowView(0, 340); //设置行高
            for (int j = 0; j < objList.size(); j++) {
                ArrayList<String> list = (ArrayList<String>) objList.get(j);
                for (int i = 0; i < list.size(); i++) {
                    String string=list.get(i).toString();
                    sheet.addCell(new Label(i, j+1 , string));
                    if (list.get(i).length() <= 5) {
                        sheet.setColumnView(i, list.get(i).length() + 8); //设置列宽
                    } else {
                        sheet.setColumnView(i, list.get(i).length() + 5); //设置列宽
                    }
                }
                sheet.setRowView(j + 1, 350); //设置行高
            }
            workbook.write();
//            SPUtils.put(c,"fileName",fileName);
//            SPUtils.put(c,"time",excelName);
            Toast.makeText(c, "导出到手机存储中文件夹Record成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcels(List<T> objList, String fileName, String excelName, Context
            c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {

                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(file);
                Workbook workbook = Workbook.getWorkbook(file);//旧
                Sheet oldsheet = workbook.getSheet(0);//旧

                writebook = Workbook.createWorkbook(file);
                WritableSheet sheet = writebook.createSheet(excelName, 0);

                int rows = oldsheet.getRows();
                int columns = oldsheet.getColumns();
                Log.d("feng", rows + "----" + columns);
                //遍历excel文件的每行每列
                for (int j = 0; j < rows; j++) {
                    //遍历行

                    for (int i = 0; i < columns; i++) {
                        Cell cell = oldsheet.getCell(i, j);
                        String result = cell.getContents();
                        sheet.addCell(new Label(i, j, result));
//                        sheet.addCell(new Label(i, j, list.get(i), arial12format));
                        Log.d("feng",i+"-----");
                    }
                }
                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
//                        if (i==0){
//                            sheet.addCell(new Label(i, rows + j,String.valueOf(rows + j) , arial12format));//重置Excel序号
//                        }else {
                        String string=list.get(i).toString();
                        int hang=rows + j;
                            sheet.addCell(new Label(i, rows + j, string));
                        Log.d("feng",j+"-----"+i+"------"+hang);
//                        }

                    }
//                    sheet.setRowView(j + 1, 350); //设置行高
                }

//                SPUtils.put(c,"fileName",fileName);
//                SPUtils.put(c,"time",excelName);
                writebook.write();
                Toast.makeText(c, "导出到手机存储中文件夹Record成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
