package gamedata;

import java.util.Date;

public class BattleResult {
    public String mPlayerName;
    public int mKind;
    public int mVictory;
    public String mtankName;
    public boolean msurvival;
    public int mEnemiesDestroyed;
    public int mDamageCaused;
    public int mDamageReceived;
    public int mShotsFired;
    public int mHits;
    public int mPenetrations;
    public int mHitsReceived;
    public int mMode;
    public Date mBattleTime;

    public BattleResult(String s){
        String[] result = s.split(",");
        if(result.length == 14){
            int messageKind = Integer.parseInt(result[0]);
            this.mKind = Integer.parseInt(result[1]);
            this.mtankName = result[2];
            this.msurvival = Boolean.parseBoolean(result[3]);
            this.mEnemiesDestroyed = Integer.parseInt(result[4]);
            this.mDamageCaused = Integer.parseInt(result[5]);
            this.mDamageReceived = Integer.parseInt(result[6]);
            this.mShotsFired = Integer.parseInt(result[7]);
            this.mHits = Integer.parseInt(result[8]);
            this.mPenetrations = Integer.parseInt(result[9]);
            this.mHitsReceived = Integer.parseInt(result[10]);
            this.mMode = Integer.parseInt(result[11]);
            this.mBattleTime = new Date();
            this.mPlayerName = result[12];
            this.mVictory = Integer.parseInt(result[13]);
        }else{
            this.mKind = -1;
        }

    }

    public String GetBattleMode(){
        switch (mMode){
            case 0:
                return "标准模式PVP";
            case 7:
                return "复活模式";
            case 9:
                return "天梯模式";
            default:
                return "";
        }
    }
}
