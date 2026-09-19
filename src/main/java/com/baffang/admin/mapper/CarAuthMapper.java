package com.baffang.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarAuthMapper {

    /**
     * 统计已认证车辆数量
     */
    int countAuthenticatedCars();

    /**
     * 统计车辆认证总数
     */
    int countTotal();
}
