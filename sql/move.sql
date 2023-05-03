/*
Navicat MySQL Data Transfer

Source Server         : 192.168.112.128
Source Server Version : 50740
Source Host           : 192.168.112.128:3306
Source Database       : move

Target Server Type    : MYSQL
Target Server Version : 50740
File Encoding         : 65001

Date: 2023-05-02 17:16:19
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='AT transaction mode undo table';

-- ----------------------------
-- Records of undo_log
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nick_name` varchar(255) NOT NULL COMMENT '昵称',
  `age` int(11) NOT NULL COMMENT '年龄',
  `sex` varchar(255) NOT NULL COMMENT '性别',
  `address` varchar(255) NOT NULL COMMENT '地址',
  `email` varchar(255) NOT NULL COMMENT '邮箱',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态，0为管理员，1为普通用户',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'w', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('2', '1', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-03-15 21:37:53');
INSERT INTO `user` VALUES ('3', '2', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('4', '3', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('5', '4', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('6', '5', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('7', '7', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('8', '8', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('9', '9', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('10', '10', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('11', '11', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('12', '12', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('13', '13', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('14', '14', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('15', '15', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('16', '16', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-03-15 21:37:53');
INSERT INTO `user` VALUES ('17', '17', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('18', '18', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('19', '19', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('20', '20', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('21', '21', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('22', '22', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('23', '23', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('24', '827626824', '123456', '王世豪', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-07 22:38:40', '2023-01-07 22:38:40');
INSERT INTO `user` VALUES ('25', '1249729083', '123456', '时已入', '23', '女', '太原理工大学', '13776685330@163.com', '1', '2023-01-09 03:35:56', '2023-01-09 03:35:56');
INSERT INTO `user` VALUES ('27', 'username', 'password', '王世豪', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-10 01:55:48', '2023-01-10 01:55:48');
INSERT INTO `user` VALUES ('28', 'wangshihao', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('29', 'wangshihao1', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('30', 'wangshiha', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('31', '31', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('32', '32', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('33', '33', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('34', '34', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('35', '35', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('94', '94', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('95', '95', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('96', '96', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('97', '97', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('98', '98', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('99', '99', '123456', '小王', '23', '男', '南京大学仙林校区', '827626824@qq.com', '0', '2023-01-11 07:30:53', '2023-01-11 07:30:53');
INSERT INTO `user` VALUES ('100', '123456789', '123456789', '测试', '18', '男', '南京大学', '827626824@qq.com', '1', '2023-03-27 11:58:24', '2023-03-27 11:58:24');
INSERT INTO `user` VALUES ('101', 'wang', '123456', 'wang', '1', '男', '1', '827626824@qq.com', '0', '2023-03-31 11:40:17', '2023-03-31 11:40:17');
