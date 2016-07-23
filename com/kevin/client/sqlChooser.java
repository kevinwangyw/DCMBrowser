package com.kevin.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class sqlChooser extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = -3463870253313037557L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    JLabel lblUsename, lblUsename_1, lblSex, lblBirthday;
    Socket client;
    JComboBox comboBox, comboBox_1;
    private JComboBox comboBox_2;
    private JLabel label;
    private JComboBox comboBox_3;
    private JLabel label_1;
    private JComboBox comboBox_4;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        // awt是单线程模式的，所有awt的组件只能在(推荐方式)事件处理线程中访问，从而保证组件状态的可确定性。
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    sqlChooser frame = new sqlChooser();
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
    public sqlChooser() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        comboBox = new JComboBox();
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String ttt = comboBox.getSelectedItem().toString();
                if (ttt.equals("添加")) {
                    lblUsename_1.setVisible(true);
                    lblSex.setVisible(true);
                    lblBirthday.setVisible(true);
                    textField_1.setVisible(true);
                    comboBox_1.setVisible(true);
                    comboBox_2.setVisible(true);
                    comboBox_3.setVisible(true);
                    comboBox_4.setVisible(true);
                    label.setVisible(true);
                    label_1.setVisible(true);
                }
                if (ttt.equals("删除")) {
                    lblUsename_1.setVisible(false);
                    lblSex.setVisible(false);
                    lblBirthday.setVisible(false);
                    textField_1.setVisible(false);
                    comboBox_1.setVisible(false);
                    comboBox_2.setVisible(false);
                    comboBox_3.setVisible(false);
                    comboBox_4.setVisible(false);
                    label.setVisible(false);
                    label_1.setVisible(false);

                }
            }
        });
        comboBox.setModel(new DefaultComboBoxModel(new String[]{"添加", "删除"}));
        comboBox.setBounds(384, 12, 54, 33);
        contentPane.add(comboBox);

        lblUsename = new JLabel("UserID");
        lblUsename.setBounds(31, 46, 110, 33);
        contentPane.add(lblUsename);

        textField = new JTextField();
        textField.setBounds(100, 53, 114, 19);
        contentPane.add(textField);
        textField.setColumns(10);

        lblUsename_1 = new JLabel("UseName");
        lblUsename_1.setBounds(31, 94, 110, 33);
        contentPane.add(lblUsename_1);

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(100, 101, 114, 19);
        contentPane.add(textField_1);

        lblSex = new JLabel("Sex");
        lblSex.setBounds(31, 139, 110, 33);
        contentPane.add(lblSex);

        comboBox_1 = new JComboBox();
        comboBox_1.setModel(new DefaultComboBoxModel(new String[]{"male", "femal"}));
        comboBox_1.setBounds(100, 139, 77, 24);
        contentPane.add(comboBox_1);

        lblBirthday = new JLabel("Birthday");
        lblBirthday.setBounds(31, 184, 110, 33);
        contentPane.add(lblBirthday);

        JButton button = new JButton("确定");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String choice = comboBox.getSelectedItem().toString();
                String message;
                // System.out.println(message);
                if (choice.equals("添加")) {
                    //出生年月日
                    String time = comboBox_2.getSelectedItem().toString() + "-"
                            + comboBox_3.getSelectedItem().toString() + "-" + comboBox_4.getSelectedItem().toString();
                    //利用空格将姓名、性别、出生年月、病人ID隔开
                    message = textField.getText().toString() + " " + textField_1.getText().toString() + " "
                            + comboBox_1.getSelectedItem().toString() + " " + time;
                } else {
                    message = textField.getText().toString();
                }

                try {
                    client = new Socket("222.29.189.229", 60001);
                    PrintWriter out = new PrintWriter(
                            new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    out.println(message);

                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String str = in.readLine();
                    System.out.println("readsql:" + str);
                    JOptionPane.showMessageDialog(null, str, "来自服务器消息", JOptionPane.INFORMATION_MESSAGE);
                    out.close();
                    in.close();

                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        button.setBounds(346, 192, 77, 25);
        contentPane.add(button);

        comboBox_2 = new JComboBox(); // year
        comboBox_2.setEditable(true);
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        String[] yy = new String[y - 1899];
        for (int i = 0; y - i >= 1900; i++) {
            yy[i] = String.valueOf(y - i);
        }
        comboBox_2.setModel(new DefaultComboBoxModel(yy));
        comboBox_2.setBounds(100, 193, 60, 24);
        contentPane.add(comboBox_2);

        label = new JLabel("-");
        label.setBounds(167, 198, 70, 15);
        contentPane.add(label);

        comboBox_3 = new JComboBox(); // month
        String[] mm = new String[12];
        for (int i = 0; i < 12; i++) {
            int j = i + 1;
            mm[i] = String.valueOf(j);
            mm[i] = format(mm[i]);
        }
        comboBox_3.setModel(new DefaultComboBoxModel(mm));
        comboBox_3.setBounds(187, 193, 54, 24);
        contentPane.add(comboBox_3);

        label_1 = new JLabel("-");
        label_1.setBounds(258, 198, 70, 15);
        contentPane.add(label_1);

        comboBox_4 = new JComboBox(); // day
        final String[] dd1 = new String[31];
        final String[] dd2 = new String[30];
        final String[] dd3 = new String[29];
        final String[] dd4 = new String[28];
        List<String[]> list = new ArrayList<String[]>();
        list.add(dd1);
        list.add(dd2);
        list.add(dd3);
        list.add(dd4);
        for (String[] dd : list) {
            for (int i = 0; i < dd.length; i++) {
                int j = i + 1;
                dd[i] = String.valueOf(j);
                dd[i] = format(dd[i]);
            }
        }

        comboBox_4.setModel(new DefaultComboBoxModel(dd1));

        comboBox_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String month = comboBox_3.getSelectedItem().toString();
                String yeart = comboBox_2.getSelectedItem().toString();
                int year = Integer.valueOf(yeart);
                if (month.equals("04") || month.equals("06") || month.equals("09") || month.equals("11")) {
                    comboBox_4.setModel(new DefaultComboBoxModel(dd2));
                } else if (month.equals("02")) {
                    if (yeardecide(year) == 1) {
                        comboBox_4.setModel(new DefaultComboBoxModel(dd3)); // 润年2月份29天
                    } else {
                        comboBox_4.setModel(new DefaultComboBoxModel(dd4));
                    }
                } else {
                    comboBox_4.setModel(new DefaultComboBoxModel(dd1));
                }
            }
        });
        comboBox_4.setBounds(274, 193, 54, 24);
        contentPane.add(comboBox_4);
    }

    public static String format(String s) {
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    public static int yeardecide(int year) // 判断是否是润年
    {
        if (year % 4 != 0) {
            return 0;
        } else if (year % 100 == 0 && year % 400 != 0) {
            return 0;
        } else {
            return 1;
        }
    }
}