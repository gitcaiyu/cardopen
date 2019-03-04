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
    public CardResponse myChannelInfo(String data, HttpSession httpSession) {
        CardResponse cardResponse = new CardResponse();
        String phone = JSONObject.parseObject(data).getString("phone");
        JSONObject jsonObject = JSONObject.parseObject(data).getJSONObject("reqBody");
        if (nmgMobile.isValid(phone)) {
            try {
                Map result = new HashMap();
                Map param = new HashMap();
                nmg_user_info nmg_user_info = nmg_user_infoMapper.getUserInfoByPhone(phone);
                httpSession.setAttribute("userInfo", nmg_user_info);
                param.put("city", nmg_user_info.getCityCode());
                if (null != jsonObject) {
                    if (jsonObject.getString("chargeName") != null && !jsonObject.getString("chargeName").equals("")) {
                        param.put("chargeName", jsonObject.getString("chargeName"));
                    }
                    if (jsonObject.getString("chargeTel") != null && !jsonObject.getString("chargeTel").equals("")) {
                        param.put("chargeTel", jsonObject.getString("chargeTel"));
                    }
                }
                List<Map<String, Object>> myChannelInfo = new ArrayList<>();
                //userType=3：盟市管理员，userType=2：社渠人员
                if (nmg_user_info.getUserRole().equals("3")) {
                    myChannelInfo = nmg_channel_infoMapper.myChannelInfo(param);
                    result.put("channel", myChannelInfo);
                } else {
                    Map map = new HashMap();
                    map.put("phone", phone);
                    nmg_city_info nmg_city_info = nmg_city_infoMapper.cityInfo(param);
                    map.put("city", nmg_city_info);
                    result.put("city", map);
                    map = new HashMap();
                    map.put("county", nmg_county_infoMapper.countyInfo(param));
                    result.put("county", map);
                    map = new HashMap();
                    map.put("chargeTel", nmg_user_info.getUserTel());
                    myChannelInfo = nmg_channel_infoMapper.myChannelInfo(param);
                    map.put("channel", myChannelInfo);
                    result.put("channel", map);
                }
                if (myChannelInfo.size() > 1) {
                    result.put("flag","2");
                } else if (myChannelInfo.size() == 1) {
                    result.put("flag","1");
                } else {
                    cardResponse.setRetCode(CodeEnum.notinchannel.getCode());
                    cardResponse.setRetDesc(CodeEnum.notinchannel.getDesc());
                }
                cardResponse.setRspBody(result);
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
    public CardResponse channelUpdate(JSONObject data) {
        CardResponse cardResponse = new CardResponse();
        try {
            JSONObject reqBody = data.getJSONObject("reqBody");
            nmg_channel_info nmg_channel_info = new nmg_channel_info();
            nmg_channel_info.setChannelId(reqBody.getString("channelId"));
            nmg_channel_info.setChannelName(reqBody.getString("channel_name"));
            nmg_channel_info.setChannelAddress(reqBody.getString("channel_address"));
            nmg_channel_info.setChargeName(reqBody.getString("charge_name"));
            nmg_channel_info.setChargeTel(reqBody.getString("charge_tel"));
            nmg_channel_info.setCity(reqBody.getString("city_code"));
            nmg_channel_info.setCounty(reqBody.getString("county_id"));
            nmg_channel_infoMapper.channelUpdate(nmg_channel_info);
        } catch (Exception e) {
            cardResponse.setRetCode(CodeEnum.failed.getCode());
            cardResponse.setRetDesc(CodeEnum.failed.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse channelDel(JSONObject data) {
        CardResponse cardResponse = new CardResponse();
        String channelId = data.getJSONObject("reqBody").getString("channelId");
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

    public CardResponse getChannelInfoById(JSONObject data) {
        CardResponse cardResponse = new CardResponse();
        Map result = new HashMap();
        Map param = new HashMap();
        Map map = new HashMap();
        nmg_user_info nmg_user_info = nmg_user_infoMapper.getUserInfoByPhone(data.getString("phone"));
        param.put("city", nmg_user_info.getCityCode());
        map.put("city", nmg_city_infoMapper.cityInfo(param));
        result.put("city", map);
        map = new HashMap();
        map.put("county", nmg_county_infoMapper.countyInfo(param));
        result.put("county", map);
        param.put("channelId", data.getJSONObject("reqBody").getString("channelId"));
        result.put("channel", nmg_channel_infoMapper.getChannelInfoById(param));
        cardResponse.setRspBody(result);
        return cardResponse;
    }

}
