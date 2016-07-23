package com.kevin.server;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.DICOM;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class Server implements Runnable  //接收上传的图片文件
{
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(60000); //创建绑定到特定端口的服务器套接字
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("accept");
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String str = in.readLine();
                    System.out.println("read:" + str);

                    File file = new File("D:/DCM_image/tmp.dcm");  //先利用一个临时文件存储接收到的图像文件
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();//地创建一个新的空文件
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    InputStream netIn = client.getInputStream();
                    InputStream inf = new DataInputStream(new BufferedInputStream(netIn));
                    byte[] buf = new byte[10000000];
                    int num = inf.read(buf);
                    while (num != (-1)) {
                        raf.write(buf, 0, num);
                        raf.skipBytes(num);
                        num = inf.read(buf);
                    }
                    in.close();
                    inf.close();
                    raf.close();

                    String inFile = "D:/DCM_image/tmp.dcm";
                    ImagePlus img = new ImagePlus(inFile);
                    FileSaver fs = new FileSaver(img);

                    Calendar c = Calendar.getInstance();

                    String year = String.valueOf(c.get(Calendar.YEAR));
                    String month = String.valueOf(c.get(Calendar.MONTH) + 1);//系统日期从0开始算起
                    String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                    String hour = String.valueOf(c.get(Calendar.HOUR));
                    String miniute = String.valueOf(c.get(Calendar.MINUTE));

                    month = format(month);
                    day = format(day);
                    hour = format(hour);
                    miniute = format(miniute);

                    String smFileName = "D:/DCM_image/" + str + "/" + year + month + day + hour + miniute + ".jpg";
                    String smFileName1 = "D:/DCM_image/" + str + "/" + year + month + day + hour + miniute + ".txt";
                    fs.saveAsJpeg(smFileName);
                    DICOM dd = new DICOM();
                    String info = dd.getInfo("D:/DCM_image/tmp.dcm");
                    PrintStream out = System.out;
                    PrintStream ps = new PrintStream(smFileName1);  //将dicom图片的信息保存到txt
                    System.setOut(ps);
                    System.out.println(info);
                    System.setOut(out);
                    delFile("D:/DCM_image/tmp.dcm");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } finally {
                    client.close();
                    System.out.println("close");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String a[]) {
        Thread desktopServerThread = new Thread(new Server());
        desktopServerThread.start();
        Thread desktopServerThread1 = new Thread(new Serversql());
        desktopServerThread1.start();
        Thread desktopServerThread2 = new Thread(new MobileServer());
        desktopServerThread2.start();
        Thread desktopServerThread3 = new Thread(new MobileServer1());
        desktopServerThread3.start();
        Thread desktopServerThread4 = new Thread(new MobileServer2());
        desktopServerThread4.start();
        Thread desktopServerThread5 = new Thread(new MobileServer3());
        desktopServerThread5.start();
    }

    public static String format(String s) {
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    public static void delFile(String fileName) {
        try {
            File file = new File(fileName);
            file.delete();
        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();

        }
    }
}