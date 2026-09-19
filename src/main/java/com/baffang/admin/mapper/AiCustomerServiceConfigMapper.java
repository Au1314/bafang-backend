// ==================== AI配置Mapper ====================
package com.baffang.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baffang.common.entity.AiCustomerServiceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AiCustomerServiceConfigMapper extends BaseMapper<AiCustomerServiceConfig> {
    
    @Select("SELECT * FROM ai_customer_service_config")
    List<AiCustomerServiceConfig> selectAllConfigs();
    
    @Select("SELECT config_value FROM ai_customer_service_config WHERE config_key = #{key}")
    String selectValueByKey(@Param("key") String key);
    
    @Update("UPDATE ai_customer_service_config SET config_value = #{value}, update_time = NOW() WHERE config_key = #{key}")
    int updateValueByKey(@Param("key") String key, @Param("value") String value);
}
