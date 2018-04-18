-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: team-47-dev-db.cllrg7hgpqkh.us-east-2.rds.amazonaws.com    Database: cs4500_spring2018_team47_dev
-- ------------------------------------------------------
-- Server version	5.6.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Movie`
--

DROP TABLE IF EXISTS `Movie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Movie` (
  `movie_id` varchar(20) NOT NULL,
  `movie_name` varchar(200) NOT NULL,
  `runtime` varchar(20) NOT NULL,
  `released_date` varchar(20) NOT NULL,
  `genre` varchar(100) NOT NULL,
  `director` varchar(500) NOT NULL,
  `actor` varchar(500) NOT NULL,
  `plot` varchar(1000) NOT NULL,
  `movie_language` varchar(200) NOT NULL,
  `country` varchar(100) NOT NULL,
  `poster` varchar(500) NOT NULL,
  `imdbRating` varchar(3) NOT NULL,
  `movieDBid` varchar(45) NOT NULL,
  PRIMARY KEY (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Movie`
--

LOCK TABLES `Movie` WRITE;
/*!40000 ALTER TABLE `Movie` DISABLE KEYS */;
/*!40000 ALTER TABLE `Movie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Movielist`
--

DROP TABLE IF EXISTS `Movielist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Movielist` (
  `user_id` int(11) NOT NULL,
  `list_name` varchar(30) NOT NULL,
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`user_id`,`list_name`),
  CONSTRAINT `owner_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Movielist`
--

LOCK TABLES `Movielist` WRITE;
/*!40000 ALTER TABLE `Movielist` DISABLE KEYS */;
/*!40000 ALTER TABLE `Movielist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Prod`
--

DROP TABLE IF EXISTS `Prod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Prod` (
  `senderId` int(11) NOT NULL,
  `sender_name` varchar(255) NOT NULL,
  `receiverId` int(11) NOT NULL,
  `receiver_name` varchar(255) NOT NULL,
  `movieId` varchar(20) NOT NULL,
  `movieName` varchar(200) NOT NULL,
  `sent_date` datetime NOT NULL,
  `senderComment` varchar(255) NOT NULL,
  `movieDBId` varchar(45) NOT NULL,
  `moviePoster` varchar(500) NOT NULL,
  `deletedBySender` tinyint(4) NOT NULL,
  `deletedByReceiver` tinyint(4) NOT NULL,
  PRIMARY KEY (`senderId`,`receiverId`,`movieId`,`deletedBySender`,`deletedByReceiver`),
  KEY `receiver_id_constraint` (`receiverId`),
  KEY `movie_id_constraint` (`movieId`),
  CONSTRAINT `movie_id_constraint` FOREIGN KEY (`movieId`) REFERENCES `Movie` (`movie_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `receiver_id_constraint` FOREIGN KEY (`receiverId`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Prod`
--

LOCK TABLES `Prod` WRITE;
/*!40000 ALTER TABLE `Prod` DISABLE KEYS */;
/*!40000 ALTER TABLE `Prod` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Review`
--

DROP TABLE IF EXISTS `Review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Review` (
  `review_id` int(11) NOT NULL AUTO_INCREMENT,
  `movie_id` varchar(20) NOT NULL,
  `reviewer_id` int(11) NOT NULL,
  `reviewer_name` varchar(255) NOT NULL,
  `review_date` datetime NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`review_id`),
  KEY `reviewer_reference` (`reviewer_id`),
  KEY `movie_reference` (`movie_id`),
  CONSTRAINT `movie_reference` FOREIGN KEY (`movie_id`) REFERENCES `Movie` (`movie_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reviewer_reference` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=541 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Review`
--

LOCK TABLES `Review` WRITE;
/*!40000 ALTER TABLE `Review` DISABLE KEYS */;
/*!40000 ALTER TABLE `Review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserMovieList`
--

DROP TABLE IF EXISTS `UserMovieList`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserMovieList` (
  `user_id` int(11) NOT NULL,
  `list_name` varchar(30) NOT NULL,
  `movie_id` varchar(20) NOT NULL,
  `movie_name` varchar(200) NOT NULL,
  `add_date` datetime NOT NULL,
  PRIMARY KEY (`user_id`,`list_name`,`movie_id`),
  KEY `to_moive` (`movie_id`),
  CONSTRAINT `to_moive` FOREIGN KEY (`movie_id`) REFERENCES `Movie` (`movie_id`),
  CONSTRAINT `to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserMovieList`
--

LOCK TABLES `UserMovieList` WRITE;
/*!40000 ALTER TABLE `UserMovieList` DISABLE KEYS */;
/*!40000 ALTER TABLE `UserMovieList` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rating`
--

DROP TABLE IF EXISTS `rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rating` (
  `rating_id` int(11) NOT NULL AUTO_INCREMENT,
  `movie_id` varchar(20) NOT NULL,
  `rating` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `review_date` date NOT NULL,
  PRIMARY KEY (`rating_id`),
  KEY `to_movie` (`movie_id`),
  CONSTRAINT `to_movie` FOREIGN KEY (`movie_id`) REFERENCES `Movie` (`movie_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rating`
--

LOCK TABLES `rating` WRITE;
/*!40000 ALTER TABLE `rating` DISABLE KEYS */;
/*!40000 ALTER TABLE `rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'USER'),(2,'ADMIN');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `active` int(11) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` int(11) DEFAULT '1',
  PRIMARY KEY (`user_id`),
  KEY `user_role_idx` (`role`),
  CONSTRAINT `user_role` FOREIGN KEY (`role`) REFERENCES `role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=983746 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (983737,1,'jjadmin@gmail.com','jjAdim','Jones','Jimmy','$2a$10$7hbcJ2z1ezrMk2Xy./EGcuiKNHlTGEHkTMJe6LcGXJl9n9JBHhBM.',2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userRelation`
--

DROP TABLE IF EXISTS `userRelation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userRelation` (
  `senderId` int(11) NOT NULL,
  `receiverId` int(11) NOT NULL,
  `relationStatus` enum('friend','onHold','senderBlocked','receiverBlocked') DEFAULT NULL,
  `isSenderBlocked` tinyint(1) DEFAULT NULL,
  `isReceiverBlocked` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`senderId`,`receiverId`),
  KEY `receiverId` (`receiverId`),
  CONSTRAINT `receiverId` FOREIGN KEY (`receiverId`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userRelation`
--

LOCK TABLES `userRelation` WRITE;
/*!40000 ALTER TABLE `userRelation` DISABLE KEYS */;
/*!40000 ALTER TABLE `userRelation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'cs4500_spring2018_team47_dev'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-04-18 13:10:07
