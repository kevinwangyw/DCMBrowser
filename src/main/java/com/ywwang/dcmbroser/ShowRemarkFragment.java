package com.ywwang.dcmbroser;


import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowRemarkFragment extends Fragment {
    private TextView textView;

    public ShowRemarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_remark, container, false);
        textView = (TextView) v.findViewById(R.id.remarText);
        textView.setMovementMethod(new ScrollingMovementMethod());
        String result = ShowPicture.getRemark();
        String[] spli = result.split(" ");
        for (int i = 0; i < spli.length; i++) {                    //服务器构造的信息格式：UserID  ImgName1 time1 content
            if ((i + 2) % 4 == 0) {
                textView.append(spli[i] + ": ");
            }
            if ((i + 1) % 4 == 0) {
                textView.append(spli[i] + "\n");
            } else {
                continue;
            }
        }
        return v;
    }

}
