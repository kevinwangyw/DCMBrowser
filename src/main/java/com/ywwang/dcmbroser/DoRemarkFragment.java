package com.ywwang.dcmbroser;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoRemarkFragment extends Fragment {
    private Button postbtn;
    private Button cancelbtn;
    private EditText editText;

    private Socket client;
    private PrintWriter out;
    private String message;
    private BufferedReader in;

    private FragmentManager fm;
    private FragmentTransaction transaction;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fm = getFragmentManager();
        transaction = fm.beginTransaction();

        View v = inflater.inflate(R.layout.fragment_do_remark, container, false);
        editText = (EditText)v.findViewById(R.id.addRemarkEditText);
        postbtn = (Button)v.findViewById(R.id.postBtn);
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new Thread(new SendRemarkThread()).start();
            }
        });
        cancelbtn = (Button)v.findViewById(R.id.cancelBtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.replace(R.id.contentFrag,new ImgFragment());
                transaction.commit();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Toast.makeText(getActivity(), "插入成功", Toast.LENGTH_SHORT).show();
                    transaction.replace(R.id.contentFrag,new ImgFragment());
                    transaction.commit();
                }
            }
        };
        return v;
    }

    class SendRemarkThread implements Runnable{
        @Override
        public void run() {
            try {
                client = new Socket("222.29.188.205", 60005);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message = QueryActivity.getUserId() + " " + QueryActivity.getGgname() + " " + editText.getText().toString();
            out.println(message);
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String str = null;
            try {
                str = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(str);

            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                client.close();
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
