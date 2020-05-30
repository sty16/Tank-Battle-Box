package TankBox;

import gamedata.Damage;

import javax.swing.*;
import java.awt.*;

public class DrawDamage extends JPanel {
    public static Damage[] damages = new Damage[5];
    public DrawDamage(){
        for(int i=0;i<5;i++){
            damages[i] = new Damage("");
        }
    }
    public void paint(Graphics g){
        super.paint(g);
        Font font = new Font("宋体", Font.BOLD,16);
        g.setFont(font);
        for(int i = 0;i<5;i++)
        {
            g.setColor(Color.red);
            if(damages[i].mAttackerKind > 0){
                if(damages[i].mMessageKind == 1){
                    if(damages[i].mIsFriend == 0){
                        g.setColor(Color.GREEN);
                        g.drawString(damages[i].mAttackerName,40 , 65+i*60);
                        g.drawString("->", 200, 65+i*60);
                        g.setColor(Color.red);
                        g.drawString(damages[i].mVictimName, 250, 65+i*60);
                        g.drawString(String.valueOf(damages[i].mDamage)+"("+damages[i].getDamageType()+")", 370, 65+i*60);
                    }else {
                        g.drawString(damages[i].mAttackerName, 40, 65 + i * 60);
                        g.drawString("->", 200, 65 + i * 60);
                        g.setColor(Color.GREEN);
                        g.drawString(damages[i].mVictimName, 250, 65 + i * 60);
                        g.setColor(Color.red);
                        g.drawString(String.valueOf(damages[i].mDamage) + "(" + damages[i].getDamageType() + ")", 370, 65 + i * 60);
                    }
                }else if(damages[i].mMessageKind == 5) {
                    if (damages[i].mIsFriend == 0) {
                        g.setColor(Color.GREEN);
                        g.drawString(damages[i].mAttackerName, 40, 65 + i * 60);
                        g.drawString("->", 200, 65 + i * 60);
                        g.setColor(Color.red);
                        g.drawString(damages[i].mVictimName, 250, 65 + i * 60);
                        g.drawString(String.valueOf(damages[i].mDamage) + "(击毁)", 370, 65 + i * 60);
                    } else {
                        g.drawString(damages[i].mAttackerName, 40, 65 + i * 60);
                        g.drawString("->", 200, 65 + i * 60);
                        g.setColor(Color.GREEN);
                        g.drawString(damages[i].mVictimName, 250, 65 + i * 60);
                        g.setColor(Color.red);
                        g.drawString(String.valueOf(damages[i].mDamage) + "(击毁)", 370, 65 + i * 60);
                    }
                }else{}
            }
        }
    }
    public static void PushString(String temp){
        for(int i=4;i>0;i--){
            damages[i] = damages[i-1];
        }
        damages[0] = new Damage(temp);
    }
}
