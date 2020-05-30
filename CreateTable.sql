CREATE TABLE `battleResult`
(
`battleResult_id` BIGINT AUTO_INCREMENT,
`playerName` VARCHAR(20) NOT NULL,
`tankkind` INT NOT NULL,
`victory` INT NOT NULL,
`survival` TINYINT NOT NULL,
`enemiesdestroyed` INT NOT NULL,
`damagecaused` INT NOT NULL,
`damagereceived` INT NOT NULL,
`shotsfired` INT NOT NULL,
`hits` INT NOT NULL,
`penetrations` INT NOT NULL,
`hitsreceived` INT NOT NULL,
`battlemode` INT NOT NULL,
`battletime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY(`battleResult_id`),
 CONSTRAINT `battleResult_tankkind_fk` FOREIGN KEY (`tankkind`)
 REFERENCES `tank`(`tankkind`)
 ON UPDATE no action
 ON DELETE CASCADE
);

CREATE TABLE `battleDamage`
(
`battle_damage_id` BIGINT AUTO_INCREMENT,
`attackerPlayerName` VARCHAR(20) NOT NULL,
`attackerKind` INT NOT NULL,
`victimKind` INT NOT NULL,
`damage` INT NOT NULL,
`damageKind` INT NOT NULL,
`battleMode` INT NOT NULL,
`battleId` INT NOT NULL,
`IsFriend` TINYINT NOT NULL,
`battleTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY(`battle_damage_id`),
CONSTRAINT `battleDamage_fk1` FOREIGN KEY (`attackerKind`)
 REFERENCES `tank`(`tankkind`)
 ON UPDATE no action
 ON DELETE CASCADE,
 CONSTRAINT `battleDamage_fk2` FOREIGN KEY (`victimKind`)
 REFERENCES `tank`(`tankkind`)
 ON UPDATE no action
 ON DELETE CASCADE
)

CREATE TABLE `user`
(
`user_id` BIGINT AUTO_INCREMENT,
`nickname` VARCHAR(50) NOT NULL,
`mobile` VARCHAR(11) NOT NULL,
`email` VARCHAR(20),
`gender` VARCHAR(10),
`birthday` VARCHAR(20),
`address` VARCHAR(50),
`registration_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY(`user_id`)
);
ALTER TABLE `user` ADD COLUMN userPassword VARCHAR(256) NOT NULL AFTER `nickname`;

CREATE TABLE `user_bindplayer`
(
`user_id` BIGINT NOT NULL,
`playerName` VARCHAR(20) NOT NULL,
`bind_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY(`user_id`, `playerName`),
CONSTRAINT `user_bindplayer_user_id_fk` FOREIGN KEY (`user_id`)
REFERENCES `user`(`user_id`)
)

CREATE TABLE `tank`
(
`tankkind` INT NOT NULL PRIMARY KEY,
`tankName` VARCHAR(20) NOT NULL,
`tankClass` INT NOT NULL,
`tankGrade` INT NOT NULL,
`tankDescription` VARCHAR(200) NOT NULL
)

CREATE TABLE `playerInfo`
(
`playerId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`playerName` VARCHAR(20) NOT NULL
)

CREATE TABLE `userCommentResult`
(
`user_id` BIGINT NOT NULL,
`battleResult_id` BIGINT NOT NULL,
`comment` VARCHAR(200) NOT NULL,
`comment_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT `user_fk` FOREIGN KEY(`user_id`)
REFERENCES `user`(`user_id`),
CONSTRAINT `battleResult_fk` FOREIGN KEY(`battleResult_id`)
REFERENCES `battleResult`(`battleResult_id`),
PRIMARY KEY(`user_id`,`battleResult_id`)
)















