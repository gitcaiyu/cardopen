package cn.leadeon.cardopen.action;

import cn.leadeon.cardopen.common.reqBody.OrderSubmission;
import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.service.nmg_order_infoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class nmg_order_infoController {

    @Autowired
    private nmg_order_infoService nmg_order_infoService;

    /**
     * 申请开卡页面初始化
     * @param phone
     * @return
     */
    @RequestMapping(value = "/applyCard",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse applyCard(String phone) {
        return nmg_order_infoService.applyCard(phone);
    }

    /**
     * 订单提交
     * @return
     */
    @RequestMapping(value = "/submission",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse submission(OrderSubmission orderSubmission) {
        return nmg_order_infoService.submission(orderSubmission);
    }

    /**
     * 查询申请记录
     * @param phone
     * @return
     */
    @RequestMapping(value = "/detail",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse detail(String phone) {
        return nmg_order_infoService.detail(phone);
    }

}
