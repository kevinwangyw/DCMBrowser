package com.kevin.server;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MobileServer1 implements Runnable // 响应手机客户端的查看图片请求
{
	String filepath = null;
	String transjpg = null;

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(60004);
			while (true) {
				Socket client = serverSocket.accept();
				System.out.println("accept");
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String str = in.readLine();
					System.out.println("read:" + str); // str is should contains
														// UserID GraphID and
														// width and height
					String[] info = str.split(" ");// info 0 uid 1 graph 3 width
													// 4 height

					if (info.length != 4) {
						System.err.println("Information transmit error!");
					}
					String userId = info[0];
					String gname = info[1];
					int width = Integer.valueOf(info[2]);
					int height = Integer.valueOf(info[3]);
					if (gname.endsWith(".jpg")) {
						filepath = "D:/DCM_image/" + userId + "/" + gname;
						transjpg = "D:/DCM_image/" + userId + "/" + "transtmp.jpg";
					}

					Image src = javax.imageio.ImageIO.read(new File(filepath));
					ImageProcessor ip = new ColorProcessor(src);
					int pheight = ip.getHeight();
					int pwidth = ip.getWidth();
					int lastheight = height;
					if ((double) height / (double) width > (double) pheight / (double) pwidth) //如果手机屏幕的高度高于图片高度，重置高度
					{
						lastheight = (int) ((double) height * (double) width / (double) pwidth);
					}
					MobileServer.doResize(filepath, lastheight, transjpg);
					// File file = new File(transjpg);
					FileInputStream fos = new FileInputStream(transjpg);
					OutputStream netOut = client.getOutputStream();
					OutputStream doc = new DataOutputStream(new BufferedOutputStream(netOut));
					byte[] buf = new byte[3000000];
					int num = fos.read(buf);
					while (num != (-1)) {
						doc.write(buf, 0, num);
						doc.flush();
						num = fos.read(buf);
					}
					in.close();
					fos.close();
					doc.close();
					netOut.close();
					Server.delFile(transjpg);
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
}