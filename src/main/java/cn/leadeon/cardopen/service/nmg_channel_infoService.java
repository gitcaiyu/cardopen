package cn.leadeon.cardopen.service;

import cn.leadeon.cardopen.common.CodeEnum;
import cn.leadeon.cardopen.common.NMGMobile;
import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.entity.nmg_channel_info;
import cn.leadeon.cardopen.entity.nmg_city_info;
import cn.leadeon.cardopen.entity.nmg_user_info;
import cn.leadeon.cardopen.mapper.nmg_channel_infoMapper;
import cn.leadeon.cardopen.mapper.nmg_city_infoMapper;
import cn.leadeon.cardopen.mapper.nmg_county_infoMapper;
import cn.leadeon.cardopen.mapper.nmg_user_infoMapper;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class nmg_channel_infoService {

    @Autowired
    private nmg_channel_infoMapper nmg_channel_infoMapper;

    @Autowired
    private nmg_user_infoMapper nmg_user_infoMapper;

    @Autowired
    private nmg_city_infoMapper nmg_city_infoMapper;

    @Autowired
    private nmg_county_infoMapper nmg_county_infoMapper;

    @Autowired
    private NMGMobile nmgMobile;

    @Transactional
    public CardResponse myChannelInfo(String data,HttpSession httpSession) {
        CardResponse cardResponse = new CardResponse();
        String phone = JSONObject.parseObject(data).getString("phone");
        if (nmgMobile.isValid(phone)) {
            try {
                Map result = new HashMap();
                Map param = new HashMap();
                nmg_user_info nmg_user_info = nmg_user_infoMapper.getUserInfoByPhone(phone);
                httpSession.setAttribute("userInfo",nmg_user_info);
                param.put("city", nmg_user_info.getCityCode());
                //userType=1：盟市管理员，userType=2：普通社渠人员
                if (nmg_user_info.getUserRole().equals("1")) {
                    result.put("channel",nmg_channel_infoMapper.myChannelInfo(param));
                    cardResponse.setRspBody(result);
                } else {
                    Map map = new HashMap();
                    map.put("phone", phone);
                    nmg_city_info nmg_city_info = nmg_city_infoMapper.cityInfo(param);
                    map.put("city", nmg_city_info);
                    result.put("city",map);
                    map = new HashMap();
                    map.put("county", nmg_county_infoMapper.countyInfo(param));
                    result.put("county",map);
                    map = new HashMap();
                    map.put("chargeTel", nmg_user_info.getUserTel());
                    map.put("channel", nmg_channel_infoMapper.myChannelInfo(map));
                    result.put("channel",map);
                    cardResponse.setRspBody(result);
                }
            } catch (Exception e) {
                cardResponse.setRetCode(CodeEnum.failed.getCode());
                cardResponse.setRetDesc(CodeEnum.failed.getDesc());
            }
        } else {
            cardResponse.setRetCode(CodeEnum.notValieMobile.getCode());
            cardResponse.setRetDesc(CodeEnum.notValieMobile.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse channelUpdate(nmg_channel_info nmg_channel_info) {
        CardResponse cardResponse = new CardResponse();
        try {
            nmg_channel_infoMapper.channelUpdate(nmg_channel_info);
        } catch (Exception e) {
            cardResponse.setRetCode(CodeEnum.failed.getCode());
            cardResponse.setRetDesc(CodeEnum.failed.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse channelDel(String data) {
        CardResponse cardResponse = new CardResponse();
        String channelId = JSONObject.parseObject(data).getString("channelId");
        if (channelId != null || !channelId.trim().equals("")) {
            try {
                nmg_channel_infoMapper.channelDel(channelId);
            } catch (Exception e) {
                cardResponse.setRetCode(CodeEnum.failed.getCode());
                cardResponse.setRetDesc(CodeEnum.failed.getDesc());
            }
        } else {
            cardResponse.setRetCode(CodeEnum.nullValue.getCode());
            cardResponse.setRetDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

}
