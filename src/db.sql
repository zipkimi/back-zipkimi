-- --------------------------------------------------------
-- 호스트:                          localhost
-- 서버 버전:                        8.0.34 - MySQL Community Server - GPL
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  10.1.0.5464
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- zipkimi 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `zipkimi` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `zipkimi`;

-- 테이블 zipkimi.admin 구조 내보내기
CREATE TABLE IF NOT EXISTS `admin` (
                                       `admin_id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
                                       `email` varchar(100) DEFAULT NULL,
    `name` varchar(100) DEFAULT NULL,
    `phone_number` varchar(100) DEFAULT NULL,
    `created_user` bigint DEFAULT NULL,
    `created_dt` datetime NOT NULL,
    `updated_user` bigint DEFAULT NULL,
    `updated_dt` datetime DEFAULT NULL

    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 zipkimi.admin:~0 rows (대략적) 내보내기
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;

-- 테이블 zipkimi.builder 구조 내보내기
CREATE TABLE IF NOT EXISTS `builder` (
                                         `builder_id` bigint NOT NULL AUTO_INCREMENT,
                                         `builder_name` varchar(100) NOT NULL,
    `status` varchar(100) NOT NULL,
    `builder_number` varchar(100) DEFAULT NULL,
    `builder_com_number` varchar(100) DEFAULT NULL,
    `ceo_name` varchar(100) DEFAULT NULL,
    `builder_contact_number` varchar(100) DEFAULT NULL,
    `created_user` bigint DEFAULT NULL,
    `created_dt` datetime NOT NULL,
    `updated_user` bigint DEFAULT NULL,
    `updated_dt` datetime DEFAULT NULL,
    PRIMARY KEY (`builder_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 zipkimi.builder:~0 rows (대략적) 내보내기
/*!40000 ALTER TABLE `builder` DISABLE KEYS */;
/*!40000 ALTER TABLE `builder` ENABLE KEYS */;

-- 테이블 zipkimi.builder_approval_history 구조 내보내기
CREATE TABLE IF NOT EXISTS `builder_approval_history` (
                                                          `builder_approval_id` bigint NOT NULL AUTO_INCREMENT,
                                                          `builder_id` bigint NOT NULL,
                                                          `admin_id` bigint NOT NULL,
                                                          `status` varchar(100) DEFAULT NULL,
    `reason` varchar(500) DEFAULT NULL,
    `created_user` bigint DEFAULT NULL,
    `created_dt` datetime DEFAULT NULL,
    `updated_user` bigint DEFAULT NULL,
    `updated_dt` datetime DEFAULT NULL,
    PRIMARY KEY (`builder_approval_id`),
    KEY `FK_builder_TO_builder_approval_history_1` (`builder_id`),
    CONSTRAINT `FK_builder_TO_builder_approval_history_1` FOREIGN KEY (`builder_id`) REFERENCES `builder` (`builder_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 zipkimi.builder_approval_history:~0 rows (대략적) 내보내기
/*!40000 ALTER TABLE `builder_approval_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `builder_approval_history` ENABLE KEYS */;

-- 테이블 zipkimi.refresh_token 구조 내보내기
CREATE TABLE IF NOT EXISTS `refresh_token` (
                                               `refresh_token_id` bigint NOT NULL AUTO_INCREMENT,
                                               `refresh_token` varchar(255) NOT NULL,
    `user_id` bigint NOT NULL,
    `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `created_user` bigint DEFAULT NULL,
    `updated_dt` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `updated_user` bigint DEFAULT NULL,
    PRIMARY KEY (`refresh_token_id`),
    KEY `FK1_user_id` (`user_id`),
    CONSTRAINT `FK1_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 zipkimi.refresh_token:~1 rows (대략적) 내보내기
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
INSERT INTO `refresh_token` (`refresh_token_id`, `refresh_token`, `user_id`, `created_dt`, `created_user`, `updated_dt`, `updated_user`) VALUES
    (13, 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTM4MjQ4MTV9.MMGb2FlCk38Nje_dpmZmVlm3ApxVUpfNQxpy58FKAR8', 1, '2023-08-28 19:53:35', NULL, '2023-08-28 19:53:35', NULL);
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;

-- 테이블 zipkimi.sms_auth 구조 내보내기
CREATE TABLE IF NOT EXISTS `sms_auth` (
                                          `sms_auth_id` bigint NOT NULL AUTO_INCREMENT,
                                          `phone_number` varchar(100) NOT NULL,
    `sms_auth_number` varchar(100) NOT NULL,
    `content` varchar(100) NOT NULL,
    `is_use` tinyint(1) DEFAULT NULL,
    `is_authenticate` tinyint(1) NOT NULL,
    `expiration_time` datetime NOT NULL,
    `sms_auth_type` varchar(100) DEFAULT NULL,
    `created_user` bigint DEFAULT NULL,
    `created_dt` datetime NOT NULL,
    `updated_user` bigint DEFAULT NULL,
    `updated_dt` datetime DEFAULT NULL,
    PRIMARY KEY (`sms_auth_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 zipkimi.sms_auth:~41 rows (대략적) 내보내기
/*!40000 ALTER TABLE `sms_auth` DISABLE KEYS */;
INSERT INTO `sms_auth` (`sms_auth_id`, `phone_number`, `sms_auth_number`, `content`, `is_use`, `is_authenticate`, `expiration_time`, `sms_auth_type`, `created_user`, `created_dt`, `updated_user`, `updated_dt`) VALUES
                                                                                                                                                                                                                      (1, '01094342762', '5689', '[집킴이] 아이디 찾기 인증번호는 [5689] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-07-27 17:03:04', 'findId', NULL, '2023-07-27 16:58:04', NULL, '2023-07-27 16:58:04'),
                                                                                                                                                                                                                      (2, '01094342762', '4130', '[집킴이] 아이디 찾기 인증번호는 [4130] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-07-27 17:27:31', 'findId', NULL, '2023-07-27 17:22:31', NULL, '2023-07-27 17:23:02'),
                                                                                                                                                                                                                      (3, '01094342762', '2481', '[집킴이] 아이디 찾기 인증번호는 [2481] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-07-27 17:32:34', 'findId', NULL, '2023-07-27 17:27:34', NULL, '2023-07-27 17:28:04'),
                                                                                                                                                                                                                      (4, '01094342762', '6510', '[집킴이] 비밀번호 찾기 인증번호는 [6510] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-07-27 17:38:30', 'findPw', NULL, '2023-07-27 17:33:30', NULL, '2023-07-27 17:34:21'),
                                                                                                                                                                                                                      (5, '01094342762', '7249', '[집킴이] 아이디 찾기 인증번호는 [7249] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-07-27 17:49:54', 'findId', NULL, '2023-07-27 17:44:54', NULL, '2023-07-27 17:45:30'),
                                                                                                                                                                                                                      (6, '01094342762', '3487', '[집킴이] 비밀번호 찾기 인증번호는 [3487] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-07-27 17:56:04', 'findPw', NULL, '2023-07-27 17:51:04', NULL, '2023-07-27 17:51:44'),
                                                                                                                                                                                                                      (7, '01094342762', '0627', '[집킴이] 아이디 찾기 인증번호는 [0627] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-01 14:33:12', 'findId', NULL, '2023-08-01 14:26:22', NULL, '2023-08-01 14:28:12'),
                                                                                                                                                                                                                      (8, '01094342762', '2364', '[집킴이] 아이디 찾기 인증번호는 [2364] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-01 14:42:13', 'findId', NULL, '2023-08-01 14:34:41', NULL, '2023-08-01 14:37:13'),
                                                                                                                                                                                                                      (9, '01094342762', '1638', '[집킴이] 비밀번호 찾기 인증번호는 [1638] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-01 14:49:04', 'findPw', NULL, '2023-08-01 14:44:04', NULL, '2023-08-01 14:44:04'),
                                                                                                                                                                                                                      (10, '01094342762', '2346', '[집킴이] 아이디 찾기 인증번호는 [2346] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-01 14:49:43', 'findId', NULL, '2023-08-01 14:44:32', NULL, '2023-08-01 14:44:43'),
                                                                                                                                                                                                                      (11, '01094342762', '1695', '[집킴이] 아이디 찾기 인증번호는 [1695] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-01 14:55:07', 'findId', NULL, '2023-08-01 14:50:07', NULL, '2023-08-01 14:50:07'),
                                                                                                                                                                                                                      (12, '01094342762', '4571', '[집킴이] 비밀번호 찾기 인증번호는 [4571] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-01 14:55:24', 'findPw', NULL, '2023-08-01 14:50:24', NULL, '2023-08-01 14:50:24'),
                                                                                                                                                                                                                      (13, '01094342762', '8531', '[집킴이] 아이디 찾기 인증번호는 [8531] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-19 00:40:15', 'findId', NULL, '2023-08-19 00:35:16', NULL, '2023-08-19 00:40:05'),
                                                                                                                                                                                                                      (14, '01094342762', '5746', '[집킴이] 아이디 찾기 인증번호는 [5746] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-19 00:46:44', 'findId', NULL, '2023-08-19 00:41:44', NULL, '2023-08-19 00:41:44'),
                                                                                                                                                                                                                      (15, '01094342762', '1250', '[집킴이] 비밀번호 찾기 인증번호는 [1250] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-19 00:47:47', 'findPw', NULL, '2023-08-19 00:43:47', NULL, '2023-08-19 00:44:58'),
                                                                                                                                                                                                                      (16, '01094342762', '8765', '[집킴이] 비밀번호 찾기 인증번호는 [8765] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-19 00:52:52', 'findPw', NULL, '2023-08-19 00:47:52', NULL, '2023-08-19 00:52:15'),
                                                                                                                                                                                                                      (17, '01094342762', '0427', '[집킴이] 아이디 찾기 인증번호는 [0427] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-19 00:57:42', 'findId', NULL, '2023-08-19 00:52:42', NULL, '2023-08-19 00:53:05'),
                                                                                                                                                                                                                      (18, '01094342762', '0578', '[집킴이] 아이디 찾기 인증번호는 [0578] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-19 01:09:03', 'findId', NULL, '2023-08-19 01:04:03', NULL, '2023-08-19 01:04:03'),
                                                                                                                                                                                                                      (19, '01094342762', '8216', '[집킴이] 비밀번호 찾기 인증번호는 [8216] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-19 01:09:12', 'findPw', NULL, '2023-08-19 01:04:12', NULL, '2023-08-19 01:04:12'),
                                                                                                                                                                                                                      (20, '01094342762', '9156', '[집킴이] 비밀번호 찾기 인증번호는 [9156] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-19 01:22:34', 'findPw', NULL, '2023-08-19 01:17:34', NULL, '2023-08-19 01:17:34'),
                                                                                                                                                                                                                      (21, '01094342762', '9217', '[집킴이] 아이디 찾기 인증번호는 [9217] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-19 01:31:37', 'findId', NULL, '2023-08-19 01:26:37', NULL, '2023-08-19 01:28:54'),
                                                                                                                                                                                                                      (22, '01094342762', '2890', '[집킴이] 아이디 찾기 인증번호는 [2890] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-20 23:07:01', 'findId', NULL, '2023-08-20 23:02:01', NULL, '2023-08-20 23:02:01'),
                                                                                                                                                                                                                      (23, '01094342762', '8157', '[집킴이] 아이디 찾기 인증번호는 [8157] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-21 15:21:40', 'findId', NULL, '2023-08-21 15:16:40', NULL, '2023-08-21 15:17:27'),
                                                                                                                                                                                                                      (24, '01094342762', '2940', '[집킴이] 비밀번호 찾기 인증번호는 [2940] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-21 15:22:40', 'findPw', NULL, '2023-08-21 15:17:40', NULL, '2023-08-21 15:18:31'),
                                                                                                                                                                                                                      (25, '01094342762', '6792', '[집킴이] 아이디 찾기 인증번호는 [6792] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-22 19:05:01', 'findId', NULL, '2023-08-22 19:00:01', NULL, '2023-08-22 19:00:01'),
                                                                                                                                                                                                                      (26, '01094342762', '6209', '[집킴이] 아이디 찾기 인증번호는 [6209] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-22 22:05:24', 'findId', NULL, '2023-08-22 22:00:24', NULL, '2023-08-22 22:00:24'),
                                                                                                                                                                                                                      (27, '01094342762', '8737', '본인확인 인증번호 (8737)입력시 \n정상처리 됩니다.', NULL, 0, '2023-08-23 02:31:54', NULL, NULL, '2023-08-23 02:26:54', NULL, '2023-08-23 02:26:54'),
                                                                                                                                                                                                                      (28, '01094342762', '2299', '본인확인 인증번호 (2299)입력시 \n정상처리 됩니다.', NULL, 0, '2023-08-23 02:32:50', NULL, NULL, '2023-08-23 02:27:50', NULL, '2023-08-23 02:27:50'),
                                                                                                                                                                                                                      (29, '01094342762', '7212', '본인확인 인증번호 (7212)입력시 \n정상처리 됩니다.', NULL, 0, '2023-08-23 02:39:00', NULL, NULL, '2023-08-23 02:34:00', NULL, '2023-08-23 02:34:00'),
                                                                                                                                                                                                                      (30, '01094342762', '9196', '본인확인 인증번호 (9196)입력시 \n정상처리 됩니다.', NULL, 0, '2023-08-23 02:57:12', NULL, NULL, '2023-08-23 02:52:12', NULL, '2023-08-23 02:52:12'),
                                                                                                                                                                                                                      (31, '01094342762', '7382', '본인확인 인증번호 (7382)입력시 \n정상처리 됩니다.', NULL, 0, '2023-08-23 03:10:37', NULL, NULL, '2023-08-23 03:05:37', NULL, '2023-08-23 03:05:37'),
                                                                                                                                                                                                                      (32, '01094342762', '7547', '본인확인 인증번호 (7547)입력시 \n정상처리 됩니다.', NULL, 0, '2023-08-23 03:10:47', NULL, NULL, '2023-08-23 03:05:47', NULL, '2023-08-23 03:05:47'),
                                                                                                                                                                                                                      (33, '01094342762', '2350', '본인확인 인증번호 (2350)입력시 \n정상처리 됩니다.', NULL, 0, '2023-08-23 03:12:02', NULL, NULL, '2023-08-23 03:07:02', NULL, '2023-08-23 03:07:02'),
                                                                                                                                                                                                                      (34, '01094342762', '5804', '[집킴이] 아이디 찾기 인증번호는 [5804] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-26 23:56:30', 'findId', NULL, '2023-08-26 23:44:32', NULL, '2023-08-26 23:51:30'),
                                                                                                                                                                                                                      (35, '01094342762', '9583', '[집킴이] 아이디 찾기 인증번호는 [9583] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 00:06:06', 'findId', NULL, '2023-08-27 00:00:12', NULL, '2023-08-27 00:01:06'),
                                                                                                                                                                                                                      (38, '01094342762', '2189', '[집킴이] 아이디 찾기 인증번호는 [2189] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 00:15:45', 'findId', NULL, '2023-08-27 00:07:43', NULL, '2023-08-27 00:10:45'),
                                                                                                                                                                                                                      (40, '01094342762', '5906', '[집킴이] 아이디 찾기 인증번호는 [5906] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 00:24:15', 'findId', NULL, '2023-08-27 00:18:59', NULL, '2023-08-27 00:19:15'),
                                                                                                                                                                                                                      (41, '01094342762', '9162', '[집킴이] 아이디 찾기 인증번호는 [9162] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 00:29:52', 'findId', NULL, '2023-08-27 00:24:52', NULL, '2023-08-27 00:24:52'),
                                                                                                                                                                                                                      (42, '01094342762', '3716', '[집킴이] 아이디 찾기 인증번호는 [3716] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 00:35:45', 'findId', NULL, '2023-08-27 00:30:45', NULL, '2023-08-27 00:31:31'),
                                                                                                                                                                                                                      (43, '01094342762', '3801', '[집킴이] 아이디 찾기 인증번호는 [3801] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 00:42:27', 'findId', NULL, '2023-08-27 00:37:27', NULL, '2023-08-27 00:38:02'),
                                                                                                                                                                                                                      (45, '01094342762', '1290', '[집킴이] 비밀번호 찾기 인증번호는 [1290] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-27 00:48:02', 'findPw', NULL, '2023-08-27 00:42:43', NULL, '2023-08-27 00:43:02'),
                                                                                                                                                                                                                      (46, '01094342762', '4597', '[집킴이] 비밀번호 찾기 인증번호는 [4597] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 00:59:27', 'findPw', NULL, '2023-08-27 00:48:53', NULL, '2023-08-27 00:54:27'),
                                                                                                                                                                                                                      (47, '01094342762', '1827', '[집킴이] 비밀번호 찾기 인증번호는 [1827] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 01:58:00', 'findPw', NULL, '2023-08-27 01:56:33', NULL, '2023-08-27 01:59:02'),
                                                                                                                                                                                                                      (48, '01094342762', '7481', '[집킴이] 비밀번호 찾기 인증번호는 [7481] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 02:02:30', 'findPw', NULL, '2023-08-27 01:59:20', NULL, '2023-08-27 02:03:30'),
                                                                                                                                                                                                                      (49, '01094342762', '6142', '[집킴이] 비밀번호 찾기 인증번호는 [6142] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-27 02:08:45', 'findPw', NULL, '2023-08-27 02:03:45', NULL, '2023-08-27 02:04:35'),
                                                                                                                                                                                                                      (50, '01094342762', '3617', '[집킴이] 아이디 찾기 인증번호는 [3617] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-30 19:27:53', 'findId', NULL, '2023-08-30 19:20:38', NULL, '2023-08-30 19:23:28'),
                                                                                                                                                                                                                      (51, '01094342762', '2836', '[집킴이] 비밀번호 찾기 인증번호는 [2836] 입니다. 인증번호를 정확히 입력해주세요.', NULL, 0, '2023-08-30 19:28:36', 'findPw', NULL, '2023-08-30 19:23:36', NULL, '2023-08-30 19:23:36'),
                                                                                                                                                                                                                      (52, '01094342762', '6792', '[집킴이] 아이디 찾기 인증번호는 [6792] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-30 21:05:24', 'findId', NULL, '2023-08-30 21:06:16', NULL, '2023-08-30 21:10:24'),
                                                                                                                                                                                                                      (53, '01094342762', '5781', '[집킴이] 아이디 찾기 인증번호는 [5781] 입니다. 인증번호를 정확히 입력해주세요.', 1, 0, '2023-08-30 21:15:39', 'findId', NULL, '2023-08-30 21:10:39', NULL, '2023-08-30 21:10:55');
/*!40000 ALTER TABLE `sms_auth` ENABLE KEYS */;

-- 테이블 zipkimi.sns_account 구조 내보내기
CREATE TABLE IF NOT EXISTS `sns_account` (
                                             `login_id` bigint NOT NULL AUTO_INCREMENT,
                                             `user_id` bigint NOT NULL,
                                             `social_id` varchar(100) DEFAULT NULL,
    `type` varchar(100) DEFAULT NULL COMMENT 'kakao\r\nnaver',
    `auth_token` varchar(100) DEFAULT NULL,
    `refresh_token` varchar(100) DEFAULT NULL,
    `created_user` bigint DEFAULT NULL,
    `created_dt` datetime NOT NULL,
    `updated_user` bigint DEFAULT NULL,
    `updated_dt` datetime DEFAULT NULL,
    PRIMARY KEY (`login_id`),
    KEY `FK_users_TO_sns_account_1` (`user_id`),
    CONSTRAINT `FK_users_TO_sns_account_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 zipkimi.sns_account:~0 rows (대략적) 내보내기
/*!40000 ALTER TABLE `sns_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `sns_account` ENABLE KEYS */;

-- 테이블 zipkimi.users 구조 내보내기
CREATE TABLE IF NOT EXISTS `users` (
                                       `user_id` bigint NOT NULL AUTO_INCREMENT,
                                       `builder_id` bigint DEFAULT NULL,
                                       `email` varchar(100) NOT NULL,
    `password` varchar(100) NOT NULL COMMENT '암호화',
    `name` varchar(100) DEFAULT NULL,
    `role` varchar(100) DEFAULT NULL,
    `phone_number` varchar(100) DEFAULT NULL,
    `is_use` tinyint(1) DEFAULT NULL,
    `created_user` bigint DEFAULT NULL,
    `created_dt` datetime NOT NULL,
    `updated_user` bigint DEFAULT NULL,
    `updated_dt` datetime DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    KEY `FK1_builder_id` (`builder_id`),
    CONSTRAINT `FK1_builder_id` FOREIGN KEY (`builder_id`) REFERENCES `builder` (`builder_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 zipkimi.users:~2 rows (대략적) 내보내기
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`user_id`, `builder_id`, `email`, `password`, `name`, `role`, `phone_number`, `is_use`, `created_user`, `created_dt`, `updated_user`, `updated_dt`) VALUES
                                                                                                                                                                             (1, NULL, 'test1@example.com', '{bcrypt}$2a$10$lBy.7qGlNh5IFXqPvKZoK.KJnRngsJOguGUuoqOtmosyqPow9aekO', NULL, 'ROLE_USER', '01094342762', 1, NULL, '2023-08-23 03:05:18', NULL, '2023-08-27 02:04:35'),
                                                                                                                                                                             (2, NULL, 'test2@example.com', '{bcrypt}$2a$10$w8MrLYONdEATKliPIdNZoOCdRbihyDbjKfvPp6t5sWiT2Ej0E1/kS', NULL, 'ROLE_USER', '01094342762', 0, NULL, '2023-08-25 13:15:41', NULL, '2023-08-25 13:15:41');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
