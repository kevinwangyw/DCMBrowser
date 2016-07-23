package com.ywwang.dcmbroser;


import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShowHeaderFragment extends android.app.Fragment {
    private TextView textView;

    public ShowHeaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_header, container, false);
        textView = (TextView) v.findViewById(R.id.headerText);
        String path = Environment.getExternalStorageDirectory() + "/" + "display.txt";
        File file = new File(path);
        try {
            textView.setText(getTxt(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.setMovementMethod(new ScrollingMovementMethod());
        return v;
    }

    public String getTxt(File url) throws IOException{
        StringBuilder txt = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(url);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = br.readLine()) != null) {
                txt.append(line);
                txt.append("\n\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return txt.toString();
    }
}
