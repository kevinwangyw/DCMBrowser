package com.ywwang.dcmbroser;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

public class ShowPicture extends AppCompatActivity implements OnClickListener {
    private Button headerBtn;
    private Button showRemarkBtn;
    private Button doRemarkBtn;
    private Button showImgBtn;

    private static String strresult = "";

    private FragmentManager fm;
    private FragmentTransaction transaction;

    private Socket client = null;

    private PrintWriter out = null;
    private BufferedReader in = null;
    private String message = "";

    private Handler UIhandler;

    public static String getRemark() {
        return strresult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        headerBtn = (Button) findViewById(R.id.headerBtn);
        headerBtn.setOnClickListener(this);
        showRemarkBtn = (Button) findViewById(R.id.showRemarkBtn);
        showRemarkBtn.setOnClickListener(this);
        doRemarkBtn = (Button) findViewById(R.id.doRemarkBtn);
        doRemarkBtn.setOnClickListener(this);
        showImgBtn = (Button) findViewById(R.id.showImgBtn);
        showImgBtn.setOnClickListener(this);

        fm = getFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.contentFrag, new ImgFragment());
        transaction.commit();

        UIhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        transaction.replace(R.id.contentFrag,new ShowHeaderFragment());
                        break;
                    case 2:
                        transaction.replace(R.id.contentFrag,new ShowRemarkFragment());
                        break;
                }
                transaction.commit();
            }
        };
    }

    @Override
    public void onClick(View v) {
        transaction = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.showImgBtn:
                System.out.println("查看图片");
                //transaction.replace(R.id.contentFrag, new ShowHeaderFragment());
                transaction.replace(R.id.contentFrag, new ImgFragment());
                transaction.commit();
                break;
            case R.id.headerBtn:
                System.out.println("查看头文件");
                new Thread(new ShowHeaderThread()).start();
                break;
            case R.id.showRemarkBtn:
                System.out.println("显示备注");
                new Thread(new ShowRemarkThread()).start();
                break;
            case R.id.doRemarkBtn:
                System.out.println("添加备注");
                transaction.replace(R.id.contentFrag, new DoRemarkFragment());
                transaction.commit();
                break;
        }
    }

    class ShowRemarkThread implements Runnable {
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
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String message1 = QueryActivity.getUserId() + " " + QueryActivity.getGgname();
            System.out.println(message1);
            out.println(message1);
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                strresult = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(strresult);
            out.close();
            try {
                in.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message();
            message.what = 2;
            UIhandler.sendMessage(message);
        }
    }
    class ShowHeaderThread implements Runnable {
        @Override
        public void run() {
            try {
                client = new Socket("222.29.188.205", 60009);
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
            String[] ttname = QueryActivity.getGgname().split("j");
            String tttname = ttname[0] + "txt";
            message = QueryActivity.getUserId() + " " + tttname;
            System.out.println(message);
            out.println(message);
            File filehead = new File(Environment.getExternalStorageDirectory() + "/display" + ".txt");
            if (filehead.exists()) {
                filehead.delete();
            }
            try {
                filehead.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(filehead, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStream netIn = null;
            try {
                netIn = client.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            InputStream inf = new DataInputStream(new BufferedInputStream(netIn));
            byte[] buf = new byte[3000000];
            int num = 0;
            try {
                num = inf.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (num != (-1)) {
                try {
                    raf.write(buf, 0, num);
                    raf.skipBytes(num);
                    num = inf.read(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                inf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.close();
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message();
            message.what = 1;
            UIhandler.sendMessage(message);
        }
    }
}
