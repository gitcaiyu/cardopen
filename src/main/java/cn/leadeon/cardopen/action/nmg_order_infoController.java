package cn.leadeon.cardopen.action;

import cn.leadeon.cardopen.common.reqBody.OrderSubmission;
import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.service.nmg_order_infoService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class nmg_order_infoController {

    @Autowired
    private nmg_order_infoService nmg_order_infoService;

    /**
     * 申请开卡页面初始化
     * @param data
     * @return
     */
    @RequestMapping(value = "/applyCard",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse applyCard(@RequestBody String data) {
        return nmg_order_infoService.applyCard(data);
    }

    /**
     * 订单提交
     * @return
     */
    @RequestMapping(value = "/submission",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse submission(@Valid @RequestBody OrderSubmission orderSubmission) {
        return nmg_order_infoService.submission(orderSubmission);
    }

    /**
     * 查询申请记录
     * @param data
     * @return
     */
    @RequestMapping(value = "/detail",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse detail(@RequestBody String data) {
        return nmg_order_infoService.detail(data);
    }


    /**
     * 订单明细删除
     * @param data
     * @return
     */
    @RequestMapping(value = "/orderInfoDel",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse orderInfoDel(@RequestBody String data) {
        return nmg_order_infoService.orderInfoDel(data);
    }

    /**
     * 订单状态更新
     * @param data
     * @return
     */
    @RequestMapping(value = "/orderStateUpdate",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse orderStateUpdate(@RequestBody String data) {
        return nmg_order_infoService.orderStateUpdate(data);
    }

    /**
     * 订单信息导出
     * @param data
     * @return
     */
    @RequestMapping(value = "/orderExport",method = RequestMethod.POST)
    @CrossOrigin
    public CardResponse orderExport(@RequestBody String data) {
        return nmg_order_infoService.orderExport(data);
    }

}
