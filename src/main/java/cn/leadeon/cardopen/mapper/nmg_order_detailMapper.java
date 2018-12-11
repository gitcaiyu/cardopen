package cn.leadeon.cardopen.mapper;

import cn.leadeon.cardopen.entity.nmg_order_detail;

public interface nmg_order_detailMapper {
    int insert(nmg_order_detail record);

    int insertSelective(nmg_order_detail record);
}