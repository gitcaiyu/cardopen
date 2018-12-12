package cn.leadeon.cardopen.service;

import cn.leadeon.cardopen.common.CodeEnum;
import cn.leadeon.cardopen.common.DateUtil;
import cn.leadeon.cardopen.common.RandomUtil;
import cn.leadeon.cardopen.common.reqBody.OrderSubmission;
import cn.leadeon.cardopen.common.resBody.CardResponse;
import cn.leadeon.cardopen.entity.nmg_order_info;
import cn.leadeon.cardopen.entity.nmg_user_info;
import cn.leadeon.cardopen.mapper.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Stream;

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

    @Autowired
    private cn.leadeon.cardopen.mapper.nmg_user_infoMapper nmg_user_infoMapper;

    @Value("${file.path}")
    private String path;

    @Transactional
    public CardResponse applyCard(String data) {
        CardResponse cardResponse = new CardResponse();
        String phone = JSONObject.parseObject(data).getString("phone");
        try {
            List result = new ArrayList();
            Map map = new HashMap();
            map.put("meal",nmg_meal_infoMapper.applyCardMeal());
            result.add(map);
            map = new HashMap();
            map.put("discount",nmg_discount_infoMapper.applyCardDisc());
            result.add(map);
            map = new HashMap();
            nmg_user_info nmg_user_info = nmg_user_infoMapper.getUserInfoByPhone(phone);
            //userType=1：盟市管理员，userType=2：普通社渠人员
            if (nmg_user_info.getUserType().equals("1")) {
                map.put("city",nmg_user_info.getCityCode());
                map.put("channelName", nmg_channel_infoMapper.myChannelInfo(map));
            } else {
                map.put("chargeTel",phone);
                map.put("channelName", nmg_channel_infoMapper.myChannelInfo(map));
            }
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
                String orderId = RandomUtil.orderid(orderSubmission.getCode());
                for (int i = 0; i < order.size(); i++) {
                    nmg_order_info nmg_order_info = (cn.leadeon.cardopen.entity.nmg_order_info) order.get(i);
                    if (nmg_order_info.getOrderId() == null) {
                        nmg_order_info.setOrderId(orderId);
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
    public CardResponse detail(String data) {
        CardResponse cardResponse = new CardResponse();
        String phone = JSONObject.parseObject(data).getString("phone");
        if (phone != null) {
            cardResponse.setResBody(nmg_order_infoMapper.detail(phone));
        } else {
            cardResponse.setResCode(CodeEnum.nullValue.getCode());
            cardResponse.setResDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse orderInfoDel(String data) {
        CardResponse cardResponse = new CardResponse();
        String batchId = JSONObject.parseObject(data).getString("batchId");
        if (batchId != null) {
            nmg_order_infoMapper.orderInfoDel(batchId);
        } else {
            cardResponse.setResCode(CodeEnum.nullValue.getCode());
            cardResponse.setResDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse orderStateUpdate(String data) {
        CardResponse cardResponse = new CardResponse();
        String orderId = JSONObject.parseObject(data).getString("orderId");
        String orderState = JSONObject.parseObject(data).getString("orderState");
        if (orderId != "" || orderState != "") {
            Map param = new HashMap();
            param.put("orderId",orderId);
            param.put("orderState",orderState);
            nmg_order_infoMapper.orderStateUpdate(param);
        } else {
            cardResponse.setResCode(CodeEnum.nullValue.getCode());
            cardResponse.setResDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse orderExport(String data) {
        CardResponse cardResponse = new CardResponse();
        String fileName = path;
        String orderId = JSONObject.parseObject(data).getString("orderId");
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet sheet= hssfWorkbook.createSheet("工单信息");
            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("工单编号");
            cell = row.createCell(1);
            cell.setCellValue("套餐资费");
            cell = row.createCell(2);
            cell.setCellValue("资费代码");
            cell = row.createCell(3);
            cell.setCellValue("优惠促销");
            cell = row.createCell(4);
            cell.setCellValue("选购号码");
            cell = row.createCell(5);
            cell.setCellValue("SIM卡号");
            List<Map<String,Object>> result = nmg_order_infoMapper.exportOrder(orderId);
            for (int i = 0; i < result.size(); i++) {
                Map maps = result.get(i);
                row = sheet.createRow(i+1);
                if (maps.get("order_id") != null) {
                    row.createCell(0).setCellValue((String) maps.get("order_id"));
                    if (i == 0) {
                        fileName = fileName + maps.get("order_id").toString()+".xls";
                    }
                }
                if (maps.get("meal_name") != null) {
                    row.createCell(1).setCellValue((String) maps.get("meal_name"));
                }
                if (maps.get("meal_code") != null) {
                    row.createCell(2).setCellValue((String) maps.get("meal_code"));
                }
                if (maps.get("discount_name") != null) {
                    row.createCell(3).setCellValue((String) maps.get("discount_name"));
                }
                if (maps.get("cardNum") != null) {
                    row.createCell(4).setCellValue((String) maps.get("cardNum"));
                }
                if (maps.get("SIMNum") != null) {
                    row.createCell(5).setCellValue((String) maps.get("SIMNum"));
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.close();
            hssfWorkbook.close();
            cardResponse.setResDesc(fileName);
        } catch (Exception e) {
            cardResponse.setResCode(CodeEnum.failed.getCode());
            cardResponse.setResDesc(CodeEnum.failed.getDesc());
        }
        return cardResponse;
    }
}
