package com.ywwang.dcmbroser;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by ywwang on 2016/7/13.
 */
public class ImgFragment extends Fragment {
    private ImageView img;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.frag_imag, container, false);
        img = (ImageView)v.findViewById(R.id.patientImg);
        System.out.println("the click image path : " + Environment.getExternalStorageDirectory());

        img.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/display.jpg"));
        return v;
    }
}
