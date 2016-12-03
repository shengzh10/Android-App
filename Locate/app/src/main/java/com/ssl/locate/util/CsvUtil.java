package com.ssl.locate.util;

import android.os.Environment;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.ssl.locate.JavaBean.Record;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by sheng
 * on 2016/11/28.
 */

public class CsvUtil {

    private static String routs = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + "Location" + File.separator;

    @SuppressWarnings("all")
    public static void writeToFile(ArrayList<Record> records) {
        File fRouts = new File(routs);
        // 路径不存在创建路径
        if (!fRouts.exists())
            fRouts.mkdirs();

        String path = fRouts.getAbsolutePath() + File.separator + "location.csv";
        File file = new File(path);
        // 文件不存在创建文件
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            CsvWriter cw = new CsvWriter(path, ',', Charset.forName("GBK"));
            // 写表头
            String[] header = {"编号", "经度", "纬度", "项点名称"};
            cw.writeRecord(header);

            // 循环写信息
            for (Record record : records) {
                String[] line = {record.getNum(), record.getLongitude(), record.getLatitude(), record.getAddress()};
                cw.writeRecord(line);
            }

            cw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Record> readFromFile() {
        String path = routs + "location.csv";
        ArrayList<Record> records = new ArrayList<>();
        ArrayList<String[]> fileList = new ArrayList<>();

        try {
            CsvReader reader = new CsvReader(path, ',', Charset.forName("GBK"));
            reader.readHeaders();
            while (reader.readRecord()) {
                fileList.add(reader.getValues());
            }
            reader.close();
            for (int row = 0; row < fileList.size(); row++) {
                records.add(new Record(Integer.parseInt(fileList.get(row)[0]),
                        fileList.get(row)[1],
                        fileList.get(row)[2],
                        fileList.get(row)[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }
}
