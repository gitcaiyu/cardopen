package cn.leadeon.cardopen.mapper;

import cn.leadeon.cardopen.entity.nmg_meal_info;

import java.util.List;
import java.util.Map;

public interface nmg_meal_infoMapper {
    int insert(nmg_meal_info record);

    int insertSelective(nmg_meal_info record);

    /**
     * 获取所有套餐列表
     *
     * @return
     */
    List<Map<String, String>> applyCardMeal(Map param);
}