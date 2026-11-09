package com.baffang.admin.mapper;

import com.baffang.common.entity.PlatformConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 平台参数配置 Mapper（单行配置）
 */
@Mapper
public interface PlatformConfigMapper {

    @Select("SELECT id, config_json AS configJson, update_admin_id AS updateAdminId, " +
            "update_time AS updateTime FROM platform_config WHERE id = 1")
    PlatformConfig selectOne();

    @Insert("INSERT INTO platform_config (id, config_json, update_admin_id) " +
            "VALUES (1, #{configJson}, #{updateAdminId}) " +
            "ON DUPLICATE KEY UPDATE config_json = VALUES(config_json), update_admin_id = VALUES(update_admin_id)")
    int upsert(PlatformConfig config);
}
