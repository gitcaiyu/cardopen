package cn.leadeon.cardopen.mapper;

import cn.leadeon.cardopen.entity.nmg_channel_info;

import java.util.List;
import java.util.Map;

public interface nmg_channel_infoMapper {
    int insert(nmg_channel_info record);

    int insertSelective(nmg_channel_info record);

    List<Map<String,Object>> myChannelInfo(String phone);
}