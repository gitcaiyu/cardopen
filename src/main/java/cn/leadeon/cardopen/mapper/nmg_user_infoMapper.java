package cn.leadeon.cardopen.mapper;

import cn.leadeon.cardopen.entity.nmg_user_info;

public interface nmg_user_infoMapper {
    int insert(nmg_user_info record);

    int insertSelective(nmg_user_info record);

    nmg_user_info getUserInfoByPhone(String phone);
}