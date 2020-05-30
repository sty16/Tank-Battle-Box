package gamedata;

import java.util.Date;

public class Damage {
    public int mMessageKind;
    public int mAttackerKind;
    public String mAttackerName;
    public int mVictimKind;
    public String mVictimName;
    public int mDamage;
    public int mDamageKind;
    public int mMode;
    public int mBattleId;
    public int mIsFriend;
    public Date mBattleTime;
    public String mAttackerPlayerName;

    public Damage(String s){
        String[] result = s.split(",", 11);
        if(result.length == 11){
            this.mMessageKind = Integer.parseInt(result[0]);
            this.mAttackerKind = Integer.parseInt(result[1]);
            try{
                byte[] mAttackerNameUTF16 = result[2].getBytes("UTF-16");
                byte[] mVictimeNameUTF16 = result[4].getBytes("UTF-16");
                this.mAttackerName = new String(mAttackerNameUTF16, "UTF-16");
                this.mVictimName = new String(mVictimeNameUTF16, "UTF-16");
            }catch (Exception e){
                this.mAttackerName = result[2];
                this.mVictimName = result[4];
            }
            this.mVictimKind = Integer.parseInt(result[3]);
            this.mDamage = Integer.parseInt(result[5]);
            this.mDamageKind = Integer.parseInt(result[6]);
            this.mMode = Integer.parseInt(result[7]);
            this.mBattleId = Integer.parseInt(result[8]);
            this.mIsFriend = Integer.parseInt(result[9]);
            this.mBattleTime = new Date();
            this.mAttackerPlayerName = result[10];
        }else{
            mAttackerKind = -1;
        }

    }

    public String getDamageType(){
        switch (mDamageKind){
            case 1:
                return "击穿";
            case 9:
                return "暴击";
            case 5:
                return "未击穿";
            case 17:
                return "误伤";
            default:
                return "";
        }
    }
}
