# Tank Battle源码分析

ServerProxy 继承 NetPeer

NetBuffer    封装后的队列，元素为byte(8位无符号整形)

NetPeer 使用socket进行进程间通信，connect， update， Send， close。

将socket中的数据读入buffer中，之后提取每个包数据，头两个字节为包的长度，接收到后，读入到内存中，进行进一步的处理，onreceive函数。第二个双字节为消息类型(NetMessage)

LocalTanker UpdatePlayerOperation 

处理用户所有的鼠标键盘输入

TankController UpdateGunFollowTarget 处理射击目标

updatetankGunRatation 取消地形导致的枪口位置改变

注释源码

```c#
vector = Quaternion.Inverse(this.mTank.GetDerivedOrientation()) * vector;

 vector3 = Quaternion.Inverse(rotation) * vector3;
```

### 战斗中伤害数据发送消息到java进程

在Battle类中添加静态成员函数

```c#
using System.IO;
using System.Runtime.InteropServices;
using System.Text;

[DllImport("Tank_Data\\Managed\\SendGameMessage.dll")]
public static extern void InsertMessage(byte[] s);

public static void ReportMessagePlus(string s)
{
	Battle.InsertMessage(Encoding.Convert(Encoding.GetEncoding("UTF-8"), Encoding.Unicode, Encoding.UTF8.GetBytes(s)));
}
```

对BattleServerProxy中的HandleShellHitTank函数进行修改，在36行处添加代码

```c#
	if (tanker2 != null)
	{
		Player player = (Player)tanker;
		string[] array = new string[21];
		array[0] = "1,";
		array[1] = tanker.GetTank().GetData().mKind.ToString();
		array[2] = ",";
		array[3] = tanker.GetTank().GetVisual().GetTankVisualData().mName;
		array[4] = ",";
		array[5] = tanker2.GetTank().GetData().mKind.ToString();
		array[6] = ",";
		array[7] = tanker2.GetTank().GetVisual().GetTankVisualData().mName;
		array[8] = ",";
		array[9] = damage.ToString();
		array[10] = ",";
		int num2 = (int)damageFlags;
		array[11] = num2.ToString();
		array[12] = ",";
		array[13] = battle.GetInfo().mMode.ToString();
		array[14] = ",";
		array[15] = battle.GetID().ToString();
		array[16] = ",";
		array[17] = "0";
		array[18] = ",";
		array[19] = player.GetRawName();
		array[20] = "\r\n";
		if (stageBattle is StageBattlePlay && battle.GetPlayer(LocalPlayer.Instance.GetDUID()).IsFriendly(tanker2))
		{
			array[17] = "1";
		}
		string s = string.Concat(array);
		if (num != 0)
		{
			Battle.ReportMessagePlus(s);
		}
```

#### 关系映射

TankData类   mKind坦克的索引(int类型)、mGrade(坦克级别ushort)、mClass(坦克类别ushort)

DamageFlags类

| DamageFlag(int) | 1    | 9    | 5      | 17                   | 0    |
| --------------- | ---- | ---- | ------ | -------------------- | ---- |
| 伤害类型        | 击穿 | 暴击 | 未击穿 | (误伤或复活模式免疫) | None |

发送数据格式

攻击坦克(kind)、攻击坦克名称(string)、被攻击坦克(kind)、被攻击坦克名称(string)、伤害值(int)、伤害类型(int)、战斗模式(int)、战斗ID(int)、是否队友误伤(int 0否1是)

对HandleShellDestroyTank进行修改，在49行处添加代码，字符串的标号为5开头。

```c#
if (tanker2 != null)
	{
		Player player = (Player)tanker;
		string[] array = new string[21];
		array[0] = "5,";
		array[1] = tanker.GetTank().GetData().mKind.ToString();
		array[2] = ",";
		array[3] = tanker.GetTank().GetVisual().GetTankVisualData().mName;
		array[4] = ",";
		array[5] = tanker2.GetTank().GetData().mKind.ToString();
		array[6] = ",";
		array[7] = tanker2.GetTank().GetVisual().GetTankVisualData().mName;
		array[8] = ",";
		array[9] = damage.ToString();
		array[10] = ",";
		int num2 = (int)damageFlags;
		array[11] = num2.ToString();
		array[12] = ",";
		array[13] = battle.GetInfo().mMode.ToString();
		array[14] = ",";
		array[15] = battle.GetID().ToString();
		array[16] = ",";
		array[17] = "0";
		array[18] = ",";
		array[19] = player.GetRawName();
		array[20] = "\r\n";
		if (stageBattle is StageBattlePlay && battle.GetPlayer(LocalPlayer.Instance.GetDUID()).IsFriendly(tanker2))
		{
			array[17] = "1";
		}
		string s = string.Concat(array);
		if (num != 0)
		{
			Battle.ReportMessagePlus(s);
		}
```

消息细节同上

### 对于战斗结束战绩的记录

BattleReport 记录所有信息

TankerBattleReport()      1.tank info  2. PlayerBattleScore  记录对局中某一玩家成绩

比赛开始时记录玩家信息：

StageBattle  --> HandleBaattlePrepared --> BattleReport.RecordTanker() -->record tank info

比赛结束后记录服务器传回结果：

WorldServerProxy -->HandleBattleResult --> RecordTankerScore() --> record score info

最后修改：

TankBattleReport类中添加成员   

```c#
public string mTankName;
public BattleTeam mTankTeam;
public bool mIsFriend;
```

BattleReport.RecordTanker函数中添加       

```c#
16行
tankerBattleReport.mTankName = tanker.GetTank().GetVisual().GetTankVisualData().mName;
tankerBattleReport.mTankTeam = tanker.GetTeam();
38行
StageBattle stageBattle = StageManager.Instance.GetCurrentStage() as StageBattle;
if (stageBattle == null)
{
    return;
}
Battle battle = stageBattle.GetBattle();
if (battle == null)
{
    return;
}
tankerBattleReport.mIsFriend = battle.GetPlayer(LocalPlayer.Instance.GetDUID()).IsFriendly(tanker);
```

在BattleReport中添加成员函数

```c#
public void ReportMessageOutside(ushort victory)
{
	for (int i = 0; i < 2; i++)
	{
		foreach (TankerBattleReport tankerBattleReport in this.mTankerReports[i])
		{
			string text = string.Concat(new string[]
			{
				"3",
				",",
				tankerBattleReport.mTank.ToString(),
				",",
				tankerBattleReport.mTankName,
				",",
				tankerBattleReport.mScore.mSurvived.ToString(),
				",",
				tankerBattleReport.mScore.mEnemiesDestroyed.ToString(),
				",",
				tankerBattleReport.mScore.mDamageCaused.ToString(),
				",",
				tankerBattleReport.mScore.mPotentialDamageReceived.ToString(),
				",",
				tankerBattleReport.mScore.mShotsFired.ToString(),
				",",
				tankerBattleReport.mScore.mHits.ToString(),
				",",
				tankerBattleReport.mScore.mPenetrations.ToString(),
				",",
				tankerBattleReport.mScore.mHitsReceived.ToString(),
				",",
				this.mInfo.mMode.ToString(),
				",",
				tankerBattleReport.mRawName,
				","
			});
			if (tankerBattleReport.mIsFriend)
			{
				text = text + victory.ToString() + "\r\n";
			}
			else if (victory == 0)
			{
				text += "1\r\n";
			}
			else if (victory == 1)
			{
				text += "0\r\n";
			}
			else
			{
				text = text + victory.ToString() + "\r\n";
			}
			Battle.ReportMessagePlus(text);
		}
	}
}
```

victory (ushot) 映射关系

| victory    | 0    | 1    | 2 or else |
| ---------- | ---- | ---- | --------- |
| ushort类型 | 胜利 | 失败 | 平局      |

在WorldServerProxy.HandleBattleResult函数中添加

```c#
30行
		if (report != null)
		{
			report.ReportMessageOutside(playerBattleResult.mVictory);
		}
```

战斗结果数据 坦克种类(int)、坦克名称(string)、是否存活(bool)、击毁数量(int)、伤害(int)、潜在伤害(int)、射击数量(int)、命中数(int)、穿透数(int)、被击中数量(int)、战斗模式(string)。

#### 数据大小端的问题

参考文档https://blog.csdn.net/QQxiaoqiang1573/article/details/84937863

utf16总共有三种形式 utf-16le(小端)、utf-16be(大端)、utf16(四字节)

#### 打开游戏时发送数据

GUIEntityChat Initialise()函数中添加

```
160行
	try
	{
		this.InsertAnnouncementMessage("Tank Batttle盒子正在提供服务，祝您游戏愉快！");
		Battle.ReportMessagePlus("Game Start\r\n");
	}
	catch (Exception)
	{
	}
```

#### 修改坦克视距

TankCameraController中Setup函数修改

```
	if (ExternTask.getProperties("viewDistance"))
	{
		TankCameraController.MaxCameraDistance = 100f;
		TankCameraController.MinSniperCameraFieldOfView = 2f;
	}
	else
	{
		TankCameraController.MaxCameraDistance = 25f;
		TankCameraController.MinSniperCameraFieldOfView = 5f;
	}
```

#### 初始化发送坦克基本数据 在TH命名空间添加新类BattleTankInfo

```c#
using System;
using System.Collections.Generic;

namespace TH
{
	internal class BattleTankInfo
	{
		public static BattleTankInfo getBattleTankInfo(uint battleId)
		{
			foreach (BattleTankInfo battleTankInfo in BattleTankInfo.tankInfos)
			{
				if (battleTankInfo.mBattle.GetID() == battleId)
				{
					return battleTankInfo;
				}
			}
			return null;
		}
        
		public static BattleTankInfo addBattleTankInfo(Battle battle)
		{
			BattleTankInfo battleTankInfo = BattleTankInfo.getBattleTankInfo(battle.GetID());
			if (battleTankInfo == null)
			{
				battleTankInfo = new BattleTankInfo(battle);
				BattleTankInfo.tankInfos.Add(battleTankInfo);
			}
			return battleTankInfo;
		}
        
		public BattleTankInfo(Battle battle)
		{
			this.mBattle = battle;
		}
        
        public void reportTankInfo()
        {
            foreach (Tanker tanker in this.mBattle.GetTankers())
            {
                uint tankKind = tanker.GetTank().GetData().mKind;
                string tankName = tanker.GetTank().GetVisual().GetTankVisualData().mName;
                uint tankClass = (uint)tanker.GetTank().GetData().mClass;
                uint tankGrade = (uint)tanker.GetTank().GetData().mGrade;
                string tankDescription = tanker.GetTank().GetVisual().GetTankVisualData().mDescription;
                tankDescription = tankDescription.Replace("\n", "").Replace("\r", "");
                Battle.ReportMessagePlus(string.Concat(new string[]
                {
                    "6,",
                    tankKind.ToString(),
                    ",",
                    tankName,
                    ",",
                    tankClass.ToString(),
                    ",",
                    tankGrade.ToString(),
                    ",",
                    tankDescription,
                    "\r\n"
                }));
            }
        }
		public Battle mBattle;
		public static List<BattleTankInfo> tankInfos = new List<BattleTankInfo>();
	}
}
```

修改StageBattle中的函数 HandleBattleBegin()

```c#
			BattleTankInfo battleTankInfo = BattleTankInfo.addBattleTankInfo(this.mBattle).reportTankInfo();
```

#### 修改弹道Missle类中的kind改为5U(虎啸弹道)

```
	if (battleInfo != null)
				{
					kind = battleInfo.mMissileKind;
					kind = 5U;
				}
```

