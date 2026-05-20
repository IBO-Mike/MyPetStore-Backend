/*
 * MyPetStore Complete Database Schema
 * Integrated from: compare_schema.sql, jpetstore.sql, mypetstore.sql
 * Server version: 9.5.0
 * Date: 2026-03-25
 */

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ===========================
-- Table structure for `account`
-- ===========================
DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userid` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `email` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `firstname` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `lastname` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `status` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `addr1` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `addr2` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `city` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `state` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `zip` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `country` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `phone` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `langpref` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `favcategory` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `mylistopt` int DEFAULT NULL,
  `banneropt` int DEFAULT NULL,
  `password` varchar(25) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userid` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'ACID','acid@yourdomain.com','ABC','XYX','OK','901 San Antonio Road','MS UCUP02-206','Palo Alto','CA','94303','USA','555-555-5555','English','CATS',1,1,'ACID','2026-03-25 20:18:26','2026-03-25 20:18:26',0),(2,'MikeShi','mike@example.com','Mike','Shi','OK','No Address','No Address','Unknown','Unknown','000000','China','13800000000','English','DOGS',0,0,'Mike@120515','2026-03-25 20:18:26','2026-03-25 20:18:26',0),(3,'UserMike','swft2006@gmail.com','Mike','Shi','OK','None','None','Changsha','Hunan','410000','China','18005735686','English','DOGS',0,0,'mike1234','2026-03-25 20:18:26','2026-03-25 20:18:26',0),(4,'admin','admin@example.com','Admin','User','OK','Head Office','Head Office','Beijing','Beijing','100000','China','13900000000','English','Dogs',0,0,'123456','2026-03-25 20:18:26','2026-03-25 20:18:26',0),(5,'j2ee','yourname@yourdomain.com','ABC','XYX','OK','901 San Antonio Road','MS UCUP02-206','Palo Alto','CA','94303','USA','555-555-5555','English','DOGS',1,1,'j2ee','2026-03-25 20:18:26','2026-03-25 20:18:26',0),(6,'jys','jys@example.com','Yusen','Jiao','OK','Dormitory','Room 23','Changsha','Hunan','410000','China','13600000000','English','DOGS',0,0,'123456','2026-03-25 20:18:26','2026-03-25 20:18:26',0),(7,'mike','swft2006@gmail.com','Mike','Shi','OK','None','None','Jiaxing','Zhejiang','314000','China','18005735686','English','DOGS',0,0,'Mike@120515','2026-03-25 20:18:26','2026-03-25 20:18:26',0),(8,'web','web@example.com','Web','User','OK','Office','Office','Hangzhou','Zhejiang','310000','China','13700000000','Chinese','Fish',1,1,'20251113','2026-03-25 20:18:26','2026-03-25 20:18:26',0);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `bannerdata`
-- ===========================
DROP TABLE IF EXISTS `bannerdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bannerdata` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `favcategory` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `bannername` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favcategory` (`favcategory`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `bannerdata` WRITE;
/*!40000 ALTER TABLE `bannerdata` DISABLE KEYS */;
INSERT INTO `bannerdata` VALUES (1,'BIRDS','<image src=\"../images/banner_birds.gif\">','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(2,'CATS','<image src=\"../images/banner_cats.gif\">','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(3,'DOGS','<image src=\"../images/banner_dogs.gif\">','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(4,'FISH','<image src=\"../images/banner_fish.gif\">','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(5,'REPTILES','<image src=\"../images/banner_reptiles.gif\">','2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `bannerdata` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `cart`
-- ===========================
DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `user_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_id` (`cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,1,'j2ee','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(2,2,'mike','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(3,3,'jys','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(4,4,'admin','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(5,5,'UserMike','2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `cart_item`
-- ===========================
DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `item_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `quantity` int NOT NULL,
  `is_in_stock` tinyint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `cart_id` (`cart_id`),
  CONSTRAINT `cart_item_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (3,1,'EST-6',1,1,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(4,1,'EST-13',1,1,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(7,3,'EST-20',1,1,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(11,2,'EST-12',1,1,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(12,2,'EST-18',1,1,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(13,4,'EST-6',2,1,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(15,4,'EST-16',11,1,'2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `category`
-- ===========================
DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `catid` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `name` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `descn` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_catid` (`catid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'BIRDS','Birds','<image src=\"../images/birds_icon.gif\"><font size=\"5\" color=\"blue\"> Birds</font>','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(2,'CATS','Cats','<image src=\"../images/cats_icon.gif\"><font size=\"5\" color=\"blue\"> Cats</font>','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(3,'DOGS','Dogs','<image src=\"../images/dogs_icon.gif\"><font size=\"5\" color=\"blue\"> Dogs</font>','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(4,'FISH','Fish','<image src=\"../images/fish_icon.gif\"><font size=\"5\" color=\"blue\"> Fish</font>','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(5,'REPTILES','Reptiles','<image src=\"../images/reptiles_icon.gif\"><font size=\"5\" color=\"blue\"> Reptiles</font>','2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `compare`
-- ===========================
DROP TABLE IF EXISTS `compare`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compare` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(80) NOT NULL,
  `productid` varchar(10) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_compare_user_product` (`userid`,`productid`),
  KEY `idx_compare_userid` (`userid`),
  KEY `idx_compare_productid` (`productid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

-- ===========================
-- Table structure for `favorite`
-- ===========================
DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(80) NOT NULL,
  `productid` varchar(10) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorite_user_product` (`userid`,`productid`),
  KEY `idx_favorite_userid` (`userid`),
  KEY `idx_favorite_productid` (`productid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

-- ===========================
-- Table structure for `inventory`
-- ===========================
DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `itemid` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `qty` int NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_itemid` (`itemid`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,'EST-1',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(2,'EST-10',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(3,'EST-11',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(4,'EST-12',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(5,'EST-13',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(6,'EST-14',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(7,'EST-15',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(8,'EST-16',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(9,'EST-17',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(10,'EST-18',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(11,'EST-19',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(12,'EST-2',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(13,'EST-20',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(14,'EST-21',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(15,'EST-22',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(16,'EST-23',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(17,'EST-24',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(18,'EST-25',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(19,'EST-26',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(20,'EST-27',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(21,'EST-28',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(22,'EST-3',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(23,'EST-4',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(24,'EST-5',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(25,'EST-6',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(26,'EST-7',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(27,'EST-8',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(28,'EST-9',10000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `item`
-- ===========================
DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `itemid` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `productid` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `listprice` decimal(10,2) DEFAULT NULL,
  `unitcost` decimal(10,2) DEFAULT NULL,
  `supplier` int DEFAULT NULL,
  `status` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `attr1` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `attr2` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `attr3` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `attr4` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `attr5` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_itemid` (`itemid`),
  KEY `fk_item_2` (`supplier`),
  KEY `itemProd` (`productid`),
  CONSTRAINT `fk_item_1` FOREIGN KEY (`productid`) REFERENCES `product` (`productid`),
  CONSTRAINT `fk_item_2` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`suppid`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'EST-1','FI-SW-01',16.50,10.00,1,'P','Large',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(2,'EST-10','K9-DL-01',18.50,12.00,1,'P','Spotted Adult Female',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(3,'EST-11','RP-SN-01',18.50,12.00,1,'P','Venomless',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(4,'EST-12','RP-SN-01',18.50,12.00,1,'P','Rattleless',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(5,'EST-13','RP-LI-02',18.50,12.00,1,'P','Green Adult',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(6,'EST-14','FL-DSH-01',58.50,12.00,1,'P','Tailless',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(7,'EST-15','FL-DSH-01',23.50,12.00,1,'P','With tail',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(8,'EST-16','FL-DLH-02',93.50,12.00,1,'P','Adult Female',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(9,'EST-17','FL-DLH-02',93.50,12.00,1,'P','Adult Male',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(10,'EST-18','AV-CB-01',193.50,92.00,1,'P','Adult Male',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(11,'EST-19','AV-SB-02',15.50,2.00,1,'P','Adult Male',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(12,'EST-2','FI-SW-01',16.50,10.00,1,'P','Small',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(13,'EST-20','FI-FW-02',5.50,2.00,1,'P','Adult Male',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(14,'EST-21','FI-FW-02',5.29,1.00,1,'P','Adult Female',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(15,'EST-22','K9-RT-02',135.50,100.00,1,'P','Adult Male',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(16,'EST-23','K9-RT-02',145.49,100.00,1,'P','Adult Female',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(17,'EST-24','K9-RT-02',255.50,92.00,1,'P','Adult Male',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(18,'EST-25','K9-RT-02',325.29,90.00,1,'P','Adult Female',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(19,'EST-26','K9-CW-01',125.50,92.00,1,'P','Adult Male',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(20,'EST-27','K9-CW-01',155.29,90.00,1,'P','Adult Female',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(21,'EST-28','K9-RT-01',155.29,90.00,1,'P','Adult Female',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(22,'EST-3','FI-SW-02',18.50,12.00,1,'P','Toothless',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(23,'EST-4','FI-FW-01',18.50,12.00,1,'P','Spotted',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(24,'EST-5','FI-FW-01',18.50,12.00,1,'P','Spotless',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(25,'EST-6','K9-BD-01',18.50,12.00,1,'P','Male Adult',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(26,'EST-7','K9-BD-01',18.50,12.00,1,'P','Female Puppy',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(27,'EST-8','K9-PO-02',18.50,12.00,1,'P','Male Puppy',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0),(28,'EST-9','K9-DL-01',18.50,12.00,1,'P','Spotless Male Puppy',NULL,NULL,NULL,NULL,'2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `lineitem`
-- ===========================
DROP TABLE IF EXISTS `lineitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lineitem` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orderid` int NOT NULL,
  `linenum` int NOT NULL,
  `itemid` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `quantity` int NOT NULL,
  `unitprice` decimal(10,2) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orderid_linenum` (`orderid`,`linenum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `lineitem` WRITE;
/*!40000 ALTER TABLE `lineitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `lineitem` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `orders`
-- ===========================
DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orderid` int NOT NULL,
  `userid` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `orderdate` date NOT NULL,
  `shipaddr1` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `shipaddr2` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `shipcity` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `shipstate` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `shipzip` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `shipcountry` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `billaddr1` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `billaddr2` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `billcity` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `billstate` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `billzip` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `billcountry` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `courier` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `totalprice` decimal(10,2) NOT NULL,
  `billtofirstname` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `billtolastname` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `shiptofirstname` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `shiptolastname` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `creditcard` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `exprdate` varchar(7) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `cardtype` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `locale` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orderid` (`orderid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `orderstatus`
-- ===========================
DROP TABLE IF EXISTS `orderstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderstatus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orderid` int NOT NULL,
  `linenum` int NOT NULL,
  `timestamp` date NOT NULL,
  `status` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orderid_linenum` (`orderid`,`linenum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `orderstatus` WRITE;
/*!40000 ALTER TABLE `orderstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `orderstatus` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `product`
-- ===========================
DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `productid` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `category` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `name` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `descn` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_productid` (`productid`),
  KEY `productCat` (`category`),
  KEY `productName` (`name`),
  CONSTRAINT `fk_product_1` FOREIGN KEY (`category`) REFERENCES `category` (`catid`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'AV-CB-01','BIRDS','Amazon Parrot','<image src=\"../images/bird2.gif\">Great companion for up to 75 years','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(2,'AV-SB-02','BIRDS','Finch','<image src=\"../images/bird1.gif\">Great stress reliever','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(3,'FI-FW-01','FISH','Koi','<image src=\"../images/fish3.gif\">Fresh Water fish from Japan','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(4,'FI-FW-02','FISH','Goldfish','<image src=\"../images/fish2.gif\">Fresh Water fish from China','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(5,'FI-SW-01','FISH','Angelfish','<image src=\"../images/fish1.gif\">Salt Water fish from Australia','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(6,'FI-SW-02','FISH','Tiger Shark','<image src=\"../images/fish4.gif\">Salt Water fish from Australia','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(7,'FL-DLH-02','CATS','Persian','<image src=\"../images/cat1.gif\">Friendly house cat, doubles as a princess','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(8,'FL-DSH-01','CATS','Manx','<image src=\"../images/cat2.gif\">Great for reducing mouse populations','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(9,'K9-BD-01','DOGS','Bulldog','<image src=\"../images/dog2.gif\">Friendly dog from England','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(10,'K9-CW-01','DOGS','Chihuahua','<image src=\"../images/dog4.gif\">Great companion dog','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(11,'K9-DL-01','DOGS','Dalmation','<image src=\"../images/dog5.gif\">Great dog for a Fire Station','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(12,'K9-PO-02','DOGS','Poodle','<image src=\"../images/dog6.gif\">Cute dog from France','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(13,'K9-RT-01','DOGS','Golden Retriever','<image src=\"../images/dog1.gif\">Great family dog','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(14,'K9-RT-02','DOGS','Labrador Retriever','<image src=\"../images/dog5.gif\">Great hunting dog','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(15,'RP-LI-02','REPTILES','Iguana','<image src=\"../images/lizard1.gif\">Friendly green friend','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(16,'RP-SN-01','REPTILES','Rattlesnake','<image src=\"../images/snake1.gif\">Doubles as a watch dog','2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `signon`
-- ===========================
DROP TABLE IF EXISTS `signon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `signon` (
  `username` varchar(25) NOT NULL,
  `password` varchar(25) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `signon` WRITE;
/*!40000 ALTER TABLE `signon` DISABLE KEYS */;
INSERT INTO `signon` VALUES ('a', 'a');
INSERT INTO `signon` VALUES ('ACID', 'ACID');
INSERT INTO `signon` VALUES ('admin', '123456');
INSERT INTO `signon` VALUES ('j2ee', 'j2ee');
INSERT INTO `signon` VALUES ('jys', '123456');
INSERT INTO `signon` VALUES ('mike', 'Mike@120515');
INSERT INTO `signon` VALUES ('UserMike', 'mike1234');
INSERT INTO `signon` VALUES ('web', '20251113');
INSERT INTO `signon` VALUES ('MikeShi', 'Mike@120515');
/*!40000 ALTER TABLE `signon` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `sequence`
-- ===========================
DROP TABLE IF EXISTS `sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sequence` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `nextid` int NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `sequence` WRITE;
/*!40000 ALTER TABLE `sequence` DISABLE KEYS */;
INSERT INTO `sequence` VALUES (1,'ordernum',1000,'2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `sequence` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Table structure for `supplier`
-- ===========================
DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `suppid` int NOT NULL,
  `name` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `status` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `addr1` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `addr2` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `city` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `state` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `zip` varchar(5) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `phone` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_suppid` (`suppid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` VALUES (1,1,'XYZ Pets','AC','600 Avon Way','','Los Angeles','CA','94024','212-947-0797','2026-03-25 12:11:45','2026-03-25 12:11:45',0),(2,2,'ABC Pets','AC','700 Abalone Way','','San Francisco ','CA','94024','415-947-0797','2026-03-25 12:11:45','2026-03-25 12:11:45',0);
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;

-- ===========================
-- Restore Session Settings
-- ===========================
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- ===========================
-- Summary of Tables
-- ===========================
-- Total Tables: 16
-- 1. account         - 用户账户信息
-- 2. bannerdata      - 横幅数据
-- 3. cart            - 购物车
-- 4. cart_item       - 购物车项目
-- 5. category        - 商品分类
-- 6. compare         - 商品对比
-- 7. favorite        - 收藏列表
-- 8. inventory       - 库存
-- 9. item            - 商品项
-- 10. lineitem       - 订单行项
-- 11. orders         - 订单
-- 12. orderstatus    - 订单状态
-- 13. product        - 商品
-- 14. sequence       - 序列
-- 15. signon         - 登录认证信息（用户名密码）
-- 16. supplier       - 供应商

