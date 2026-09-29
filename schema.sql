-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: fitlog
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `fitlog`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `fitlog` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `fitlog`;

--
-- Table structure for table `goals`
--

DROP TABLE IF EXISTS `goals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `target_calories_burn` int DEFAULT NULL,
  `target_workout_count` int DEFAULT NULL,
  `week_start_date` date DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `target_calories` int DEFAULT NULL,
  `goal_type` varchar(30) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `target_date` date DEFAULT NULL,
  `target_value` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_goal_user_week` (`user_id`,`week_start_date`),
  CONSTRAINT `FKb1mp6ulyqkpcw6bc1a2mr7v1g` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goals`
--

LOCK TABLES `goals` WRITE;
/*!40000 ALTER TABLE `goals` DISABLE KEYS */;
INSERT INTO `goals` VALUES (1,_binary '',1800,4,'2026-09-28',1,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `goals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meals`
--

DROP TABLE IF EXISTS `meals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `calories_consumed` int DEFAULT NULL,
  `date` date NOT NULL,
  `food_item` varchar(150) NOT NULL,
  `meal_type` varchar(30) DEFAULT NULL,
  `quantity` varchar(50) NOT NULL,
  `user_id` bigint NOT NULL,
  `calories` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_meal_user_date` (`user_id`,`date`),
  CONSTRAINT `FK677c66qpjr7234luomahc1ale` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meals`
--

LOCK TABLES `meals` WRITE;
/*!40000 ALTER TABLE `meals` DISABLE KEYS */;
INSERT INTO `meals` VALUES (8,NULL,'2026-09-29','Apple & Raw Almonds',NULL,'1 apple + 15 nuts',1,190),(9,NULL,'2026-09-29','Whey Protein Shake',NULL,'1 scoop (350ml)',1,250),(10,NULL,'2026-09-29','Grilled Chicken Salad',NULL,'1 plate (350g)',1,420);
/*!40000 ALTER TABLE `meals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(120) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `birth_date` date DEFAULT NULL,
  `current_weight` double DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-09-28 07:41:11.155932','alex.runner@example.com','Ashwin','','alex_runner','2007-08-12',65,NULL),(2,'2026-09-28 07:41:11.284999','sarah.fitness@example.com','Sarah Connor','','sarah_fit','2001-08-22',58,NULL),(3,'2026-09-28 07:58:50.226101','babuandhiyur@gmail.com','babu','ab056c5e671a9028869bb29b0f44dcb06671213760620b8d49f7dad36394aa2e','babu','1995-03-10',70,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workouts`
--

DROP TABLE IF EXISTS `workouts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workouts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `calories_burned` int DEFAULT NULL,
  `date` date NOT NULL,
  `duration_minutes` int DEFAULT NULL,
  `notes` text,
  `workout_type` varchar(50) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `calories_burnt` int NOT NULL,
  `duration` int NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_workout_user_date` (`user_id`,`date`),
  CONSTRAINT `FKpf8ql3wbw2drijbk1ugfvki3d` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workouts`
--

LOCK TABLES `workouts` WRITE;
/*!40000 ALTER TABLE `workouts` DISABLE KEYS */;
INSERT INTO `workouts` VALUES (4,380,'2026-09-29',40,'Treadmill intervals and incline walking','Morning Cardio',1,380,40,'Morning Cardio'),(7,NULL,'2026-09-29',NULL,'Brisk evening walk',NULL,1,150,30,'Cardio - Brisk Walk'),(8,NULL,'2026-09-29',NULL,'Steady pace 5km jog',NULL,1,300,30,'Cardio - Outdoor Run');
/*!40000 ALTER TABLE `workouts` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-29 10:59:35
