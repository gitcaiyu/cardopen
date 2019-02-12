package cn.leadeon.cardopen.common.reqBody;

import cn.leadeon.cardopen.entity.nmg_order_info;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
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
     * 渠道编码
     */
    private String code;

    /**
     * 渠道地址
     */
    private String address;

    /**
     * 申请开卡明细信息
     */
    private List<nmg_order_info> orderResult = new ArrayList<nmg_order_info>();

}
