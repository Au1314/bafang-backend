-- 测试数据插入脚本

-- 插入普通用户（未认证车辆和充电桩）
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_user', '普通用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138000', 'user@test.com', 0, 0, 0);

-- 插入车辆认证用户
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_car_owner', '车辆认证用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138001', 'car_owner@test.com', 2, 0, 0);

-- 插入充电桩认证用户
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_station_owner', '充电桩认证用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138002', 'station_owner@test.com', 0, 2, 0);

-- 插入同时认证车辆和充电桩的用户
INSERT INTO `user_info` (`username`, `name`, `password`, `type`, `phone`, `email`, `auth_vehicle`, `auth_charging_station`, `is_deleted`) 
VALUES ('test_all_owner', '车辆和充电桩认证用户', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, '13800138003', 'all_owner@test.com', 2, 2, 0);

-- 插入普通管理员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_admin', '普通管理员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 1, 0);

-- 插入超级管理员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_super_admin', '超级管理员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 0, 0);

-- 插入审核员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_auditor', '审核员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 2, 0);

-- 插入客服人员
INSERT INTO `admin_info` (`username`, `name`, `password`, `type`, `is_deleted`) 
VALUES ('test_cs', '客服人员', '$2a$10$BDudvN2t5.Xt/T4Lu2l49.CmrOIgdpY/55Ppxh4OK73nYnLAI.yv2', 3, 0);
