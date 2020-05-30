#基本增删改查：
#用户绑定玩家昵称信息查询
SELECT playerName FROM `user` INNER JOIN user_bindplayer ON `user`.user_id = user_bindplayer.user_id
WHERE nickname = '空降皇后无情'

DELETE FROM `battle_result` 
WHERE `tankkind` = 29 AND `damagecaused` = 0; 

DELETE FROM `battleResult`
WHERE `playerName` = '我要空降皇后（无情）'AND `damagecaused` = 0;

update user set `nickname`= '这个杀手有点冷', mobile='13121265866' where `user_id` = 1

INSERT INTO `user`(`nickname`,`userPassword`,`mobile`,`email`,`gender`,`birthday`,`address`) VALUES
('user2', '123456','13121265866','abletian@gmail.com','男',NULL,NULL),
('user3','123456','15548972058','123084784@qq.com','女',NULL,NULL)

TRUNCATE TABLE `user`;
INSERT INTO `tank`(`tankKind`, `tankName`, `tankClass`, `tankGrade`,`tankDescription`) VALUES
('315','AB快枪手Jaguar','1','8','')
('342','教皇','0','8','黄道十二宫之首！幕后的操纵者！共3种战斗形态')
('138','杜达耶夫E4', '3', '8', '全方面超越T110E4，更强更持久！杜达耶夫【华东二区】的专属座驾')

INSERT INTO `playerInfo`(`playerName`) 
(SELECT  DISTINCT `playerName` FROM `battleResult`
WHERE `playerName` NOT IN (SELECT DISTINCT `playerName` FROM `playerInfo`))

ALTER TABLE `playerInfo` DROP `playerId`
ALTER TABLE	`playerInfo` ADD `playerId` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT FIRST;


#复杂查询
#某玩家当日的坦克出战情况
SELECT `tankName`, AVG(damagecaused) as `场均伤害` , COUNT(*) as `场次`, COUNT(`victory` = 0 OR NULL)/COUNT(*) as `winrate`
FROM `battleResult` INNER JOIN 	`tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.playerName = 'づい義薄雲天づい淡雅风致' AND `battleResult`.battlemode = 0
AND DATEDIFF(NOW(), `battleResult`.battletime) = 0
GROUP BY `battleResult`.tankkind
ORDER BY `winrate` desc


#单发平均前20，使用了rownum来显示排名信息							
SET @rownum=0;						
SELECT  @rownum:=@rownum+1 as rownum, `TB`.*
FROM(
SELECT `tankName`, AVG(damage) as 'Average damage per shot'
FROM `battleDamage` INNER JOIN `tank` on `battleDamage`.attackerKind = `tank`.tankkind
WHERE `battleDamage`.battlemode = 0
GROUP BY `battleDamage`.attackerKind
ORDER BY `Average damage per shot` desc
LIMIT 20
) As `TB`

#暴击率
SELECT `tankName`, COUNT(`damageKind` = 9 OR NULL)/ COUNT(`damageKind` IN (1, 9) OR NULL) as 'criticalrate'
FROM `battleDamage` INNER JOIN `tank` on `battleDamage`.attackerKind = `tank`.tankkind
WHERE `battleDamage`.battlemode = 0
GROUP BY `battleDamage`.attackerKind
ORDER BY `criticalrate` desc
LIMIT 20
#命中率
SELECT `tankName`, SUM(`hits`)/SUM(`shotsfired`) as 'accuracy'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `accuracy` desc
LIMIT 20
#击穿率						
SELECT `tankName`, SUM(`penetrations`)/SUM(`shotsfired`) as 'penetration rate'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `penetration rate` desc
LIMIT 20
#单发平均伤害
SELECT `tankName`, AVG(damage) as 'Average damage per shot'
FROM `battleDamage` INNER JOIN `tank` on `battleDamage`.attackerKind = `tank`.tankkind
WHERE `battleDamage`.battlemode = 0
GROUP BY `battleDamage`.attackerKind
ORDER BY `Average damage per shot` desc
LIMIT 20
#单发最高伤害
SELECT `tankName`, MAX(damage) as 'Max damage per shot'
FROM `battleDamage` INNER JOIN `tank` on `battleDamage`.attackerKind = `tank`.tankkind
WHERE `battleDamage`.battlemode = 0
GROUP BY `battleDamage`.attackerKind
ORDER BY `Max damage per shot` desc
LIMIT 20
#坦克胜率
SELECT `tankName`, COUNT(`victory` = 0 OR NULL)/COUNT(`battleResult`.tankkind) as  `tank win rate`
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `tank win rate` desc
LIMIT 20
#坦克存活率
SELECT `tankName`, COUNT(`survival` = 1 OR NULL)/COUNT(`battleResult`.tankkind) as  `tank survival rate`
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `tank survival rate` desc
LIMIT 20
#单场伤害
SELECT `tankName`, MAX(`damagecaused`) as 'max damage casued'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `max damage casued` desc
LIMIT 20
#场均伤害
SELECT `tankName`, AVG(`damagecaused`) as 'average damage casued'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `average damage casued` desc
LIMIT 20
#单场承受伤害
SELECT `tankName`, MAX(`damagereceived`) as 'max damage received'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `max damage received` desc
LIMIT 20
#场均承受伤害
SELECT `tankName`, AVG(`damagereceived`) as 'average damage received'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `average damage received` desc
LIMIT 20
#场均被击中次数
SELECT `tankName`, AVG(`hitsreceived`) as 'average hits received'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `average hits received` desc
LIMIT 20
#场均摧毁敌人数
SELECT `tankName`, AVG(`enemiesdestroyed`) as 'average enemies destroyed'
FROM `battleResult` INNER JOIN `tank` on `battleResult`.tankkind = `tank`.tankkind
WHERE `battleResult`.battlemode = 0
GROUP BY `battleResult`.tankKind
ORDER BY `average enemies destroyed` desc
LIMIT 20

#创建userbattleinfo  视图
CREATE VIEW userbattleinfo AS
SELECT 	`battleResult`.playerName AS playerName,
   user_id,tankName,`tank`.tankkind AS tankkind,victory,survival,
   enemiesdestroyed,damagecaused,damagereceived,
   shotsfired,hits,penetrations,hitsreceived,
   battlemode,battletime
FROM `battleResult` INNER JOIN `user_bindplayer` ON `battleResult`.playerName = `user_bindplayer`.playerName
INNER JOIN `tank` ON`battleResult`.tankkind = `tank`.tankkind
ORDER BY battletime DESC

#查询某一用户的全部游戏记录
SELECT *
FROM `userbattleinfo`
where user_id=7;

#查询用户坦克情况
SELECT tankName,COUNT(tankkind)as usingtimes,COUNT(`victory`=0 or Null)/COUNT(tankkind) as 'win rate',
COUNT(`survival`=1 or Null) /COUNT(tankkind) as 'survival rate',AVG(`damagecaused`) as 'average damage casued',
AVG(`damagereceived`) as 'average damage received', AVG(`hitsreceived`) as 'average hits received',
AVG(`enemiesdestroyed`) as 'average enemies destroyed',SUM(`hits`)/SUM(`shotsfired`) as 'accuracy',
SUM(`penetrations`)/SUM(`shotsfired`) as 'penetration rate'
FROM `userbattleinfo`
WHERE user_id=7
GROUP BY tankkind
ORDER BY usingtimes desc;