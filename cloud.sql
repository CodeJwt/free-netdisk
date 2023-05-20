/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : cloud

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 24/03/2023 12:34:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `activity_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '活动id',
  `activity_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动名称',
  `activity_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动url',
  `activity_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活动描述',
  `activity_memory` bigint(20) NOT NULL DEFAULT 0 COMMENT '活动赠送容量',
  `memory_time` int(11) NOT NULL DEFAULT 7 COMMENT '容量有效时间（天）',
  `start_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '活动开始时间',
  `end_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '活动结束时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '活动创建时间',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '活动状态（0-正常，1-下架，2-结束）',
  PRIMARY KEY (`activity_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_origin
-- ----------------------------
DROP TABLE IF EXISTS `file_origin`;
CREATE TABLE `file_origin`  (
  `origin_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '源文件id',
  `file_identify` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件md5标识（存于minio的对象名）',
  `preview_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件预览url',
  `file_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_ext_name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件扩展名',
  `file_size` int(11) NOT NULL DEFAULT 0 COMMENT '文件大小',
  `file_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '文件类型（0-文本，1-图片，2-视频，3-音乐，4-其它）',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '文件状态（0-正常，1--已清除）',
  `create_user_id` int(11) NOT NULL DEFAULT 0 COMMENT '最初上传的用户id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`origin_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '源文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_recycle
-- ----------------------------
DROP TABLE IF EXISTS `file_recycle`;
CREATE TABLE `file_recycle`  (
  `recycle_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `delete_user_id` int(11) NOT NULL DEFAULT 0 COMMENT '删除文件的用户id',
  `delete_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '删除时间',
  `clear_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '自动清理时间',
  `auto_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '自动删除标识（0-未删除（正常展示），1-已自动清理）',
  PRIMARY KEY (`recycle_id`) USING BTREE,
  INDEX `index_user_time`(`delete_user_id`,`delete_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收站表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_recycle_item
-- ----------------------------
DROP TABLE IF EXISTS `file_recycle_item`;
CREATE TABLE `file_recycle_item`  (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '回收细分项id',
  `recycle_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '回收记录id',
  `file_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '关联文件id（user_file）',
  `origin_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '源文件id',
  `dir_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为文件夹（0-是，1-不是）',
  `file_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_ext_name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件扩展名',
  `file_size` int(11) NOT NULL DEFAULT 0 COMMENT '文件大小',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '文件状态（0-正常，1-已恢复或彻底删除）',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `recycle_id`(`recycle_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收站详细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_user
-- ----------------------------
DROP TABLE IF EXISTS `file_user`;
CREATE TABLE `file_user`  (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户文件id',
  `origin_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '源文件id',
  `file_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_ext_name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件扩展名',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件逻辑路径（页面展示的路径）',
  `file_size` int(11) NOT NULL DEFAULT 0 COMMENT '文件大小',
  `dir_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否为文件夹（0-是，1-不是）',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户id',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父文件夹id(0为顶层目录)',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '文件状态（0-正常，1-在回收站，2-已清除）',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `parent_user_time`(`parent_id`, `user_id`, `update_time`) USING BTREE,
  INDEX `path_user_parent` (`file_path`, `user_id`,`parent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户文件表（页面展示）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_user_origin
-- ----------------------------
DROP TABLE IF EXISTS `file_user_origin`;
CREATE TABLE `file_user_origin`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `file_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户文件id',
  `origin_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '源文件id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `origin_user`(`origin_id`,`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户文件关联源文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(10) NOT NULL auto_increment,
  `username` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名(账号）',
  `password` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `sex` tinyint(1) NOT NULL DEFAULT 0 COMMENT '性别（0-女，1-男）',
  `real_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '真实姓名',
  `reg_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '注册时间',
  `total_memory` bigint(20) NOT NULL DEFAULT 5368709120 COMMENT '网盘总容量',
  `base_memory` bigint(20) NOT NULL DEFAULT 5368709120 COMMENT '基础容量（随等级变化）',
  `activities_memory` bigint(20) NOT NULL DEFAULT 0 COMMENT '参与活动获得容量',
  `pay_memory` bigint(20) NOT NULL DEFAULT 0 COMMENT '付费获得容量',
  `used_memory` bigint(20) NOT NULL DEFAULT 0 COMMENT '网盘已用容量',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `user_status` tinyint(1) NULL DEFAULT 0 COMMENT '用户状态（0-正常，1-冻结，2-封禁）',
  `profile_photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像url',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_join
-- ----------------------------
DROP TABLE IF EXISTS `user_join`;
CREATE TABLE `user_join`  (
  `join_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户参与记录id',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户id',
  `activity_id` int(11) NOT NULL DEFAULT 0 COMMENT '关联活动id',
  `join_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '参与活动时间',
  `activity_memory` bigint(20) NOT NULL DEFAULT 0 COMMENT '活动赠送容量',
  `memory_time` int(11) NOT NULL DEFAULT 7 COMMENT '容量有效时间（天）',
  PRIMARY KEY (`join_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户参与活动记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_level
-- ----------------------------
DROP TABLE IF EXISTS `user_level`;
CREATE TABLE `user_level`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户id',
  `level` tinyint(11) NOT NULL DEFAULT 1 COMMENT '用户等级',
  `upgrade_exp` int(11) NOT NULL DEFAULT 100 COMMENT '升级所需经验值（随等级变化）',
  `current_exp` int(11) NOT NULL DEFAULT 0 COMMENT '现有经验值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户等级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_share
-- ----------------------------
DROP TABLE IF EXISTS `user_share`;
CREATE TABLE `user_share`  (
  `share_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分享记录id',
  `share_identify` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分享唯一标识uuid',
  `share_user_id` int(11) NOT NULL DEFAULT 0 COMMENT '分享文件的用户id',
  `share_pwd` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '提取码（4位）',
  `public_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否公开（公开不需要密码）0-公开，1-不公开',
  `share_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '分享时间',
  `end_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '过期时间',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '分享状态（0-正常，1-已失效）',
  `access_count` int(11) NOT NULL DEFAULT 0 COMMENT '访问次数',
  `download_count` int(11) NOT NULL DEFAULT 0 COMMENT '下载次数',
  `save_count` int(11) NOT NULL DEFAULT 0 COMMENT '保存次数',
  PRIMARY KEY (`share_id`) USING BTREE,
  INDEX `share_user_time`(`share_user_id`,`share_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户分享表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_share_item
-- ----------------------------
DROP TABLE IF EXISTS `user_share_item`;
CREATE TABLE `user_share_item`  (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分享细分项id',
  `share_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '分享记录id',
  `file_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '关联文件id（user_file）',
  `origin_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '源文件id',
  `file_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_ext_name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件扩展名',
  `file_size` int(11) NOT NULL DEFAULT 0 COMMENT '文件大小',
  `dir_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为文件夹（0-是，1-不是）',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `share_id`(`share_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户分享详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_transfer
-- ----------------------------
DROP TABLE IF EXISTS `user_transfer`;
CREATE TABLE `user_transfer`  (
  `transfer_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `file_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名称',
  `file_ext_name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件扩展名',
  `file_size` int(11) NOT NULL DEFAULT 0 COMMENT '文件大小',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '页面展示的路径（对应上传）',
  `file_real_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '本地路径（对应下载）',
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户id',
  `transfer_mode` tinyint(1) NOT NULL DEFAULT 0 COMMENT '传输方式（0-上传，1-下载）',
  `transfer_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '传输时间',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '传输状态（0-传输中，1-传输失败，2-传输完成，3-已删除记录）',
  PRIMARY KEY (`transfer_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户传输表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
