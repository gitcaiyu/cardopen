package cn.leadeon.cardopen.common.reqBody;

import cn.leadeon.cardopen.entity.nmg_order_info;
import lombok.Data;

import java.util.List;

@Data
public class OrderSubmission {

    /**
     * 收件人名字
     */
    private String name;

    /**
     * 收件人联系方式
     */
    private String phone;

    /**
     * 渠道地址
     */
    private String address;

    /**
     * 申请开卡明细信息
     */
    private List<nmg_order_info> result;

}
