package cn.leadeon.cardopen.service;

import cn.leadeon.cardopen.common.CodeEnum;
import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.mapper.nmg_channel_infoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class nmg_channel_infoService {

    @Autowired
    private nmg_channel_infoMapper nmg_channel_infoMapper;

    @Transactional
    public CardResponse myChannelInfo(String phone) {
        CardResponse cardResponse = new CardResponse();
        try {
            cardResponse.setResBody(nmg_channel_infoMapper.myChannelInfo(phone));
            cardResponse.setResCode(CodeEnum.success.getCode());
            cardResponse.setResDesc(CodeEnum.success.getDesc());
        } catch (Exception e) {
            cardResponse.setResCode(CodeEnum.failed.getCode());
            cardResponse.setResDesc(CodeEnum.failed.getDesc());
        }
        return cardResponse;
    }

}
