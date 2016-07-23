package com.kevin.client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
public class FileChooser extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField2;
	File file;
	String filename;
	Socket client;
//	private BackgroundPanel backgroundPanel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");  //UIManager manages the current look and feel
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileChooser frame = new FileChooser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FileChooser() {
		setTitle(" 客户端");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel label = new JLabel("请选择文件");
		label.setBounds(55, 42, 70, 15);
		contentPane.add(label);

		textField = new JTextField();
		textField.setBounds(134, 40, 163, 25);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton button = new JButton("打开");
		button.setBounds(309, 37, 70, 25);
		contentPane.add(button);

		JLabel label2 = new JLabel("请输入病人病人ID");
		label2.setBounds(55, 87, 131, 15);
		contentPane.add(label2);

		textField2 = new JTextField();
		textField2.setBounds(183, 77, 114, 25);
		contentPane.add(textField2);
		textField2.setColumns(10);

		JButton button2 = new JButton("发送\n");
		//发送按钮响应事件
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					do_button_actionPerformed1(e);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		button2.setBounds(309, 74, 70, 25);
		contentPane.add(button2);
		//添加文件按钮事件响应
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_button_actionPerformed(e);
			}
		});

	}
	protected void do_button_actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("医学图像文件", "dcm","dicom");  // filters using a specified set of extensions
		chooser.setFileFilter(filter);
		int option = chooser.showOpenDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();  //return the selected file
			textField.setText(file.toString());
		}
	}
	protected void do_button_actionPerformed1(ActionEvent e) throws UnknownHostException, IOException, InterruptedException
	{
		String message =textField2.getText().toString();


		FileInputStream fos = new FileInputStream(file);
		client = new Socket("222.29.189.229", 60000);
		PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(client.getOutputStream())),true);
		out.println(message);
		OutputStream netOut = client.getOutputStream();
		// 创建网络输出流并提供数据包装器
		OutputStream doc = new DataOutputStream(new BufferedOutputStream(netOut));
		// 创建文件读取缓冲区
		byte[] buf = new byte[10000000];
		int num = fos.read(buf);
		while (num != (-1)) {
			// 是否读完文件
			doc.write(buf, 0, num);
			// 把文件数据写出网络缓冲区
			doc.flush();
			// 刷新缓冲区把数据写往客户端
			num = fos.read(buf);// 继续从文件中读取数据
			//Thread.sleep(10);
		}
		out.close();
		fos.close();
		doc.close();
		//System.out.println("Server OK!");
	}
}
