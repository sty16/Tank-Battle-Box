package mysqlapi;
import gamedata.BattleResult;
import gamedata.Damage;
import gamedata.TankInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MysqlAPI {
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static  final String url = "jdbc:mysql://123.56.118.161:3306/TankData?useSSL=false&serverTimezone=Asia/Shanghai";
    private static  final String user = "root";
    private static  final String password = "123456";
    private static  Connection con = null;
    public static boolean status = false;
    public static  boolean connectServer() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                status = true;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (con == null) {
            System.out.println("connect mysql server failed");
            return false;
        }else{
            status = true;
            return true;
        }
    }
    public static Connection getInstance(){
        return con;
    }

    public static  boolean closeServer(){
        if(con != null){
            try{
                con.close();
                con = null;
                status = false;
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        con = null;
        return false;
    }

    public static void addBattleResult(BattleResult param){
        PreparedStatement ps;
        String sql = "insert into battleResult(playerName, tankkind, victory, survival, enemiesdestroyed, "
                + "damagecaused, damagereceived, shotsfired, hits, penetrations, hitsreceived, battlemode, battletime)"
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, param.mPlayerName);
            ps.setInt(2, param.mKind);
            ps.setInt(3, param.mVictory);
            ps.setBoolean(4,param.msurvival);
            ps.setInt(5,param.mEnemiesDestroyed);
            ps.setInt(6, param.mDamageCaused);
            ps.setInt(7, param.mDamageReceived);
            ps.setInt(8,param.mShotsFired);
            ps.setInt(9,param.mHits);
            ps.setInt(10,param.mPenetrations);
            ps.setInt(11,param.mHitsReceived);
            ps.setInt(12,param.mMode);
            ps.setTimestamp(13,new Timestamp(param.mBattleTime.getTime()));
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void addBattleDamage(Damage param){
        PreparedStatement ps;
        String sql = "insert into battleDamage"
                + "(attackerKind, attackerPlayerName, victimKind, damage, damageKind, battleMode, battleId, IsFriend, battleTime)"
                + "values(?,?,?,?,?,?,?,?,?)";
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, param.mAttackerKind);
            ps.setString(2, param.mAttackerPlayerName);
            ps.setInt(3,param.mVictimKind);
            ps.setInt(4, param.mDamage);
            ps.setInt(5, param.mDamageKind);
            ps.setInt(6,param.mMode);
            ps.setInt(7,param.mBattleId);
            ps.setInt(8, param.mIsFriend);
            ps.setTimestamp(9, new Timestamp(param.mBattleTime.getTime()));
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void addTankInfo(TankInfo tankInfo){
        PreparedStatement ps;
        String sql = "insert into tank"
                + "(tankKind, tankName, tankClass, tankGrade, tankDescription)"
                + "values(?,?,?,?,?)";
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, tankInfo.tankKind);
            ps.setString(2, tankInfo.tankName);
            ps.setInt(3, tankInfo.tankClass);
            ps.setInt(4, tankInfo.tankGrade);
            ps.setString(5, tankInfo.tankDesc);
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  static List<Integer> requestTankInfo(){
        PreparedStatement ps;
        List<Integer> tankKinds = new ArrayList<>();
        String sql = "select distinct tankKind from tank";
        try {
            ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Integer tankKind = Integer.valueOf(rs.getInt("tankKind"));
                tankKinds.add(tankKind);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    return tankKinds;
    }
}
