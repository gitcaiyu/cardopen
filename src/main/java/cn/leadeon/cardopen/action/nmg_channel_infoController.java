package cn.leadeon.cardopen.action;

import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.entity.nmg_channel_info;
import cn.leadeon.cardopen.service.nmg_channel_infoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
public class nmg_channel_infoController {

    @Autowired
    private nmg_channel_infoService nmg_channel_infoService;

    /**
     * 社会渠道人员手厅入口
     * @param data
     * @return
     */
    @RequestMapping(value = "/myChannelInfo",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse myChannelInfo(@RequestBody String data, HttpSession httpSession) {
        return nmg_channel_infoService.myChannelInfo(data,httpSession);
    }

    /**
     * 盟市负责人修改社渠人员信息
     * @param nmg_channel_info
     * @return
     */
    @RequestMapping(value = "/channelUpdate",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse channelUpdate(@RequestBody nmg_channel_info nmg_channel_info) {
        return nmg_channel_infoService.channelUpdate(nmg_channel_info);
    }

    /**
     * 渠道信息删除
     * @param data
     * @return
     */
    @RequestMapping(value = "/channelDel",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse channelDel(@RequestBody String data) {
        return nmg_channel_infoService.channelDel(data);
    }


}
