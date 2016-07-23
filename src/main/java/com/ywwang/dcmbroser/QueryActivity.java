package com.ywwang.dcmbroser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextId;
    private EditText editTextName;
    private EditText editTextBirth;
    private Button clearQueryBtn;
    private Button vagueQueryBtn;
    private ListView resultList = null;

    private Boolean queryFinish;

    private String message;
    private static String UserId;

    private String[] tt;
    private String[] YY;
    private static String ggname;
    private int screenHeight;

    public static String getGgname() {
        return ggname;
    }

    public static String getUserId() {
        return UserId;
    }

    private int screenWidth;

    private Socket client = null;

    private BufferedReader in = null;

    private Handler queryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        editTextBirth = (EditText) findViewById(R.id.editTextBirth);
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextName = (EditText) findViewById(R.id.editTextName);

        clearQueryBtn = (Button) findViewById(R.id.ClearQueryBtn);
        clearQueryBtn.setOnClickListener(this);
        vagueQueryBtn = (Button) findViewById(R.id.VagueQueryBtn);
        vagueQueryBtn.setOnClickListener(this);

        resultList = (ListView) findViewById(android.R.id.list);

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        queryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        setViews();
                        break;
                    case 2:
                        Intent intent = null;
                        intent = new Intent(QueryActivity.this, ShowPicture.class);
                        startActivity(intent);
                        break;
                    case 3:
                        setViews1();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ClearQueryBtn:
                message = editTextId.getText().toString();
                System.out.println("patient id to be sent : " + message);
                UserId = message;

                System.out.println("patient id : " + message);

                Thread clearQueryThread = new Thread(new ClearQueryThread());
                clearQueryThread.start();

                System.out.println("clear query started!");
                //list_files(listView);
                //setViews();
                break;
            case R.id.VagueQueryBtn:
                message = editTextName.getText().toString().trim() + " " + editTextBirth.getText().toString().trim();
                new Thread(new VagueQueryThread()).start();
                break;
        }
    }

    class ClearQueryThread implements Runnable {
        private Socket client, client1;
        private BufferedReader in;

        @Override
        public void run() {
            queryFinish = false;
            try {
                client = new Socket("222.29.188.205", 60002);
                System.out.println("====connection success=====");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            PrintWriter out = null;

            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println(message);
            System.out.println("query message " + message);

            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            String str = null;

            try {
                str = in.readLine();
                System.out.println(str);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("image names received from server : " + str);
            tt = str.split(" ");

            RandomAccessFile raf = null;
            InputStream inf = null;
            InputStream netIn = null;
            File dfile = Environment.getExternalStorageDirectory();
            File[] ttfile = dfile.listFiles();
            if (ttfile != null) {
                for (int i1 = 0; i1 < ttfile.length; i1++) {
                    if(ttfile[i1].isFile()){
                        ttfile[i1].delete();  //删除上次查询保存在本地的图片
                    }
                }
            }

            for (int i = 0; i < tt.length; i++) {
                try {
                    client1 = new Socket("222.29.188.205", 60003);
                    System.out.println("60003");
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                File file = new File(Environment.getExternalStorageDirectory() + "/" + tt[i]);
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    raf = new RandomAccessFile(file, "rw");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    netIn = client1.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inf = new DataInputStream(new BufferedInputStream(netIn));

                byte[] buf = new byte[300000];
                int num = 0;
                try {
                    num = inf.read(buf);
                    System.out.println(num);
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
                    client1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                queryFinish = true;
                System.out.println("get image from server success!");
            }
            Message msg = new Message();
            msg.what = 1;
            queryHandler.sendMessage(msg);
        }
    }

    class GetImagThread implements Runnable {
        private List<Map<String, Object>> imgList;
        private int position;

        public GetImagThread(List<Map<String, Object>> imgList, int position) {
            this.imgList = imgList;
            this.position = position;
        }

        @Override
        public void run() {

            Socket client = null;
            try {
                client = new Socket("222.29.188.205", 60004);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ggname = (String) imgList.get(position).get("title");

            String message1 = QueryActivity.getUserId() + " " + ggname + " " + screenWidth + " " + screenHeight;

            out.println(message1);

            File filedisplay = new File(Environment.getExternalStorageDirectory() + "/display" + ".jpg");
            if (filedisplay.exists()) {
                filedisplay.delete();
            }
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(filedisplay, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStream netIn = null;
            try {
                netIn = client.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
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

            Message msg = new Message();
            msg.what = 2;
            queryHandler.sendMessage(msg);
        }
    }

    private void setViews() {
        ListView lv = (ListView) findViewById(android.R.id.list);
        final List<Map<String, Object>> imgList = getDatas();
        SimpleAdapter adapter = new SimpleAdapter(this, imgList, R.layout.clearlist
                , new String[]{"image", "title"}, new int[]{R.id.smllImg, R.id.title});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        System.out.println("SimpleAdapter has been set!");
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                System.out.println("click position is : " + position);

                new Thread(new GetImagThread(imgList, position)).start();
            }
        });
    }

    private List<Map<String, Object>> getDatas() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        File path = Environment.getExternalStorageDirectory();
        File[] files = path.listFiles();

        for (int i1 = 0; i1 < tt.length; i1++) {
            Map<String, Object> map = new HashMap<String, Object>();
            System.out.println("image file path : " + files[i1].toString());
            //map.put("image", BitmapFactory.decodeFile(files[i1].toString()));
            Bitmap bitmap = BitmapFactory.decodeFile(files[i1].toString());

            map.put("image",bitmap );

            String[] ff = files[i1].toString().split("/");
            map.put("title", ff[ff.length - 1]);  //从路径中取图片名
            System.out.println("image name : " + map.get("title"));

            list.add(map);
        }
        return list;
    }

    class VagueQueryThread implements Runnable {
        private Socket client;
        private PrintWriter out;
        private BufferedReader in;

        @Override
        public void run() {

            try {
                client = new Socket("222.29.188.205", 60002);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            String str1 = null;

            try {
                str1 = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            YY = str1.trim().split(" ");

            Message msg = new Message();
            msg.what = 3;
            queryHandler.sendMessage(msg);
        }
    }

    private void setViews1() {
        ListView lv = (ListView) findViewById(android.R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(this,
                getDatas1(), R.layout.vaguelist, new String[]{"UserId", "UserName", "Sex", "Birthday"},
                new int[]{R.id.UserId, R.id.UserName, R.id.Sex, R.id.Birthday});
        lv.setAdapter(adapter);
    }

    private List<Map<String, Object>> getDatas1() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i1 = 0; i1 < YY.length; i1++) {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("UserId", YY[i1]);
            i1++;
            map.put("UserName", YY[i1]);
            i1++;
            map.put("Sex", YY[i1]);
            i1++;
            map.put("Birthday", YY[i1]);

            System.out.println(i1);

            list.add(map);
        }
        return list;
    }

}
