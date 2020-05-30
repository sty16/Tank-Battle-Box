package serverproxy;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import TankBox.DrawDamage;
import TankBox.TankBox;
import TankBox.TankBoxUserPanel;
import gamedata.Damage;
import gamedata.TankInfo;
import mysqlapi.MysqlAPI;
import gamedata.BattleResult;

public class ServerProxy {
    private static ServerSocket server;
    public static boolean status = false;
    private DrawDamage drawDamage = new DrawDamage();
    public ServerProxy() throws IOException{
    server = new ServerSocket(8999);
    }
    public static void start() throws IOException{
        System.out.println("等待系统连接");
        Socket socket = server.accept();
//        BufferedReader bw = new BufferedReader(new InputStreamReader(socket.getInputStream(),"Unicode"));
        try{
            status = true;
            InputStream in = socket.getInputStream();
            BufferedReader bw = new BufferedReader(new InputStreamReader(in, "Unicode"));
            System.out.println("连接成功");
            String temp;
            List<Integer> tankKinds = MysqlAPI.requestTankInfo();
            while((temp = bw.readLine())!= null)
            {
                try{
                    if(temp != null){
                        String[] result = temp.split(",", 2);
                        switch (result[0]){
                            case "3":
                                BattleResult br = new BattleResult(temp);
                                if(br.mKind != -1) {
                                    MysqlAPI.addBattleResult(br);
                                }
                                break;
                            case "1":
                            case "5":
                                TankBoxUserPanel.drawDamage.PushString(temp);
                                TankBoxUserPanel.drawDamage.repaint();
                                TankBoxUserPanel.drawDamage.updateUI();
                                Damage damage = new Damage(temp);
                                if(damage.mAttackerKind != -1){
                                    MysqlAPI.addBattleDamage(damage);
                                }
                                break;
                            case "6":
                                TankInfo tankinfo = new TankInfo(temp);
                                if(tankKinds.contains(Integer.valueOf(tankinfo.tankKind))){
                                    break;
                                }else{
                                    MysqlAPI.addTankInfo(tankinfo);
                                }
                            default:
                                break;
                        }
                    }
                    if(temp.equals("disconnect")){     //处理断开连接
                        System.out.println("yes");
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                        break;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                    break;
                }
            }
            System.out.println("finish");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
