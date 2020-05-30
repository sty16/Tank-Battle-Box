package tankstream;

import mysqlapi.MysqlAPI;
import serverproxy.ServerProxy;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TankBoxLogin extends JFrame {

    JButton m_loginButton;
    private JLayeredPane layerdPanel;


    public void run(){
        layerdPanel = new JLayeredPane();
        setTitle("TankBox");
        ImageIcon bg = new ImageIcon(".\\resources\\Img\\tank.jpg");
        JLabel label = new JLabel(bg);
        label.setBounds(0, 0, bg.getIconWidth(), bg.getIconHeight());
        JPanel contentPanel = (JPanel) new JPanel();
        contentPanel.setOpaque(true);
        contentPanel.setLayout(new BorderLayout());
        ImageIcon mozhana = new ImageIcon(".\\resources\\Img\\mozhana.jpg");
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.setOpaque(true);
        topPanel.add(new JButton("无语"));
        contentPanel.add(topPanel,"Center");
//        JPanel ButtonPanel = getButtonPanel();
//        contentPanel.add(ButtonPanel, "South");
        layerdPanel.add(label, Integer.MIN_VALUE    );
        layerdPanel.add(contentPanel, JLayeredPane.DEFAULT_LAYER);
//        getLayeredPane().add(label, Integer.MIN_VALUE);
        this.setLayeredPane(layerdPanel);
        setBounds(100,100,650,500);
//        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    private JPanel getTopPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(new JLabel(""));
        JPanel userInfo = getUserInfoPanel();
        userInfo.setOpaque(true);
        panel.add(userInfo);
        return panel;
    }
    private JPanel getUserInfoPanel(){
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new FlowLayout());
//        JLabel portrait = new JLabel(new ImageIcon("\\resources\\Img\\mozhana.jpg"));
//        userInfoPanel.add(portrait);
        JPanel accountPanel = getAccountPanel();
//        accountPanel.setOpaque(false);
        userInfoPanel.add(accountPanel);
        JPanel registerPanel = getRegisterPanel();
//        registerPanel.setOpaque(false);
        userInfoPanel.add(registerPanel);
        return userInfoPanel;
    }
    private  JPanel getAccountPanel(){
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BorderLayout(0, 7));
        String [] ids = {
                "1308478462",
                "675035422"
        };
        JComboBox<String> user = new JComboBox<String>(ids);
        user.setEditable(false);
        accountPanel.add(user, "North");
        JPasswordField password = new JPasswordField(15);
        accountPanel.add(password, "Center");
        JPanel checkPanel = getCheckPanel();
        checkPanel.setOpaque(false);
        accountPanel.add(checkPanel, "South");
        return accountPanel;
    }

    private JPanel getRegisterPanel()
    {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));

        JButton button1 = new JButton("注册帐号");
        button1.setBorderPainted(false);
        button1.setFocusPainted(false);
        button1.setContentAreaFilled(false); //设置透明
        button1.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerPanel.add(button1);
//        button1.addActionListener(this);

        JButton button2 = new JButton("找回密码");
        button2.setBorderPainted(false);
        button2.setFocusPainted(false);
        button2.setContentAreaFilled(false);
        button2.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerPanel.add(button2);
//        button2.addActionListener(this);
        return(registerPanel);
    }

    private JPanel getCheckPanel(){
        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new BoxLayout((JPanel)checkPanel, BoxLayout.X_AXIS));
        Font font = new Font("宋体", Font.BOLD, 12);
        JCheckBox check1 = new JCheckBox("记住密码", true);
        check1.setFont(font);
        checkPanel.add(check1);
        JCheckBox check2 = new JCheckBox("自动登录", false);
        check2.setFont(font);
        checkPanel.add(check2);
        return(checkPanel);
    }
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout(50,0));

        // buttonPanel = button1 + button2
//        JButton button1 = new JButton(new ImageIcon(".\\resources\\Img\\left.jpg"));
//        button1.setBorderPainted(false);
//        button1.setFocusPainted(false);
//        button1.setContentAreaFilled(false);
//        buttonPanel.add(button1,"West");

        m_loginButton = new JButton("      登           录      ");
        buttonPanel.add(m_loginButton, "Center");
//        m_loginButton.addActionListener(this);

//        JButton button2 = new JButton(new ImageIcon(".\\resources\\Img\\leftbottom.png"));
//        button2.setBorderPainted(false);
//        button2.setFocusPainted(false);
//        button2.setContentAreaFilled(false);
//        buttonPanel.add(button2 , "East");
//        button2.addActionListener(this);
        return(buttonPanel);
    }

}
