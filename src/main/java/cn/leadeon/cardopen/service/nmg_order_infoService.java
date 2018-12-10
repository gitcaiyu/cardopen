package cn.leadeon.cardopen.service;

import cn.leadeon.cardopen.common.CodeEnum;
import cn.leadeon.cardopen.common.DateUtil;
import cn.leadeon.cardopen.common.RandomUtil;
import cn.leadeon.cardopen.common.reqBody.OrderSubmission;
import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.entity.nmg_order_info;
import cn.leadeon.cardopen.mapper.nmg_channel_infoMapper;
import cn.leadeon.cardopen.mapper.nmg_discount_infoMapper;
import cn.leadeon.cardopen.mapper.nmg_meal_infoMapper;
import cn.leadeon.cardopen.mapper.nmg_order_infoMapper;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class nmg_order_infoService {

    @Autowired
    private nmg_discount_infoMapper nmg_discount_infoMapper;

    @Autowired
    private nmg_meal_infoMapper nmg_meal_infoMapper;

    @Autowired
    private nmg_channel_infoMapper nmg_channel_infoMapper;

    @Autowired
    private nmg_order_infoMapper nmg_order_infoMapper;

    @Transactional
    public CardResponse applyCard(String phone) {
        CardResponse cardResponse = new CardResponse();
        try {
            List result = new ArrayList();
            Map map = new HashMap();
            map.put("meal",nmg_meal_infoMapper.applyCardMeal());
            result.add(map);
            map = new HashMap();
            map.put("discount",nmg_discount_infoMapper.applyCardDisc());
            result.add(map);
            map = new HashMap();
            map.put("chargeTel",phone);
            map.put("channelName",nmg_channel_infoMapper.myChannelInfo(map));
            result.add(map);
            cardResponse.setResBody(result);
            cardResponse.setResCode(CodeEnum.success.getCode());
            cardResponse.setResDesc(CodeEnum.success.getDesc());
        } catch (Exception e) {
            cardResponse.setResCode(CodeEnum.failed.getCode());
            cardResponse.setResDesc(CodeEnum.failed.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse submission(OrderSubmission orderSubmission) {
        CardResponse cardResponse = new CardResponse();
        if (orderSubmission.getResult().size() != 0) {
            try {
                JSONArray order = JSONArray.parseArray(orderSubmission.getResult().toString());
                for (int i = 0; i < order.size(); i++) {
                    nmg_order_info nmg_order_info = (cn.leadeon.cardopen.entity.nmg_order_info) order.get(i);
                    if (nmg_order_info.getOrderId() == null) {
                        nmg_order_info.setOrderId(RandomUtil.orderid(orderSubmission.getCode()));
                        nmg_order_info.setOrderPeople(orderSubmission.getName());
                        nmg_order_info.setSubTime(DateUtil.getDateString());
                        nmg_order_info.setCreateTime(DateUtil.getDateString());
                        nmg_order_infoMapper.insert(nmg_order_info);
                    } else {
                        nmg_order_info.setUpdateTime(DateUtil.getDateString());
                        nmg_order_info.setUpdatePeople(orderSubmission.getName());
                        nmg_order_infoMapper.updateOrderInfo(nmg_order_info);
                    }
                }
            } catch (Exception e) {
                cardResponse.setResCode(CodeEnum.failed.getCode());
                cardResponse.setResDesc(CodeEnum.failed.getDesc());
            }
        } else {
            cardResponse.setResCode(CodeEnum.nullValue.getCode());
            cardResponse.setResDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse detail(String phone) {
        CardResponse cardResponse = new CardResponse();
        if (phone != null) {
            cardResponse.setResBody(nmg_order_infoMapper.detail(phone));
        } else {
            cardResponse.setResCode(CodeEnum.nullValue.getCode());
            cardResponse.setResDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }
}
