package cn.leadeon.cardopen.action;

import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.service.nmg_channel_infoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class nmg_channel_infoController {

    @Autowired
    private nmg_channel_infoService nmg_channel_infoService;

    /**
     * 社会渠道人员手厅入口
     * @param phone
     * @return
     */
    @RequestMapping(value = "/myChannelInfo",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse myChannelInfo(String phone) {
        return nmg_channel_infoService.myChannelInfo(phone);
    }



}
