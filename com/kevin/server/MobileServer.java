package com.kevin.server;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MobileServer implements Runnable {
	Connection con;

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(60002);
			ServerSocket serverSocket1 = new ServerSocket(60003);
			//PrintWriter既可以封装OutputStream类型的字节流，还能够封装Writer类型的字符输出流并增强其功能。
			PrintWriter out = null;
			BufferedReader in = null;
			FileInputStream fis = null;
			OutputStream doc = null;
			OutputStream netOut;
			while (true) {
				Socket client = serverSocket.accept();  //Listens for a connection to be made to this socket and accepts it
				System.out.println("====socket accept====");
				try {
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String str = in.readLine();
					System.out.println("read:" + str);
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
					String[] s = str.split(" ");
					if (s.length == 1) 
					// The information accepted is UsrID,we can find the file
					//当输入用户的ID时，进行精确查询，服务器端返回给手机某病人的所有医学图像缩略图
					{
						String filename = "D:/DCM_image/" + str;
						String tmpname = "D:/DCM_image/tmp.jpg";
						String tt = "";		
						File f = new File(filename);
						String[] filelist = f.list();//列出病人ID文件夹下的所有文件
						int[] mark = new int[filelist.length];
						for (int i = 0; i < filelist.length; i++) {
							if (filelist[i].endsWith(".jpg")) {
								// j++;
								tt = tt + filelist[i] + " "; //将某个病人的图像全部取出
								mark[i] = 1;
							}
						}
						System.out.println(tt);
						out.println(tt);//先将所有的对应文件名字发给对方
						out.close();
						for (int i = 0; i < filelist.length; i++) {
							if (filelist[i].endsWith(".jpg")) {
								System.out.println(filename + "/" + filelist[i]);
								File f1 = new File(tmpname);
								if(f1.exists()){
									f1.delete();
								}
								doResize(filename + "/" + filelist[i], 100, tmpname); 
								System.out.println("do resize : " + filelist[i]);
								fis = new FileInputStream(f1);
								
								Socket client1 = serverSocket1.accept();

								netOut = client1.getOutputStream();
								// 创建网络输出流并提供数据包装器
								doc = new DataOutputStream(new BufferedOutputStream(netOut));
								// 创建文件读取缓冲区
								byte[] buf = new byte[300000];
								int num = fis.read(buf);  
								//Reads up to b.length bytes of data from this input stream into an array of bytes
								//return -1 if there is no more data because the end of the file has been reached
								while (num != (-1)) {
									// 是否读完文件
									doc.write(buf, 0, num);
									// 把文件数据写出网络缓冲区
									doc.flush();
									// 刷新缓冲区把数据写往客户端
									num = fis.read(buf);// 继续从文件中读取数据
									// Thread.sleep(10);
								}
								client1.close();
								doc.close();
								netOut.close();
								fis.close();
							}
						}
						out.close();

					}
					if (s.length == 2)// The information accepted is a ambiguous one
						              //当用户同时输入姓名和生日时，查询得到的为同姓名同生日的所有人的ID，之后再输入ID进行精确查询。
					{
						String name = s[0];
						String birthday = s[1];

						try {
							System.out.println("==== connecting to the database! ====");
							String DB = "jdbc:sqlserver://127.0.0.1:1433;databaseName=DCM_db";
							String User = "sa";
							String Pwd = "popping7410";
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(DB, User, Pwd);  //Attempts to establish a connection to the given database URL
							System.out.println("====数据库连接成功====");
						} catch (Exception e) {
							System.out.println("====数据库连接失败====");
							e.printStackTrace();
						}

						try {
							String fin = "";
							//Statement:The object used for executing a static SQL statement and returning the results it produces
							Statement stm = con.createStatement();
							ResultSet rs = stm.executeQuery("select* from userinfo where Usename = '" + name
									+ "'and birthday = '" + birthday + "'");
							int count = 0;
							while (rs.next()) {
								count++;
								String UserID = rs.getString("UserID").replace(" ", "");
								String UserName = rs.getString("Usename").replace(" ", "");
								String UserSex = rs.getString("Sex").replace(" ", "");
								String time = rs.getString("birthday").replace(" ", "");

								fin = fin + UserID + " " + UserName + " " + UserSex + " " + time + " ";

								System.out.println(UserID + " " + UserName + " " + UserSex + " " + time);
							}
							System.out.println(count);
							out.println(fin);
							out.close();
						} catch (Exception e1) {

							e1.printStackTrace();
						}
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				} finally {
					fis.close();
					doc.close();
					in.close();
					client.close();
					System.out.println("close");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String doResize(String inFile, double aheight, String inDstDir) throws IOException { // 生成缩略图时固定高度
		int height = 0;
		int width = 0;
		Image src = javax.imageio.ImageIO.read(new File(inFile));
		if (src != null) {
			ImageProcessor ip = new ColorProcessor(src);
			height = ip.getHeight();
			width = ip.getWidth();
			double scale = aheight / (double) height;
			// ip = ip.resize((int)(width * inScale),(int)(height * inScale));
			ip = ip.resize((int) (width * scale), (int) (aheight));

			ImagePlus imp = new ImagePlus("", ip);
			FileSaver fs = new FileSaver(imp);  //class for saving image
			String smFileName = inDstDir;
			fs.saveAsJpeg(smFileName);
			ip = null;
			imp = null;
			fs = null;
			return smFileName;
		} else
			return null;
	}
}