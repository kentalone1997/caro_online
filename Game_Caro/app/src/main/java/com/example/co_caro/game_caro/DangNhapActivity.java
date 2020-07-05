package com.example.co_caro.game_caro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DangNhapActivity extends AppCompatActivity {

    Button btnchoionline, btnThoat;
    EditText txtTen;
    FirebaseDatabase database;
    DatabaseReference databaseNguoiChoi;
    String myKey; // Lưu key đã tạo trên firebase

    ArrayList<String> KeysNguoiChoi; // danh sách key của người chơi trên firebase. Sử dụng để cập nhật trạng thái 1 cho tất của người  chơi

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Hàm bắt đầu khi chạy Activity
        //Remove title bar

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< Ẩn title bar (Tên ứng dụng)

        setContentView(R.layout.activity_dang_nhap); // load giao diện dang_nhap

        database = FirebaseDatabase.getInstance(); // Kết nối vào node gốc của Database
        databaseNguoiChoi = database.getReference().child("NguoiChoiGame"); // Lưu node NguoiChoiGame

        btnchoionline = (Button)findViewById(R.id.btchoionline);
        txtTen = (EditText) findViewById(R.id.txtTen);
        btnThoat = (Button)findViewById(R.id.btnthoatgame);
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        KeysNguoiChoi = new ArrayList<String>();


        btnchoionline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //set trang thai tat ca nguoi choi khac = 1
                for(int i = 0; i < KeysNguoiChoi.size();i++)
                {
                    String key = KeysNguoiChoi.get(i);
                    databaseNguoiChoi.child(key).child("trangThai").setValue(1);

                }
                String ten = txtTen.getText().toString();
                if(ten.length() < 5)
                    Toast.makeText(DangNhapActivity.this,"Tên không hợp lệ. Vui lòng nhập lại",Toast.LENGTH_LONG).show();
                else
                {
                    btnchoionline.setEnabled(false);
                    Intent intent = new Intent(DangNhapActivity.this,MainActivity.class);
                    NguoiChoi nguoiChoi = new NguoiChoi(ten,0,"1");
                    String key = databaseNguoiChoi.push().getKey();
                    myKey = key;
                    Map<String,Object> data = new HashMap<>();
                    data.put(key, nguoiChoi.toMap());
                    databaseNguoiChoi.updateChildren(data);

                    intent.putExtra("myKey",key);
                    startActivity(intent);

                }



            }
        });

        databaseNguoiChoi.addChildEventListener(new ChildEventListener() { // hàm này được gọi khi Activity chạy
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try
                {
                    NguoiChoi nguoiChoi = dataSnapshot.getValue(NguoiChoi.class); // ép kiểu dữ liệu về class NguoiChoi
                    String key = dataSnapshot.getKey();
                    KeysNguoiChoi.add(key);
                    Log.d("Nguoi Choi: ", nguoiChoi.getTenNguoiChoi());
                }catch (Exception e)
                {
                    Log.d("Error Firebase: ",e.toString());

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
}
