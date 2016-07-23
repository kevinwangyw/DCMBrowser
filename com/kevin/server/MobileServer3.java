package com.kevin.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MobileServer3 implements Runnable {
	String filepath = null;

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(60009);  //处理查看dicom图像请求
			while (true) {
				Socket client = serverSocket.accept();
				System.out.println("accept");
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String str = in.readLine();
					System.out.println("read:" + str); // str is should contains UserID GraphID.txt
					String[] info = str.split(" ");

					String userId = info[0];
					String txtname = info[1];
					String txt = "D:/DCM_image/" + userId + "/" + txtname;
					// File file = new File(transjpg);
					FileInputStream fos = new FileInputStream(txt);
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
					// Server.delFile(txt);

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