package com.kevin.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class MobileServer2 implements Runnable {
	Connection con;

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(60005);
			while (true) {
				Socket client = serverSocket.accept();
				System.out.println("accept");
				try {
					PrintWriter out = new PrintWriter(
							new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String str = in.readLine();
					System.out.println("read5555:" + str);// UserID ,grpahname, time, content
					
					String[] s = str.split(" ");
					String UserID = s[0];
					String ImgName = s[1];

					if (s.length == 3)// For inserting
					{
						String time = null;
						Calendar c = Calendar.getInstance();

						String year = String.valueOf(c.get(Calendar.YEAR));
						String month = String.valueOf(c.get(Calendar.MONTH) + 1);
						String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
						// format(year);
						month = format(month);
						day = format(day);
						time = year + "-" + month + "-" + day;
						String content = s[2];
						try {
							System.out.println("==== connecting to the database! ====");
							String DB = "jdbc:sqlserver://127.0.0.1:1433;databaseName=DCM_db";
							String User = "sa";
							String Pwd = "popping7410";
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(DB, User, Pwd);
						} catch (Exception e) {
							System.out.println("====数据库连接失败====");
							e.printStackTrace();
						}
						try {
							Statement stm = con.createStatement();
							stm.executeUpdate("insert into remark values('" + UserID + "', '" + ImgName + "' ,'" + time
									+ "' ,'" + content + "')");
							out.println("插入成功!");
							out.close();
							in.close();
						} catch (Exception e1) {
							out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),
									true);
							out.println("����ʧ�ܣ�");
							System.out.println("====����ʧ��====");
							out.close();
							in.close();
							e1.printStackTrace();
						}
					} else if (s.length == 2)// For selecting UserID ImgName
					{
						try {
							System.out.println("==== connecting to the database! ====");
							String DB = "jdbc:sqlserver://127.0.0.1:1433;databaseName=DCM_db";
							String User = "sa";
							String Pwd = "popping7410";
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(DB, User, Pwd);
						} catch (Exception e) {
							System.out.println("====数据库连接失败====");
							e.printStackTrace();
						}

						try {
							String fin = "";
							Statement stm = con.createStatement();
							ResultSet rs = stm.executeQuery(
									"select* from remark where UserID = '" + UserID + "'and ImgName = '" + ImgName + "'");

							while (rs.next()) {
								String UseID = rs.getString("UserID");
								String ImgName1 = rs.getString("ImgName");
								String time1 = rs.getString("time");
								String ccc = rs.getString("content");

								fin = fin + UserID + " " + ImgName1 + " " + time1 + " " + ccc + " ";
							}
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
					client.close();
					System.out.println("close");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String format(String s) {
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}
}