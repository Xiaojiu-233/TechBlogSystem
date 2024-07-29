-- MySQL dump 10.13  Distrib 8.0.15, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: xiaojiu_blog
-- ------------------------------------------------------
-- Server version	8.0.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `blog`
--

DROP TABLE IF EXISTS `blog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `blog` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `text` varchar(1500) NOT NULL,
  `images` varchar(100) DEFAULT NULL,
  `likes_id` bigint(20) NOT NULL,
  `user_name` varchar(20) NOT NULL,
  `share` int(10) unsigned NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_blog_like` (`likes_id`) USING BTREE,
  KEY `fk_blog_user` (`user_id`),
  CONSTRAINT `fk_blog_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog`
--

LOCK TABLES `blog` WRITE;
/*!40000 ALTER TABLE `blog` DISABLE KEYS */;
INSERT INTO `blog` VALUES (1817118685175517186,2,'你好','/blog/common/download/?name=Blog/5461c4a5-30be-43b0-9408-824fe30ad0d0.png',1817118685242626049,'AnonTokyo',1,'2024-07-27 16:43:50','海豹的游记'),(1817118980353855489,2,'你好阿\n我的朋友','/blog/common/download/?name=Blog/83bc500b-93c3-47ec-bdf3-df8e34d76dfa.png',1817118980416770049,'AnonTokyo',2,'2024-07-27 16:45:00','test');
/*!40000 ALTER TABLE `blog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `coll_list`
--

DROP TABLE IF EXISTS `coll_list`;
/*!50001 DROP VIEW IF EXISTS `coll_list`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `coll_list` AS SELECT 
 1 AS `blog_id`,
 1 AS `coll_num`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `blog_id` bigint(20) NOT NULL,
  `likes_id` bigint(20) NOT NULL,
  `user_name` varchar(20) NOT NULL,
  `text` varchar(500) NOT NULL,
  `create_time` datetime NOT NULL,
  `share` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_com_like` (`likes_id`) USING BTREE,
  KEY `fk_com_user` (`user_id`),
  KEY `fk_com_blog` (`blog_id`),
  CONSTRAINT `fk_com_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`),
  CONSTRAINT `fk_com_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emp`
--

DROP TABLE IF EXISTS `emp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `emp` (
  `id` bigint(20) NOT NULL,
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emp`
--

LOCK TABLES `emp` WRITE;
/*!40000 ALTER TABLE `emp` DISABLE KEYS */;
INSERT INTO `emp` VALUES (1,'root','ISMvKXpXpadDiUoOSoAfww==','2024-06-27 15:36:11','2024-07-28 10:04:12');
/*!40000 ALTER TABLE `emp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `likes` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `states` int(11) NOT NULL,
  PRIMARY KEY (`id`,`user_id`) USING BTREE,
  KEY `fk_like_user` (`user_id`),
  KEY `id` (`id`),
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (1817118685242626049,1,1),(1817118685242626049,2,1),(1817118980416770049,1,1),(1817118980416770049,2,1);
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `likes_list`
--

DROP TABLE IF EXISTS `likes_list`;
/*!50001 DROP VIEW IF EXISTS `likes_list`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `likes_list` AS SELECT 
 1 AS `likes_id`,
 1 AS `likes_num`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `likes_target`
--

DROP TABLE IF EXISTS `likes_target`;
/*!50001 DROP VIEW IF EXISTS `likes_target`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `likes_target` AS SELECT 
 1 AS `likes_id`,
 1 AS `blog_id`,
 1 AS `comment_id`,
 1 AS `id`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `mail`
--

DROP TABLE IF EXISTS `mail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `mail` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `from_id` bigint(20) DEFAULT NULL,
  `from_name` varchar(20) NOT NULL,
  `text` varchar(500) NOT NULL,
  `is_read` int(5) unsigned NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_mail_from_idx` (`from_id`),
  KEY `fk+mail_user_idx` (`user_id`),
  CONSTRAINT `fk+mail_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_mail_from` FOREIGN KEY (`from_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mail`
--

LOCK TABLES `mail` WRITE;
/*!40000 ALTER TABLE `mail` DISABLE KEYS */;
INSERT INTO `mail` VALUES (1809900542803914754,1,NULL,'管理员','您好，很抱歉地通知您：您的博客【6】由于不遵守社区规范，已被删除。',0,'2024-07-07 18:41:30','博客删除通知'),(1816456170020139010,1,NULL,'管理员','您好，您对博客（id为1）的举报已成功受理，感谢您为美化社区环境做出的贡献！',1,'2024-07-25 20:51:14','举报受理通知'),(1817198493221838850,2,NULL,'管理员','您好，您对博客（id为1817118980353855489）的举报经过核实，发现不违反社区规定，已被驳回。',1,'2024-07-27 22:00:57','举报受理通知'),(1817198507247591426,2,NULL,'管理员','您好，您对评论（id为1）的举报经过核实，发现不违反社区规定，已被驳回。',1,'2024-07-27 22:01:01','举报受理通知'),(1817198515682336770,2,NULL,'管理员','您好，您对博客（id为1）的举报经过核实，发现不违反社区规定，已被驳回。',1,'2024-07-27 22:01:03','举报受理通知'),(1817202536560254978,NULL,NULL,'管理员','试试吧',1,'2024-07-27 22:17:01','应该能换行吧卧槽'),(1817467442547167233,1,2,'AnonTokyo','你好',1,'2024-07-28 15:49:40','黑');
/*!40000 ALTER TABLE `mail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice_check`
--

DROP TABLE IF EXISTS `notice_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `notice_check` (
  `user_id` bigint(20) NOT NULL,
  `check_mail` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`check_mail`),
  KEY `fk_notice_mail` (`check_mail`),
  CONSTRAINT `fk_notice_mail` FOREIGN KEY (`check_mail`) REFERENCES `mail` (`id`),
  CONSTRAINT `fk_notice_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice_check`
--

LOCK TABLES `notice_check` WRITE;
/*!40000 ALTER TABLE `notice_check` DISABLE KEYS */;
INSERT INTO `notice_check` VALUES (1,1817202536560254978),(2,1817202536560254978),(1816993204660887554,1817202536560254978);
/*!40000 ALTER TABLE `notice_check` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `report` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `text` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  `target` enum('用户','博客','评论') NOT NULL,
  `target_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_rep_user` (`user_id`),
  CONSTRAINT `fk_rep_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `uncheck_notice`
--

DROP TABLE IF EXISTS `uncheck_notice`;
/*!50001 DROP VIEW IF EXISTS `uncheck_notice`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `uncheck_notice` AS SELECT 
 1 AS `user_id`,
 1 AS `num`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `head_img` varchar(100) DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sex` enum('男','女') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '男',
  `age` int(11) DEFAULT NULL,
  `sign` varchar(100) DEFAULT NULL,
  `register_time` datetime NOT NULL,
  `login_time` datetime DEFAULT NULL,
  `is_lock` bigint(20) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'xiaojiu','JdVa0oOqQAr0ZMdtcTwHrQ==','1','1','1','男',1,'1','2024-06-03 16:48:30','2024-07-27 17:50:13',0),(2,'anontokyo','JfnnlDI7RTiF9RgfG2JNCw==','AnonTokyo','/blog/common/download/?name=User/986b11cf-b6aa-4cfe-bd01-80a5238400d9.png','1','女',16,'anontokyo','2024-06-03 16:48:30','2024-07-28 09:50:41',0),(1816993204660887554,'xiaojiu233','JdVa0oOqQAr0ZMdtcTwHrQ==','小究','','13677349041','男',NULL,NULL,'2024-07-27 08:25:13','2024-07-28 14:26:21',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_coll`
--

DROP TABLE IF EXISTS `user_coll`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_coll` (
  `user_id` bigint(20) NOT NULL,
  `blog_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`blog_id`),
  KEY `fk_coll_blog` (`blog_id`),
  CONSTRAINT `fk_coll_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`),
  CONSTRAINT `fk_coll_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_coll`
--

LOCK TABLES `user_coll` WRITE;
/*!40000 ALTER TABLE `user_coll` DISABLE KEYS */;
INSERT INTO `user_coll` VALUES (2,1817118980353855489);
/*!40000 ALTER TABLE `user_coll` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `user_fans`
--

DROP TABLE IF EXISTS `user_fans`;
/*!50001 DROP VIEW IF EXISTS `user_fans`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `user_fans` AS SELECT 
 1 AS `user_id`,
 1 AS `fans`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user_sub`
--

DROP TABLE IF EXISTS `user_sub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_sub` (
  `user_id` bigint(20) NOT NULL,
  `sub_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`sub_id`) USING BTREE,
  KEY `fk_sub_fuser` (`sub_id`),
  CONSTRAINT `fk_sub_fuser` FOREIGN KEY (`sub_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_sub_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_sub`
--

LOCK TABLES `user_sub` WRITE;
/*!40000 ALTER TABLE `user_sub` DISABLE KEYS */;
INSERT INTO `user_sub` VALUES (1,2);
/*!40000 ALTER TABLE `user_sub` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `user_subs`
--

DROP TABLE IF EXISTS `user_subs`;
/*!50001 DROP VIEW IF EXISTS `user_subs`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `user_subs` AS SELECT 
 1 AS `user_id`,
 1 AS `subs`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `coll_list`
--

/*!50001 DROP VIEW IF EXISTS `coll_list`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `coll_list` AS select `user_coll`.`blog_id` AS `blog_id`,count(0) AS `coll_num` from `user_coll` group by `user_coll`.`blog_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `likes_list`
--

/*!50001 DROP VIEW IF EXISTS `likes_list`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `likes_list` AS select `li`.`id` AS `likes_id`,(count(0) - (select count(0) from `likes` where ((`likes`.`id` = `li`.`id`) and (`likes`.`states` = 0)))) AS `likes_num` from `likes` `li` where `li`.`id` in (select `blog`.`likes_id` AS `likes_id` from `blog` union select `comment`.`likes_id` AS `likes_id` from `comment`) group by `likes_id` union select `blog`.`likes_id` AS `likes_id`,0 AS `likes_num` from `blog` where (not(`blog`.`likes_id` in (select `likes`.`id` from `likes`))) union select `comment`.`likes_id` AS `likes_id`,0 AS `likes_num` from `comment` where (not(`comment`.`likes_id` in (select `likes`.`id` from `likes`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `likes_target`
--

/*!50001 DROP VIEW IF EXISTS `likes_target`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `likes_target` AS select `blog`.`likes_id` AS `likes_id`,`blog`.`id` AS `blog_id`,NULL AS `comment_id`,`blog`.`user_id` AS `id` from `blog` union select `comment`.`likes_id` AS `likes_id`,NULL AS `blog_id`,`comment`.`blog_id` AS `comment_id`,`comment`.`user_id` AS `id` from `comment` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `uncheck_notice`
--

/*!50001 DROP VIEW IF EXISTS `uncheck_notice`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `uncheck_notice` AS select `user`.`id` AS `user_id`,((select count(0) from `mail` where (isnull(`mail`.`user_id`) and (`mail`.`create_time` > `user`.`register_time`))) - (select count(0) from `notice_check` where (`notice_check`.`user_id` = `user`.`id`))) AS `num` from `user` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_fans`
--

/*!50001 DROP VIEW IF EXISTS `user_fans`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `user_fans` AS select `user_sub`.`sub_id` AS `user_id`,count(0) AS `fans` from `user_sub` group by `user_sub`.`sub_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `user_subs`
--

/*!50001 DROP VIEW IF EXISTS `user_subs`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `user_subs` AS select `user_sub`.`user_id` AS `user_id`,count(0) AS `subs` from `user_sub` group by `user_sub`.`user_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-28 17:05:21
