package cn.leadeon.cardopen.entity;

import lombok.Data;

@Data
public class nmg_order_info {
    private String orderId;

    private String batchId;

    private String city;

    private String county;

    private String channelId;

    private String channelName;

    private String orderMeal;

    private String orderTariff;

    private String orderDiscount;

    private String orderCount;

    private String orderState = "1";

    private String createTime;

    private String updateTime;

    private String updatePeople;

    private String subTime;

    private String orderAddressee;

    private String orderPhone;

    private String orderPeople;

    private String orderOtherPhone;

    private String orderOtherPeople;
}