package com.example.co_caro.game_caro;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    AdapterGridview adapterGridview;
    ArrayList<Integer> arrayList;
    String myKey;
    String rivalKey;
    String myGameKey;
    int LoaiCo;
    int sodong = 20;
    int socot = 12;

    final long TIME_WAITING = 30000; // thời gian chờ
    int LuotDanh;
    int KTVanCo; // Chưa tạo bàn cờ = 0
    int KTCho;
    int DangChoi;
    FirebaseDatabase database;
    DatabaseReference databaseNguoiChoi;
    DatabaseReference databaseVanChoi;


    ArrayList<NguoiChoi> arrayListNguoiChoi;
    ArrayList<VanChoi> arrayListVanChoi;

    ArrayList<String> KeysNguoiChoi;
    ArrayList<String> KeysVanChoi;
    Dialog dialog_waiting;
    Dialog dialog_thongbao;
    Integer value;
    TextView txtTenNguoiChoi1;
    TextView txtTenNguoiChoi2;

    // count down timer
    CountDownTimer countDownTimer;
    CountDownTimer countDownTimer_DoiThu;
    long timeleft = TIME_WAITING;
    AlertDialog alertDialog;


    TextView countdowntext;
    boolean navigationBarVisibility = true; //because it's visible when activity is created


    int myscore;
    int rivalscore;

    TextView txtDiemX;
    TextView txtDiemO;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this,DangNhapActivity.class);
        startActivity(intent);
    }
    private void resetDataBanCo()
    {
        for(int i = 0; i < sodong*socot;i++)
            arrayList.set(i,0);
    }
    private void showDialogThang()
    {
        myscore++;
        UpdateScore();
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog_Alert);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_thongbao,(ConstraintLayout)findViewById(R.id.layoutDialogcontainer));
        builder.setView(view);
        ((TextView) view.findViewById(R.id.txtThongBao)).setText("Thắng rồi");
        alertDialog = builder.create();
        alertDialog.show();
        countDownTimer_DoiThu.cancel();
        countDownTimer.cancel();
        ((Button) view.findViewById(R.id.btnchoilai)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDataBanCo();
                adapterGridview.ConverArrayListToArray(arrayList);
                adapterGridview.notifyDataSetChanged();
                VanChoi vanChoi = new VanChoi(myKey, rivalKey, 1, -2, 0, arrayList);
                databaseVanChoi.child(myGameKey).setValue(vanChoi);
                Toast.makeText(getBaseContext(), "Đã tạo ván cờ. mời bạn đánh trước", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
                resetTimer();
                countDownTimer.start();
                gridView.setEnabled(true);

            }
        });
        ((Button) view.findViewById(R.id.btnthoat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(MainActivity.this,DangNhapActivity.class);
                startActivity(intent);

            }
        });

    }


    private void showDialogThua()
    {
        rivalscore++;
        UpdateScore();
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog_Alert);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_thongbao,(ConstraintLayout)findViewById(R.id.layoutDialogcontainer));
        builder.setView(view);
        ((TextView) view.findViewById(R.id.txtThongBao)).setText("Thua rồi");
        alertDialog = builder.create();
        alertDialog.show(); // showw
        countDownTimer_DoiThu.cancel();
        countDownTimer.cancel();
        ((Button) view.findViewById(R.id.btnchoilai)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDataBanCo();
                adapterGridview.ConverArrayListToArray(arrayList);
                adapterGridview.notifyDataSetChanged();
                VanChoi vanChoi = new VanChoi(myKey, rivalKey, 1, -2, 0, arrayList);
                databaseVanChoi.child(myGameKey).setValue(vanChoi);
                Toast.makeText(getBaseContext(), "Đã tạo ván cờ. mời bạn đánh trước", Toast.LENGTH_LONG).show();
                resetTimer();
                countDownTimer.start(); // chạy countdown
                alertDialog.dismiss(); // tắt dialog
                gridView.setEnabled(true);

            }
        });
        ((Button) view.findViewById(R.id.btnthoat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(MainActivity.this,DangNhapActivity.class);
                startActivity(intent);
            }
        });

    }
    private  void UpdateScore() {
        if (LoaiCo == 1) {
            txtDiemO.setText(myscore + "");
            txtDiemX.setText(rivalscore + "");
        }
        else
        {
            txtDiemO.setText(rivalscore + "");
            txtDiemX.setText(myscore + "");
        }
    }
    private void TaoVanCo()
    {
        // Đếm số người chơi có trạng thái = 0 (Không tính người chơi hiện tại)
        String doithu = "";
        int vt = 0;
        for (int i = 0; i < arrayListNguoiChoi.size(); i++) {
            if (arrayListNguoiChoi.get(i).getTrangThai() == 0 && KeysNguoiChoi.get(i).equals(myKey) == false) {
                KTCho = 1;
                Log.d("SoSanh: ", myKey + " vs " + KeysNguoiChoi.get(i));
                doithu = KeysNguoiChoi.get(i); // Lưu lại
                vt = i;
                break;
            }


        }

        //Nếu có người đang chờ
        if (KTCho == 1) {
            dialog_waiting.dismiss(); //Đóng cửa sổ "Tìm đối thủ..."
            // Kiểm tra trong danh sách ván cờ đã có chưa?
            for (int j = 0; j < arrayListVanChoi.size(); j++) {
                String keyvanchoi = KeysVanChoi.get(j);
                VanChoi vanChoi = arrayListVanChoi.get(j);
                if (vanChoi.getNguoiThang() == 0 && vanChoi.getKey1().equals(myKey)) {

                    KTCho = 0;
                    DangChoi = 1;
                    LuotDanh = 1;
                    rivalKey = arrayListVanChoi.get(j).getKey2();

                    KTVanCo = 1; // Đã tạo ván cờ
                    return;
                }

                if (vanChoi.getNguoiThang() == 0 && vanChoi.getKey2().equals(myKey)) {
                    DangChoi = 1;
                    KTVanCo = 1; // Đã tạo ván cờ
                    KTCho = 0;
                    LuotDanh = 2;
                    rivalKey = arrayListVanChoi.get(j).getKey1();
                    myGameKey = keyvanchoi;
                    return;

                }


            }
            // Nếu ván cờ chưa tạo
            if (KTVanCo == 0) {
                //Tạo ván cờ
                VanChoi vanChoi = new VanChoi(myKey, doithu, 1, -1, 0, arrayList);
                String keyvanchoi = databaseVanChoi.push().getKey();
                Map<String, Object> data = new HashMap<>();
                data.put(keyvanchoi, vanChoi.toMap());
                databaseVanChoi.updateChildren(data);
                myGameKey = keyvanchoi;
                rivalKey = doithu;
                LuotDanh = 1;
                DangChoi = 1;
                LoaiCo = 1;

                // Cập nhật lại danh sách


                int myid = KeysNguoiChoi.indexOf(myKey);
                int rivalid = KeysNguoiChoi.indexOf(rivalKey);
                Log.d("ACB","myid" + myid);
                Log.d("ACB","riavalid" + rivalid);
                NguoiChoi nguoiChoi1 = arrayListNguoiChoi.get(myid);
                NguoiChoi nguoiChoi2 = arrayListNguoiChoi.get(rivalid);

                // Đã chơi
                nguoiChoi1.setTrangThai(1);
                nguoiChoi2.setTrangThai(1);

                arrayListNguoiChoi.set(myid, nguoiChoi1);
                arrayListNguoiChoi.set(rivalid, nguoiChoi2);
                // Run timer
                countDownTimer.start();

                Toast.makeText(getBaseContext(), "Đã tạo ván cờ. mời bạn đánh trước", Toast.LENGTH_LONG).show();

            }


        } else
            Log.d("Trang Thái game: ", "Đang chờ");


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) { // Bắt đầu xử lý khi MainActitvy chạy
        super.onCreate(savedInstanceState);
        ; //<< Ẩn TitleBar
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        //Khoi tao diem;

        myscore = 0;
        rivalscore = 0;

        myGameKey = "";
        LuotDanh = 1;
        DangChoi = 0;
        KTVanCo = 0; // CHưa tạo ván cờ
        KTCho = 0;// ko có người chờ
        database = FirebaseDatabase.getInstance();
        databaseNguoiChoi = database.getReference().child("NguoiChoiGame");
        databaseVanChoi = database.getReference().child("VanChoi");


        value = 0;
        arrayListNguoiChoi = new ArrayList<NguoiChoi>();
        arrayListVanChoi = new ArrayList<VanChoi>();
        KeysNguoiChoi = new ArrayList<String>();
        KeysVanChoi = new ArrayList<String>();


        final Intent intent = getIntent();
        myKey = intent.getStringExtra("myKey");


        // Đỗ dữ liệu vào Grivide

        gridView = (GridView) findViewById(R.id.gridview); // ánh xạ biến
        gridView.setVerticalScrollBarEnabled(false); // Ẩn thanh scrollbar
        arrayList = new ArrayList<Integer>(); // Khởi tạo Dữ liệu gridview
        adapterGridview = new AdapterGridview(this, R.layout.activity_item, arrayList, 1, sodong, socot); // Tạo adapter
        gridView.setAdapter(adapterGridview); // Set adapter cho gridview
        setData(); // Khởi tạo dữ liệu bàn cờ cho arraylist


        dialog_waiting = new Dialog(this);
        dialog_waiting.setContentView(R.layout.layout_dialog_waiting);
        dialog_waiting.setCancelable(false);
        dialog_waiting.show();


        txtTenNguoiChoi1 = (TextView) findViewById(R.id.txtTenNguoiChoi1);
        txtTenNguoiChoi2 = (TextView) findViewById(R.id.txtTenNguoiChoi2);

        txtDiemO = (TextView) findViewById(R.id.txtDiemO);
        txtDiemX = (TextView) findViewById(R.id.txtDiemX);


        //Count down
        countdowntext = (TextView) findViewById(R.id.Timer);
        countDownTimer = new CountDownTimer(timeleft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeleft = millisUntilFinished;
                updateTimer(); // cập nhật dữ liệu lên TextView
            }

            @Override
            public void onFinish() {
                showDialogThua();
                countDownTimer.cancel();
                countDownTimer_DoiThu.cancel();

            }
        }.start();
        resetTimer();
        countDownTimer_DoiThu = new CountDownTimer(timeleft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                showDialogThang();
                countDownTimer.cancel();
                countDownTimer_DoiThu.cancel();

            }
        }.start();
        countDownTimer_DoiThu.cancel();
        // reset timer
        resetTimer();




        databaseVanChoi.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                VanChoi vanChoi = dataSnapshot.getValue(VanChoi.class);
                arrayListVanChoi.add(vanChoi);
                KeysVanChoi.add(dataSnapshot.getKey());


                if (vanChoi.getKey1().equals(myKey)) {
                    adapterGridview.setLoaico(1);
                    LuotDanh = 1;
                    DangChoi = 1;
                    KTVanCo = 1;
                    ArrayList<Integer> arrayList1 = vanChoi.getArrayList();
                    setDataArrayList(arrayList1);


                    rivalKey = vanChoi.getKey2();
                    myGameKey = dataSnapshot.getKey();
                    // Cập nhật lại trạng thái của Người chơi
                    databaseNguoiChoi.child(myKey).child("keyVanCo").setValue(myGameKey);
                    databaseNguoiChoi.child(myKey).child("trangThai").setValue(1);
                    databaseNguoiChoi.child(rivalKey).child("keyVanCo").setValue(myGameKey);
                    databaseNguoiChoi.child(rivalKey).child("trangThai").setValue(1);
                    // Cập nhật lại danh sách


                    int myid = KeysNguoiChoi.indexOf(myKey);
                    int rivalid = KeysNguoiChoi.indexOf(rivalKey);
                    NguoiChoi nguoiChoi1 = arrayListNguoiChoi.get(myid);
                    NguoiChoi nguoiChoi2 = arrayListNguoiChoi.get(rivalid);

                    nguoiChoi1.setTrangThai(1);
                    nguoiChoi2.setTrangThai(1);

                    txtTenNguoiChoi2.setText(nguoiChoi1.getTenNguoiChoi() + " (Bạn)");
                    txtTenNguoiChoi1.setText(nguoiChoi2.getTenNguoiChoi());
                    arrayListNguoiChoi.set(myid, nguoiChoi1);
                    arrayListNguoiChoi.set(rivalid, nguoiChoi2);

                }

                if (vanChoi.getKey2().equals(myKey)) {
                    adapterGridview.setLoaico(2);
                    LuotDanh = 2;
                    DangChoi = 1;
                    KTVanCo = 1;
                    ArrayList<Integer> arrayList1 = vanChoi.getArrayList();
                    setDataArrayList(arrayList1);
                    rivalKey = vanChoi.getKey1();
                    myGameKey = dataSnapshot.getKey();
                    resetTimer();
                    countDownTimer_DoiThu.start();
                    Toast.makeText(getBaseContext(), "Vui lòng chờ...", Toast.LENGTH_LONG).show();
                    // Cập nhật lại trạng thái của Người chơi
                    databaseNguoiChoi.child(myKey).child("keyVanCo").setValue(myGameKey);
                    databaseNguoiChoi.child(myKey).child("trangThai").setValue(1);

                    databaseNguoiChoi.child(rivalKey).child("keyVanCo").setValue(myGameKey);
                    databaseNguoiChoi.child(rivalKey).child("trangThai").setValue(1);
                    gridView.setEnabled(false);

                    // Cập nhật lại danh sách


                    int myid = KeysNguoiChoi.indexOf(myKey);
                    int rivalid = KeysNguoiChoi.indexOf(rivalKey);
                    if(myid !=-1 && rivalid !=-1) {
                        NguoiChoi nguoiChoi1 = arrayListNguoiChoi.get(myid);
                        NguoiChoi nguoiChoi2 = arrayListNguoiChoi.get(rivalid);

                        nguoiChoi1.setTrangThai(1);
                        nguoiChoi2.setTrangThai(1);

                        txtTenNguoiChoi1.setText(nguoiChoi1.getTenNguoiChoi() + " (Bạn)");
                        txtTenNguoiChoi2.setText(nguoiChoi2.getTenNguoiChoi());

                        arrayListNguoiChoi.set(myid, nguoiChoi1);
                        arrayListNguoiChoi.set(rivalid, nguoiChoi2);
                    }

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                VanChoi vanChoi = dataSnapshot.getValue(VanChoi.class);
                String key = dataSnapshot.getKey();
                if (myGameKey.equals(key)) {
                    LuotDanh = vanChoi.getLuotDanh();
                    int position = vanChoi.getViTriDanh();

                    int nguoithang = vanChoi.getNguoiThang();

                    int i = adapterGridview.getidDong(position);
                    int j = adapterGridview.getidCot(position);
                    int loaico = adapterGridview.getLoaico();


                    if(position == -2)
                    {

                        if (vanChoi.getKey1().equals(myKey)) {
                            adapterGridview.setLoaico(1);
                            LuotDanh = 1;
                            DangChoi = 1;
                            KTVanCo = 1;
                            rivalKey = vanChoi.getKey2();
                            myGameKey = dataSnapshot.getKey();
                            // Cập nhật lại trạng thái của Người chơi
                            databaseNguoiChoi.child(myKey).child("keyVanCo").setValue(myGameKey);
                            databaseNguoiChoi.child(myKey).child("trangThai").setValue(1);
                            databaseNguoiChoi.child(rivalKey).child("keyVanCo").setValue(myGameKey);
                            databaseNguoiChoi.child(rivalKey).child("trangThai").setValue(1);
                            // Cập nhật lại danh sách


                            int myid = KeysNguoiChoi.indexOf(myKey);
                            int rivalid = KeysNguoiChoi.indexOf(rivalKey);
                            NguoiChoi nguoiChoi1 = arrayListNguoiChoi.get(myid);
                            NguoiChoi nguoiChoi2 = arrayListNguoiChoi.get(rivalid);

                            nguoiChoi1.setTrangThai(1);
                            nguoiChoi2.setTrangThai(1);

                            txtTenNguoiChoi2.setText(nguoiChoi1.getTenNguoiChoi() + " (Bạn)");
                            txtTenNguoiChoi1.setText(nguoiChoi2.getTenNguoiChoi());
                            arrayListNguoiChoi.set(myid, nguoiChoi1);
                            arrayListNguoiChoi.set(rivalid, nguoiChoi2);

                        }

                        if (vanChoi.getKey2().equals(myKey)) {
                            resetDataBanCo();
                            adapterGridview.ConverArrayListToArray(arrayList);
                            adapterGridview.notifyDataSetChanged();
                            countDownTimer.cancel();
                            adapterGridview.setLoaico(2);
                            LuotDanh = 2;
                            DangChoi = 1;
                            KTVanCo = 1;

                            rivalKey = vanChoi.getKey1();
                            myGameKey = dataSnapshot.getKey();
                            Toast.makeText(getBaseContext(), "Vui lòng chờ...", Toast.LENGTH_LONG).show();
                            // Cập nhật lại trạng thái của Người chơi
                            databaseNguoiChoi.child(myKey).child("keyVanCo").setValue(myGameKey);
                            databaseNguoiChoi.child(myKey).child("trangThai").setValue(1);

                            databaseNguoiChoi.child(rivalKey).child("keyVanCo").setValue(myGameKey);
                            databaseNguoiChoi.child(rivalKey).child("trangThai").setValue(1);
                            gridView.setEnabled(false);

                            // Cập nhật lại danh sách


                            int myid = KeysNguoiChoi.indexOf(myKey);
                            int rivalid = KeysNguoiChoi.indexOf(rivalKey);
                            if(myid !=-1 && rivalid !=-1) {
                                NguoiChoi nguoiChoi1 = arrayListNguoiChoi.get(myid);
                                NguoiChoi nguoiChoi2 = arrayListNguoiChoi.get(rivalid);

                                nguoiChoi1.setTrangThai(1);
                                nguoiChoi2.setTrangThai(1);

                                txtTenNguoiChoi1.setText(nguoiChoi1.getTenNguoiChoi() + " (Bạn)");
                                txtTenNguoiChoi2.setText(nguoiChoi2.getTenNguoiChoi());

                                arrayListNguoiChoi.set(myid, nguoiChoi1);
                                arrayListNguoiChoi.set(rivalid, nguoiChoi2);
                            }

                        }
                        alertDialog.dismiss();
                        dialog_waiting.dismiss();
                        return;
                    }

                    if (loaico != LuotDanh || position == -1)
                        return;
                    if (LuotDanh == 1) {

                        adapterGridview.SetQuanCo(i, j, 2);
                        ArrayList<Integer> arrayList1 = vanChoi.getArrayList();
                        setDataArrayList(arrayList1);
                        adapterGridview.ConverArrayListToArray(arrayList);
                        int check = adapterGridview.checkWin(i, j);

                        if (check != 0) {
                            if(loaico == 2)
                                showDialogThang();
                            else
                                showDialogThua();
                            //Toast.makeText(MainActivity.this, "Cờ X thắng", Toast.LENGTH_LONG).show();
                            //adapterGridview.khoitaobanco();
                            //setData();
                            //gridView.invalidate();



                        }

                    } else {
                        adapterGridview.SetQuanCo(i, j, 1);
                        ArrayList<Integer> arrayList1 = vanChoi.getArrayList();
                        setDataArrayList(arrayList1);
                        adapterGridview.ConverArrayListToArray(arrayList);

                        Log.d("arraylist", arrayList.toString());

                        //customTextview.setBackgroundResource(R.drawable.o);
                        int check = adapterGridview.checkWin(i, j);
                        if (check != 0) {
                            if(loaico == 1)
                                showDialogThang();
                            else
                                showDialogThua();
                            //Toast.makeText(MainActivity.this, "Cờ O thắng", Toast.LENGTH_LONG).show();
                            //adapterGridview.khoitaobanco();
                            //adapterGridview.notifyDataSetChanged();
                        }


                    }
                    if(loaico==LuotDanh) {
                       countDownTimer.start();
                       countDownTimer_DoiThu.cancel();
                        gridView.setEnabled(true);
                    }



                }

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


        databaseNguoiChoi.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NguoiChoi nguoiChoi = dataSnapshot.getValue(NguoiChoi.class);
                String key = dataSnapshot.getKey();
                KeysNguoiChoi.add(key);
                arrayListNguoiChoi.add(nguoiChoi);
                Log.d("Nguoi Choi: ", nguoiChoi.getTenNguoiChoi());
                if (DangChoi == 0)
                    databaseNguoiChoi.child(myKey).child("trangThai").setValue(0);
                else {
                    databaseNguoiChoi.child(myKey).child("trangThai").setValue(1);
                    return;
                }

                TaoVanCo();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NguoiChoi nguoiChoi = dataSnapshot.getValue(NguoiChoi.class);
                String key = dataSnapshot.getKey();
                // cap nhat listNguoiChoi dang luu
                int t = KeysNguoiChoi.indexOf(key);
                arrayListNguoiChoi.set(t, nguoiChoi);
                if (nguoiChoi.getKeyVanCo().equals("1") == false && key.equals(myKey))
                    dialog_waiting.dismiss();


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


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomTextview customTextview = ((CustomTextview) view.findViewById(R.id.customtextview));
                int i = adapterGridview.getidDong(position);
                int j = adapterGridview.getidCot(position);
                int loaico = adapterGridview.getLoaico();
                Log.d("Long Viet: ", loaico + " Loai Co");
                Log.d("Long Viet: ", LuotDanh + " Luot Danh");


                if (adapterGridview.checkViTri(position) == 0)
                    return;
                if (loaico != LuotDanh)
                    return;
                if(loaico == LuotDanh) {
                    if (loaico == 1) {


                        adapterGridview.SetQuanCo(i, j, 1);
                        arrayList.set(position, 1);
                        adapterGridview.ConverArrayListToArray(arrayList);
                        adapterGridview.notifyDataSetChanged();
                        LuotDanh = 2;

                        int check = adapterGridview.checkWin(i, j);
                        if (check != 0) {
                            if(loaico == 1)
                                showDialogThang();
                            else
                                showDialogThua();
                            //Toast.makeText(MainActivity.this, "Cờ O thắng", Toast.LENGTH_LONG).show();
                            //adapterGridview.khoitaobanco();
                            //setData();
                            //gridView.invalidate();

                        }
                        databaseVanChoi.child(myGameKey).child("luotDanh").setValue(2);
                        databaseVanChoi.child(myGameKey).child("viTriDanh").setValue(position);
                        databaseVanChoi.child(myGameKey).child("arrayList").setValue(arrayList);

                    } else {
                        adapterGridview.SetQuanCo(i, j, 2);
                        arrayList.set(position, 2);
                        adapterGridview.notifyDataSetChanged();
                        adapterGridview.ConverArrayListToArray(arrayList);
                        LuotDanh = 1;

                        int check = adapterGridview.checkWin(i, j);
                        if (check != 0) {

                            if(loaico == 2)
                                showDialogThang();
                            else
                                showDialogThua();
                           // Toast.makeText(MainActivity.this, "Cờ X thắng", Toast.LENGTH_LONG).show();
                            //adapterGridview.khoitaobanco();
                            //adapterGridview.notifyDataSetChanged();

                        }

                        databaseVanChoi.child(myGameKey).child("luotDanh").setValue(1);
                        databaseVanChoi.child(myGameKey).child("viTriDanh").setValue(position);
                        databaseVanChoi.child(myGameKey).child("arrayList").setValue(arrayList);


                    }
                    gridView.setEnabled(false);
                    resetTimer();
                    countDownTimer_DoiThu.start();
                }

                    /*if(LoaiCo == 1)
                    {
                        LuotDanh = 2;
                        databaseVanChoi.child(myGameKey).child("luotDanh").setValue(2);
                        databaseVanChoi.child(myGameKey).child("viTriDanh").setValue(position);
                    }
                    else
                    {
                        LuotDanh =1;
                        databaseVanChoi.child(myGameKey).child("luotDanh").setValue(1);
                        databaseVanChoi.child(myGameKey).child("viTriDanh").setValue(position);
                    }*/


                Log.d("myKey:", myKey);
                Log.d("rivalKey:", rivalKey);
                Log.d("LuotDanh:", LuotDanh + "");
                Log.d("myGameKey:", myGameKey);


            }
        });

        /**/
        // Kiểm tra người số người chơi có trạng thái 0


    }

    private void setData() {
        //arrayList = new ArrayList<Integer>();
        for (int i = 0; i < socot * sodong; i++) {
            arrayList.add(0);
        }
        adapterGridview.notifyDataSetChanged(); // Cập nhật giao diện của gridview
    }

    private void setDataArrayList(ArrayList<Integer> data) {
        for (int i = 0; i < socot * sodong; i++) {
            arrayList.set(i, data.get(i));
        }
        adapterGridview.notifyDataSetChanged();
    }

    private void setTrangThaiAll() {
        Log.d("KeyNguoiChoi: ", "Hello abc");


    }



    public void resetTimer()
    {
        countDownTimer.cancel();
        timeleft = TIME_WAITING;
        updateTimer();

    }
    public  void updateTimer()
    {
        int minute = (int)timeleft / 60000;
        int second = (int) timeleft % 60000/1000;

        String text = "0" + minute + " : ";
        if(second < 10)
            text += "0";

        text += second;
        countdowntext.setText(text);

    }
}
