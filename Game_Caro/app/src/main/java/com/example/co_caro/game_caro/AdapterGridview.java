package com.example.co_caro.game_caro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class AdapterGridview extends BaseAdapter {
    Context myContext;
    int myLayout;
    ArrayList<Integer> arr;
    int loaico;
    int sodong;
    int socot;
    int [][]banco;

    void khoitaobanco()
    {
        banco = new int[sodong][socot];
        for(int i = 0; i < sodong; i++)
            for(int j = 0; j < socot; j++)
                banco[i][j]=0;

    }

    public AdapterGridview(Context myContext, int myLayout, ArrayList<Integer> arr, int loaico, int sodong, int socot) {
        this.myContext = myContext;
        this.myLayout = myLayout;
        this.arr = arr;
        this.loaico = loaico;
        this.sodong = sodong;
        this.socot = socot;
        khoitaobanco();
    }

    public int getLoaico() {
        return loaico;
    }

    public void setLoaico(int loaico) {
        this.loaico = loaico;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(myLayout,null);
        final CustomTextview customTextview = convertView.findViewById(R.id.customtextview);
        if(arr.get(position)==0)
            customTextview.setBackgroundResource(R.drawable.button);
        else if(arr.get(position)==1)
            customTextview.setBackgroundResource(R.drawable.o);
        else
            customTextview.setBackgroundResource(R.drawable.x);
        return convertView;
    }

    public int getidDong(int pos)
    {
        return pos/socot;
    }
    public int getidCot(int pos)
    {
        return pos%socot;
    }
    public int checkViTri(int pos)
    {
        int i = getidDong(pos);
        int j = getidCot(pos);
        if(banco[i][j] == 0)
            return 1;
        return 0;
    }

    public void SetQuanCo(int i, int j, int type)
    {
        banco[i][j] = type;
    }
    public int checkWin(int row, int col) {
        int[][] rc = { { 0, -1, 0, 1 }, { -1, 0, 1, 0 }, { 1, -1, -1, 1 },
                { -1, -1, 1, 1 } };
        int i = row, j = col;
        for (int direction = 0; direction < 4; direction++) {
            int count = 0;
            i = row;
            j = col;
            while (i > 0 && i < sodong && j > 0 && j < socot

                    && banco[i][j] == banco[row][col]) {
                count++;
                if (count == 5) {
                    return banco[row][col];
                }
                i += rc[direction][0];
                j += rc[direction][1];
            }

            count--;
            i = row;
            j = col;
            while (i > 0 && i < sodong && j > 0 && j < socot
                    && banco[i][j] == banco[row][col]) {
                count++;
                if (count == 5) {
                    return banco[row][col];
                }
                i += rc[direction][2];
                j += rc[direction][3];
            }
        }
        return 0;
    }
    public void ConverArrayListToArray(ArrayList<Integer> arrayList)
    {
        for(int i = 0; i < arrayList.size(); i++)
            banco[i/socot][i%socot] = arrayList.get(i);

    }
}
