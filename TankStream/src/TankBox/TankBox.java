package TankBox;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import mysqlapi.MysqlAPI;
import serverproxy.ServerProxy;
/*
 * Created by JFormDesigner on Sat May 02 11:52:44 CST 2020
 */



/**
 * @author unknown
 */
public class TankBox extends JFrame {
    public static void main(String[] args) {
        boolean conresult = MysqlAPI.connectServer();
        System.out.println(conresult);
        Thread socketserver = new serverProxyThread("socket");
        socketserver.start();
        TankBox app = new TankBox();
//        try{
//            boolean conresult = MysqlAPI.connectServer();
//            System.out.println(conresult);
//            ServerProxy myserver = new ServerProxy();
//            myserver.start();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }
    public String username;
    public TankBox() {
        initComponents();
    }

    private void button1ActionPerformed(ActionEvent e) {
        JPanel contentpane = (JPanel) this.getContentPane();
        BorderLayout layout = (BorderLayout) contentpane.getLayout();
        contentpane.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        this.repaint();
        contentpane.add(panel2, BorderLayout.CENTER);
        this.revalidate();

    }

    private void button2ActionPerformed(ActionEvent e) {
        JPanel contentpane = (JPanel) this.getContentPane();
        BorderLayout layout = (BorderLayout) contentpane.getLayout();
        contentpane.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        this.repaint();
        contentpane.add(panel3, BorderLayout.CENTER);
        this.revalidate();
        System.out.println("panel3");
    }

    private void button6ActionPerformed(ActionEvent e) {
        String username = textField2.getText();
        String password = String.valueOf(passwordField2.getPassword());
        String passwordconfirm = String.valueOf(passwordField3.getPassword());
        String playerName = textField3.getText();
        if(username.equals("") || password.equals("") || passwordconfirm.equals("") || playerName.equals("")){
            JOptionPane.showMessageDialog(null, "请填写完整信息", null, JOptionPane.ERROR_MESSAGE);
            return ;
        }
        if(!password.equals(passwordconfirm)){
            JOptionPane.showMessageDialog(null, "两次密码不一致", null, JOptionPane.ERROR_MESSAGE);
            passwordField2.setText("");
            passwordField3.setText("");
            return;
        }
        Connection con = MysqlAPI.getInstance();
        String sql = "select nickname from user where nickname = ?";
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                JOptionPane.showMessageDialog(null, "用户名已存在", null, JOptionPane.ERROR_MESSAGE);
                rs.close();
                return;
            }else{
                String insert = "insert into user(nickname, userPassword, registration_time) values(?,?,?)";
                PreparedStatement insertps = con.prepareStatement(insert);
                insertps.setString(1,username);
                insertps.setString(2, password);
                Date currentTime = new Date();
                insertps.setTimestamp(3, new Timestamp(currentTime.getTime()));
                insertps.execute();
                int userid;
                String selectid = "select user_id from user where nickname = ?";
                PreparedStatement queryid = con.prepareStatement(selectid);
                queryid.setString(1, username);
                ResultSet rsUserid = queryid.executeQuery();
                if(rsUserid.next()){
                    userid = rsUserid.getInt("user_id");
                    String insertbind = "insert into user_bindplayer(user_id, playerName) values(?, ?)";
                    PreparedStatement insertbindps = con.prepareStatement(insertbind);
                    insertbindps.setInt(1, userid);
                    insertbindps.setString(2, playerName);
                    insertbindps.execute();
                }
                JOptionPane.showMessageDialog(null,"注册成功，请登录", null, JOptionPane.PLAIN_MESSAGE);
            }
        }catch(Exception exp){
            exp.printStackTrace();
            return;
        }
    }

    private void button5ActionPerformed(ActionEvent e) {
        String username = textField1.getText();
        String password = String.valueOf(passwordField1.getPassword());
        Connection con = MysqlAPI.getInstance();
        String sql = "select nickname, userPassword from user where nickname = ?";
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                JOptionPane.showMessageDialog(null,"用户不存在，请先注册", null, JOptionPane.ERROR_MESSAGE);
                textField1.setText("");
                passwordField1.setText("");
                return ;
            }else{
                String validate = rs.getString("userPassword");
                if(validate.equals(password)){
                    this.username = username;
                    JOptionPane.showMessageDialog(null, "登录成功", null, JOptionPane.PLAIN_MESSAGE);
                    JPanel contentpane = (JPanel) this.getContentPane();
                    contentpane.removeAll();
                    contentpane.setVisible(false);
                    contentpane.setLayout(new BorderLayout());
                    TankBoxUserPanel userPanel = new TankBoxUserPanel(this.username);
                    contentpane.add(userPanel, BorderLayout.CENTER);
                    contentpane.setVisible(true);
                    return ;
                }
                else{
                    JOptionPane.showMessageDialog(null,"用户名或密码不正确", null, JOptionPane.ERROR_MESSAGE);
                    textField1.setText("");
                    passwordField1.setText("");
                }
            }
        }catch (Exception exp){
            exp.printStackTrace();
            return ;
        }
    }

    private void button3ActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null,"登录后可查看", null, JOptionPane.PLAIN_MESSAGE);
    }

    private void button4ActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null,"登录后可查看", null, JOptionPane.PLAIN_MESSAGE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        button4 = new JButton();
        panel2 = new JPanel();
        textField1 = new JTextField();
        label1 = new JLabel();
        label2 = new JLabel();
        passwordField1 = new JPasswordField();
        button5 = new JButton();
        label3 = new JLabel();
        panel3 = new JPanel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        textField2 = new JTextField();
        passwordField2 = new JPasswordField();
        passwordField3 = new JPasswordField();
        textField3 = new JTextField();
        label8 = new JLabel();
        button6 = new JButton();

        //======== this ========
        setVisible(true);
        setTitle("Tank Battle\u76d2\u5b50");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new GridLayout(4, 1, 0, 20));

            //---- button1 ----
            button1.setText("\u767b\u5f55");
            button1.addActionListener(e -> button1ActionPerformed(e));
            panel1.add(button1);

            //---- button2 ----
            button2.setText("\u6ce8\u518c");
            button2.addActionListener(e -> button2ActionPerformed(e));
            panel1.add(button2);

            //---- button3 ----
            button3.setText("\u6211\u7684\u6218\u7ee9");
            button3.addActionListener(e -> button3ActionPerformed(e));
            panel1.add(button3);

            //---- button4 ----
            button4.setText("\u5766\u514b\u5927\u6570\u636e");
            button4.addActionListener(e -> button4ActionPerformed(e));
            panel1.add(button4);
        }
        contentPane.add(panel1, BorderLayout.WEST);

        //======== panel2 ========
        {

            //---- textField1 ----
            textField1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- label1 ----
            label1.setText("\u7528\u6237\u540d\uff1a");
            label1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- label2 ----
            label2.setText("\u5bc6\u7801\uff1a");
            label2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- passwordField1 ----
            passwordField1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

            //---- button5 ----
            button5.setText("\u767b\u5f55");
            button5.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 28));
            button5.addActionListener(e -> button5ActionPerformed(e));

            //---- label3 ----
            label3.setText("Tank Battle\u7528\u6237\u767b\u5f55");
            label3.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 30));

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                        .addContainerGap(146, Short.MAX_VALUE)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(label2, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(passwordField1, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                            .addComponent(textField1, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
                        .addGap(57, 57, 57))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(159, 159, 159)
                                .addComponent(button5, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(153, 153, 153)
                                .addComponent(label3, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(165, Short.MAX_VALUE))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(passwordField1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addComponent(button5, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(78, Short.MAX_VALUE))
            );
        }
        contentPane.add(panel2, BorderLayout.CENTER);
        setSize(755, 485);
        setLocationRelativeTo(null);

        //======== panel3 ========
        {
            panel3.setPreferredSize(new Dimension(624, 453));

            //---- label4 ----
            label4.setText("\u7528\u6237\u540d\uff1a");
            label4.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- label5 ----
            label5.setText("\u5bc6\u7801\uff1a");
            label5.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- label6 ----
            label6.setText("\u786e\u8ba4\u5bc6\u7801\uff1a");
            label6.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- label7 ----
            label7.setText("\u7ed1\u5b9a\u6e38\u620f\u6635\u79f0\uff1a");
            label7.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- textField2 ----
            textField2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- passwordField2 ----
            passwordField2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

            //---- passwordField3 ----
            passwordField3.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

            //---- textField3 ----
            textField3.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

            //---- label8 ----
            label8.setText("Tank Battle\u7528\u6237\u6ce8\u518c");
            label8.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 28));

            //---- button6 ----
            button6.setText("\u6ce8\u518c");
            button6.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 28));
            button6.addActionListener(e -> button6ActionPerformed(e));

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addGroup(panel3Layout.createParallelGroup()
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGroup(panel3Layout.createParallelGroup()
                                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label5, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label7, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(label6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel3Layout.createParallelGroup()
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addComponent(textField3, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 149, Short.MAX_VALUE))
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGroup(panel3Layout.createParallelGroup()
                                    .addComponent(passwordField3, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(passwordField2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                                        .addComponent(textField2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGroup(panel3Layout.createParallelGroup()
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGap(153, 153, 153)
                                .addComponent(label8, GroupLayout.PREFERRED_SIZE, 297, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGap(152, 152, 152)
                                .addComponent(button6, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(208, Short.MAX_VALUE))
            );
            panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label8, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(textField2, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label4, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(passwordField2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label5, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(passwordField3, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label6, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label7, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addComponent(textField3, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addComponent(button6, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JPanel panel2;
    private JTextField textField1;
    private JLabel label1;
    private JLabel label2;
    private JPasswordField passwordField1;
    private JButton button5;
    private JLabel label3;
    private JPanel panel3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JTextField textField2;
    private JPasswordField passwordField2;
    private JPasswordField passwordField3;
    private JTextField textField3;
    private JLabel label8;
    private JButton button6;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
class serverProxyThread extends Thread{
    public serverProxyThread(String name){
        super(name);
    }
    public void run(){
        try{
            ServerProxy server = new ServerProxy();
            server.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}