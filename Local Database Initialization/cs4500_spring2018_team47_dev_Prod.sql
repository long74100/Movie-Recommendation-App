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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-04-17 18:50:44
