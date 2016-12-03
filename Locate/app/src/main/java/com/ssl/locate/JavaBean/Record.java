package com.ssl.locate.JavaBean;

/**
 * Created by sheng
 * on 2016/11/28.
 */

public class Record {
    private String longitude;   // 经度
    private String latitude;    // 纬度
    private int num;
    private String address;     // 地点

    public Record(int num, String longitude, String latitude, String address) {
        this.num = num;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    public String getNum() {
        return "" + num;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }
}
