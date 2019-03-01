package cn.leadeon.cardopen.mapper;

import cn.leadeon.cardopen.entity.nmg_order_info;

import java.util.List;
import java.util.Map;

public interface nmg_order_infoMapper {
    int insert(nmg_order_info record);

    int insertSelective(nmg_order_info record);

    List<Map<String, Object>> detail(Map param);

    List<Map<String, Object>> getCountById(Map param);

    List<Map<String, Object>> queryCountByPhone(Map param);

    int updateOrderInfo(nmg_order_info record);

    int orderInfoDel(String batchId);

    int orderStateUpdate(Map param);

    List<Map<String, Object>> exportOrder(Map map);
}