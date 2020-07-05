package com.example.co_caro.game_caro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VanChoi {
    private String Key1;
    private String Key2;


    private Integer LuotDanh;
    private Integer ViTriDanh;
    private Integer NguoiThang;
    private ArrayList<Integer> ArrayList;


    public Map<String,Object> toMap(){
        Map<String,Object> maps = new HashMap<>();
        maps.put("key1",Key1);
        maps.put("key2",Key2);
        maps.put("luotDanh",LuotDanh);
        maps.put("viTriDanh",ViTriDanh);
        maps.put("nguoiThang",NguoiThang);
        maps.put("arrayList",ArrayList);
        return maps;

    }

    public VanChoi(String key1, String key2, Integer luotDanh, Integer viTriDanh, Integer nguoiThang, ArrayList<Integer> arrayList) {
        Key1 = key1;
        Key2 = key2;
        LuotDanh = luotDanh;
        ViTriDanh = viTriDanh;
        NguoiThang = nguoiThang;
        ArrayList = arrayList;
    }

    public VanChoi() {
    }

    public java.util.ArrayList<Integer> getArrayList() {
        return ArrayList;
    }

    public void setArrayList(java.util.ArrayList<Integer> arrayList) {
        ArrayList = arrayList;
    }

    public String getKey1() {
        return Key1;
    }

    public String getKey2() {
        return Key2;
    }

    public Integer getLuotDanh() {
        return LuotDanh;
    }

    public Integer getViTriDanh() {
        return ViTriDanh;
    }

    public Integer getNguoiThang() {
        return NguoiThang;
    }

    public void setKey1(String key1) {
        Key1 = key1;
    }

    public void setKey2(String key2) {
        Key2 = key2;
    }

    public void setLuotDanh(Integer luotDanh) {
        LuotDanh = luotDanh;
    }

    public void setViTriDanh(Integer viTriDanh) {
        ViTriDanh = viTriDanh;
    }

    public void setNguoiThang(Integer nguoiThang) {
        NguoiThang = nguoiThang;
    }
}
