package com.example.co_caro.game_caro;

import java.util.HashMap;
import java.util.Map;

public class NguoiChoi {
    private String TenNguoiChoi;
    private Integer TrangThai;
    private String KeyVanCo;







    public NguoiChoi() { // Phải có phương thức này để ép kiểu dữ liệu từ Firebase về nguoiChoi

    }

    public NguoiChoi(String tenNguoiChoi, Integer trangThai, String keyVanCo) {
        TenNguoiChoi = tenNguoiChoi;
        TrangThai = trangThai;
        KeyVanCo = keyVanCo;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> maps = new HashMap<>();
        maps.put("tenNguoiChoi",TenNguoiChoi);
        maps.put("trangThai",TrangThai);
        maps.put("keyVanCo", KeyVanCo);
        return maps;

    }


    public String getTenNguoiChoi() {
        return TenNguoiChoi;
    }

    public Integer getTrangThai() {
        return TrangThai;
    }

    public String getKeyVanCo() {
        return KeyVanCo;
    }

    public void setTenNguoiChoi(String tenNguoiChoi) { TenNguoiChoi = tenNguoiChoi; }
    public void setTrangThai(Integer trangThai) {
        TrangThai = trangThai;
    }
    public void setKeyVanCo(String keyVanCo) {
        KeyVanCo = keyVanCo;
    }
}
