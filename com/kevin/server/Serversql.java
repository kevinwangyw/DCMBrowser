package com.kevin.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Serversql implements Runnable {
	Connection con;

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(60001);
			while (true) {
				Socket client = serverSocket.accept();
				System.out.println("accept");
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String str = in.readLine();
					System.out.println("message from client:" + str);
					// in.close();
					String[] s = str.split(" ");
					String UseID = null;
					String Usename = null;
					String Sex = null;
					String birthday = null;
					UseID = s[0];
					if (s.length == 4) {  //添加病人，并为病人新建一个文件夹用于存放图片
						System.out.println("length of meassge array : " + s.length);
						Usename = s[1];
						Sex = s[2];
						birthday = s[3];

						try {
							System.out.println("==== connecting to the database! ====");
							String DB = "jdbc:sqlserver://127.0.0.1:1433;databaseName=DCM_db";
							String User = "sa";
							String Pwd = "popping7410";
							// 之所以要使用下面这条语句，是因为要使用SQLServer的驱动，所以我们要把它驱动起来，
							// 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来
							//Class.forName:Returns the Class object associated with the class 
							//or interface with the given string name
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(DB, User, Pwd);
							System.out.println("==== 数据库连接成功 ====");
						} catch (Exception e) {
							System.out.println("==== 数据库连接失败 ====");
							e.printStackTrace();
						}
						try {
							Statement stm = con.createStatement();
							stm.executeUpdate("insert into userinfo values('" + UseID + "', '" + Usename + "' , '" + Sex
									+ "' , '" + birthday + "')");
							String newF = "D:/DCM_image/" + UseID;
							newFolder(newF);
							PrintWriter out = new PrintWriter(
									new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
							out.println("插入成功!");
							out.close();
							in.close();
						} catch (Exception e1) {
							PrintWriter out = new PrintWriter(
									new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
							out.println("插入失败!");
							System.out.println("====插入失败====");
							out.close();
							in.close();
							e1.printStackTrace();
						}
					}
					if (s.length == 1) {  //输入病人的ID删除病人的所有图片
						try {
							String DB = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName = DCM_db";
							String User = "kevin";
							String Pwd = "jsj483483";
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
							con = DriverManager.getConnection(DB, User, Pwd);
						} catch (Exception e) {
							System.out.println("====数据库连接失败====");
							e.printStackTrace();
						}
						try {
							Statement stm = con.createStatement();
							stm.executeUpdate("delete from info where(UserID=" + UseID + ")");
							String delF = "D:/DCM_image/" + UseID;
							delFolder(delF);
							PrintWriter out = new PrintWriter(
									new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
							out.println("删除成功!");
							out.close();
							in.close();
						} catch (Exception e1) {
							System.out.println("====删除失败!====");
							e1.printStackTrace();
						}
					}
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

	// 新建一个文件夹

	public void newFolder(String folderPath) {

		try {

			String filePath = folderPath;

			File myFilePath = new File(filePath);

			if (!myFilePath.exists()) {

				myFilePath.mkdir();

			}

		} catch (Exception e) {

			System.out.println("新建文件夹操作出错");

			e.printStackTrace();

		}

	}

	// 删除文件夹
	public void delFolder(String folderPath) {

		try {

			String filePath = folderPath;

			File delPath = new File(filePath);

			delPath.delete();

		} catch (Exception e) {

			System.out.println("删除文件夹操作出错");

			e.printStackTrace();

		}
	}
}