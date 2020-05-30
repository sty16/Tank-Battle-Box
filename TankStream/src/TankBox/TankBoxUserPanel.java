/*
 * Created by JFormDesigner on Sat May 02 17:57:54 CST 2020
 */

package TankBox;

import mysqlapi.MysqlAPI;
import serverproxy.ServerProxy;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Brainrain
 */
public class TankBoxUserPanel extends JPanel {
    public static  DrawDamage drawDamage = new DrawDamage();
    public TankBoxUserPanel() {
        initComponents();
    }
    public String username;
    public String Playername;
    public final String[] battleMode = {"PVP模式", "天梯模式", "复活模式"};
    public final String[] resultMode = {"暴击率","命中率","击穿率", "单发平均伤害", "单发最高伤害", "坦克胜率", "存活率", "单场伤害","场均伤害","单场承受伤害","场均承受伤害","场均被击中次数","场均发现敌人数"};
    public TankBoxUserPanel(String username){
        this.username = username;
        initComponents();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(ServerProxy.status){
                    textField2.setText("连接成功");
                }else{
                    textField2.setText("未连接");
                }
                if(MysqlAPI.status){
                    textField1.setText("连接成功");
                }else{
                    textField1.setText("未连接");
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void button3ActionPerformed(ActionEvent e) {
        BorderLayout layout = (BorderLayout) this.getLayout();
        this.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        this.setVisible(false);
        this.add(panel3, BorderLayout.CENTER);
        panel4.removeAll();
        panel4.add(drawDamage, BorderLayout.CENTER);
        panel4.revalidate();
        this.setVisible(true);
    }

    private void button1ActionPerformed(ActionEvent e) {
//        GameStart.instance.startGame();
    }

    private void button4ActionPerformed(ActionEvent e) {
        BorderLayout layout = (BorderLayout) this.getLayout();
        this.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        this.setVisible(false);
        this.add(panel5, BorderLayout.CENTER);
        panel6.remove(comboBox1);
        comboBox1 = new JComboBox<String>(battleMode);
        comboBox1.setMaximumRowCount(3);
        comboBox1.setSelectedIndex(0);
        comboBox1.addItemListener(e1 -> comboBox1ItemStateChanged(e1));
        panel6.add(comboBox1);
        String sql = "SELECT playerName FROM user INNER JOIN user_bindplayer ON user.user_id = user_bindplayer.user_id" +
                " WHERE nickname = ?";
        Connection con = MysqlAPI.getInstance();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, this.username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                this.Playername = rs.getString("playerName");
                rs.close();
            }else{
                JOptionPane.showMessageDialog(null, "您未绑定游戏账户", null, JOptionPane.ERROR_MESSAGE);
                rs.close();
            }
        }catch (Exception error){
            error.printStackTrace();
        }
        this.drawBattleResult(this.Playername, 0);
        this.setVisible(true);
    }

    private void drawBattleResult(String PlayerName, int ballteMode){
        int exploitsNum = 0;
        scrollPane1.remove(table1);
        Connection con = MysqlAPI.getInstance();
        String[] colNames = {"坦克名称", "场均伤害", "出战场次", "胜率"};
        String presql = "SELECT count(*) as `fightTank` FROM\n" +
                "(SELECT tankkind from battleResult\n" +
                "WHERE battleResult.playerName = ? AND `battleResult`.battlemode = ?\n" +
                "AND DATEDIFF(NOW(), `battleResult`.battletime) = 0\n" +
                "GROUP BY battleResult.tankkind) as todayResult";
        try{
            PreparedStatement ps = con.prepareStatement(presql);
            ps.setString(1, PlayerName);
            ps.setInt(2, ballteMode);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                exploitsNum = rs.getInt("fightTank");
                rs.close();
            }
        }catch (Exception e1){
            e1.printStackTrace();
        }
        if(exploitsNum == 0){
            String[][] tableValues = new String[1][colNames.length];
            for(int i = 0;i<tableValues.length;i++){
                for(int j = 0;j<colNames.length;j++){
                    tableValues[i][j] = "无记录";
                }
            }
            table1 = new JTable(tableValues, colNames);
            table1.setRowHeight(30);
            panel5.remove(scrollPane1);
            scrollPane1 = new JScrollPane(table1);
            panel5.add(scrollPane1, BorderLayout.CENTER);
            panel5.revalidate();
        }else{
            String sql = "SELECT `tankName`, AVG(damagecaused) as `avgdamage` , COUNT(*) as `battlecount`, COUNT(`victory` = 0 OR NULL)/COUNT(*) as `winrate`\n" +
                    "FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind\n" +
                    "WHERE `battleResult`.playerName = ? AND `battleResult`.battlemode = ?\n" +
                    "AND DATEDIFF(NOW(), `battleResult`.battletime) = 0\n" +
                    "GROUP BY `battleResult`.tankkind\n" +
                    "ORDER BY `battlecount` desc";

            try{
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, PlayerName);
                ps.setInt(2, ballteMode);
                ResultSet rs = ps.executeQuery();
                String[][] tableValues = new String[exploitsNum][colNames.length];
                for(int i = 0;i<tableValues.length;i++){
                    if(rs.next()){
                        tableValues[i][0] = rs.getString("tankName");
                        tableValues[i][1] = String.valueOf(rs.getFloat("avgdamage"));
                        tableValues[i][2] = String.valueOf(rs.getInt("battlecount"));
                        tableValues[i][3] = String.valueOf(rs.getFloat("winrate"));
                        }
                }
                table1 = new JTable(tableValues, colNames);
                table1.setRowHeight(30);
                panel5.remove(scrollPane1);
                scrollPane1 = new JScrollPane(table1);
                panel5.add(scrollPane1, BorderLayout.CENTER);
                panel5.revalidate();
            } catch (Exception e2){
                e2.printStackTrace();
            }
        }
    }

    public void drawCriticalRate(int battleMode){
        Connection con = MysqlAPI.getInstance();
        String[] colNames = {"排名", "坦克名称", "暴击率"};
        String sql = "SELECT `tankName`, COUNT(`damageKind` = 9 OR NULL)/ COUNT(`damageKind` IN (1, 9) OR NULL) as 'criticalrate'\n" +
                "FROM `battleDamage` INNER JOIN `tank` on `battleDamage`.attackerKind = `tank`.tankkind\n" +
                "WHERE `battleDamage`.battlemode = ?\n" +
                "GROUP BY `battleDamage`.attackerKind\n" +
                "ORDER BY `criticalrate` desc\n" +
                "LIMIT 20";
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, battleMode);
            ResultSet rs = ps.executeQuery();
            String[][] tableValues = new String[20][colNames.length];
            for(int i = 0;i<tableValues.length;i++){
                if(rs.next()){
                    tableValues[i][0] = String.valueOf(i+1);
                    tableValues[i][1] = rs.getString("tankName");
                    tableValues[i][2] = String.valueOf(rs.getFloat("criticalrate"));
                }
            }
            table2.setModel(new DefaultTableModel(tableValues, colNames));
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
            tcr.setHorizontalAlignment(JLabel.CENTER);
            table2.setDefaultRenderer(Object.class, tcr);
            table2.updateUI();
            table2.revalidate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void drawAvgDamage(int battleMode){
        Connection con = MysqlAPI.getInstance();
        String[] colNames = {"排名", "坦克名称", "单发平均伤害"};
        String sql = "SELECT `tankName`, AVG(`damage`) as `avgDamage`\n" +
                "FROM `battleDamage` INNER JOIN `tank` on `battleDamage`.attackerKind = `tank`.tankkind\n" +
                "WHERE `battleDamage`.battlemode = ?\n" +
                "GROUP BY `battleDamage`.attackerKind\n" +
                "ORDER BY `avgDamage` desc\n" +
                "LIMIT 20;";
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, battleMode);
            ResultSet rs = ps.executeQuery();
            String[][] tableValues = new String[20][colNames.length];
            for(int i = 0;i<tableValues.length;i++){
                if(rs.next()){
                    tableValues[i][0] = String.valueOf(i+1);
                    tableValues[i][1] = rs.getString("tankName");
                    tableValues[i][2] = String.valueOf(rs.getFloat("avgDamage"));
                }
            }
            table2.setModel(new DefaultTableModel(tableValues, colNames));
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
            tcr.setHorizontalAlignment(JLabel.CENTER);
            table2.setDefaultRenderer(Object.class, tcr);
            table2.updateUI();
            table2.revalidate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void comboBox1ItemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED){
            int battleState = comboBox1.getSelectedIndex();
            if(battleState == 0){
                this.drawBattleResult(this.Playername, 0);
            }else if(battleState == 1){
                this.drawBattleResult(this.Playername, 9);
            }else{
                this.drawBattleResult(this.Playername, 7);
            }
        }
    }

    private void comboBox1ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void button5ActionPerformed(ActionEvent e) {
        BorderLayout layout = (BorderLayout) this.getLayout();
        this.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        this.setVisible(false);
        this.add(panel7, BorderLayout.CENTER);
        panel8.remove(comboBox2);
        comboBox2 = new JComboBox<String>(resultMode);
        comboBox2.setMaximumRowCount(5);
        comboBox2.setSelectedIndex(0);
        comboBox2.addItemListener(e1 -> comboBox2ItemStateChanged(e1));
        panel8.add(comboBox2);
        panel8.remove(comboBox3);
        comboBox3 = new JComboBox<String>(battleMode);
        comboBox3.setMaximumRowCount(3);
        comboBox3.setSelectedIndex(0);
        comboBox3.addItemListener(e2 -> comboBox3ItemStateChanged(e2));
        panel8.add(comboBox3);
        this.drawCriticalRate(0);
        this.setVisible(true);
    }

    private void comboBox2ItemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED){
            int messageKind = comboBox2.getSelectedIndex();
            int battleMode = comboBox3.getSelectedIndex();
            switch (messageKind){
                case 0:
                    this.drawCriticalRate(battleMode); break;
                case 3:
                    this.drawAvgDamage(battleMode); break;
                default:
                    break;
            }
        }
    }

    private void comboBox3ItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        button4 = new JButton();
        button5 = new JButton();
        panel2 = new JPanel();
        panel3 = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        textField2 = new JTextField();
        panel4 = new JPanel();
        panel5 = new JPanel();
        panel6 = new JPanel();
        label3 = new JLabel();
        comboBox1 = new JComboBox();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        panel7 = new JPanel();
        panel8 = new JPanel();
        label4 = new JLabel();
        comboBox2 = new JComboBox();
        comboBox3 = new JComboBox();
        scrollPane2 = new JScrollPane();
        table2 = new JTable();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new GridLayout(5, 1, 0, 20));

            //---- button1 ----
            button1.setText("\u5f00\u59cb\u6e38\u620f");
            button1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
            button1.addActionListener(e -> {
			button1ActionPerformed(e);
			button1ActionPerformed(e);
		});
            panel1.add(button1);

            //---- button2 ----
            button2.setText("\u7528\u6237\u4fe1\u606f");
            button2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
            panel1.add(button2);

            //---- button3 ----
            button3.setText("\u5b9e\u65f6\u4f24\u5bb3\u6570\u636e");
            button3.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
            button3.addActionListener(e -> {
			button3ActionPerformed(e);
			button3ActionPerformed(e);
			button3ActionPerformed(e);
		});
            panel1.add(button3);

            //---- button4 ----
            button4.setText("\u4eca\u65e5\u6218\u7ee9");
            button4.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
            button4.addActionListener(e -> button4ActionPerformed(e));
            panel1.add(button4);

            //---- button5 ----
            button5.setText("\u5766\u514b\u5927\u6570\u636e");
            button5.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
            button5.addActionListener(e -> button5ActionPerformed(e));
            panel1.add(button5);
        }
        add(panel1, BorderLayout.WEST);

        //======== panel2 ========
        {

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGap(0, 555, Short.MAX_VALUE)
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGap(0, 420, Short.MAX_VALUE)
            );
        }
        add(panel2, BorderLayout.CENTER);

        //======== panel3 ========
        {

            //---- label1 ----
            label1.setText("\u8fde\u63a5\u6570\u636e\u5e93\u670d\u52a1\u5668\u72b6\u6001\uff1a");

            //---- label2 ----
            label2.setText("\u6e38\u620f\u8fde\u63a5\u72b6\u6001\uff1a");

            //---- textField2 ----
            textField2.setText("t");

            //======== panel4 ========
            {
                panel4.setLayout(new BorderLayout());
            }

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(textField2, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(75, Short.MAX_VALUE))
            );
            panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                            .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                            .addComponent(textField2, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel4, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
            );
        }

        //======== panel5 ========
        {
            panel5.setLayout(new BorderLayout(0, 20));

            //======== panel6 ========
            {
                panel6.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 5));

                //---- label3 ----
                label3.setText("\u6211\u7684\u4eca\u65e5\u5766\u514b\u6218\u7ee9");
                label3.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 22));
                panel6.add(label3);

                //---- comboBox1 ----
                comboBox1.addActionListener(e -> comboBox1ActionPerformed(e));
                comboBox1.addItemListener(e -> comboBox1ItemStateChanged(e));
                panel6.add(comboBox1);
            }
            panel5.add(panel6, BorderLayout.NORTH);

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(table1);
            }
            panel5.add(scrollPane1, BorderLayout.CENTER);
        }

        //======== panel7 ========
        {
            panel7.setLayout(new BorderLayout(50, 20));

            //======== panel8 ========
            {
                panel8.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 5));

                //---- label4 ----
                label4.setText("\u5168\u670d\u5766\u514b\u5927\u6570\u636e");
                label4.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 22));
                panel8.add(label4);

                //---- comboBox2 ----
                comboBox2.addItemListener(e -> comboBox2ItemStateChanged(e));
                panel8.add(comboBox2);

                //---- comboBox3 ----
                comboBox3.addItemListener(e -> comboBox3ItemStateChanged(e));
                panel8.add(comboBox3);
            }
            panel7.add(panel8, BorderLayout.NORTH);

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(table2);
            }
            panel7.add(scrollPane2, BorderLayout.CENTER);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JPanel panel2;
    private JPanel panel3;
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JTextField textField2;
    private JPanel panel4;
    private JPanel panel5;
    private JPanel panel6;
    private JLabel label3;
    private JComboBox comboBox1;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JPanel panel7;
    private JPanel panel8;
    private JLabel label4;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JScrollPane scrollPane2;
    private JTable table2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
