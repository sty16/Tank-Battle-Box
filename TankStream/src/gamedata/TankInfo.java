package gamedata;

public class TankInfo {
    public int tankKind;
    public String tankName;
    public int tankClass;
    public int tankGrade;
    public String tankDesc;

    public TankInfo(String s){
        String[] result = s.split(",", 6);
        this.tankKind = Integer.parseInt(result[1]);
        this.tankName = result[2];
        this.tankClass = Integer.parseInt(result[3]);
        this.tankGrade = Integer.parseInt(result[4]);
        this.tankDesc = result[5];
    }
}
