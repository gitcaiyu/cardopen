package cn.leadeon.cardopen.service;

import cn.leadeon.cardopen.common.CodeEnum;
import cn.leadeon.cardopen.common.DateUtil;
import cn.leadeon.cardopen.common.RandomUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @Autowired
    private nmg_user_infoMapper nmg_user_infoMapper;

    @Autowired
    private nmg_city_infoMapper nmg_city_infoMapper;

    @Autowired
    private nmg_county_infoMapper nmg_county_infoMapper;

    @Value("${file.path}")
    private String path;

    @Value("${file.readPath}")
    private String readFilePath;

    @Transactional
    public CardResponse applyCard(String data) {
        CardResponse cardResponse = new CardResponse();
        String phone = JSONObject.parseObject(data).getString("phone");
        try {
            Map param = new HashMap();
            Map result = new HashMap();
            Map map = new HashMap();
            nmg_user_info nmg_user_info = nmg_user_infoMapper.getUserInfoByPhone(phone);
            if (nmg_user_info != null) {
                param.put("city", nmg_user_info.getCityCode());
                map.put("meal", nmg_meal_infoMapper.applyCardMeal(param));
                result.put("meal", map);
                map = new HashMap();
                map.put("discount", nmg_discount_infoMapper.applyCardDisc(param));
                result.put("discount", map);
                map = new HashMap();
                List<Map<String, Object>> myChannelInfo = new ArrayList<>();
                //userType=1：盟市管理员，userType=2：普通社渠人员
//                if (nmg_user_info.getUserRole().equals("1")) {
//                    map.put("city", nmg_user_info.getCityCode());
//                    myChannelInfo = nmg_channel_infoMapper.myChannelInfo(param);
//                    map.put("channel", myChannelInfo);
//                } else {
                param.put("chargeTel", phone);
                myChannelInfo = nmg_channel_infoMapper.myChannelInfo(param);
                map.put("channel", myChannelInfo);
//                }
                if (myChannelInfo.size() > 0) {
                    result.put("channelBase", map);
                    cardResponse.setRspBody(result);
                    cardResponse.setRetCode(CodeEnum.success.getCode());
                    cardResponse.setRetDesc(CodeEnum.success.getDesc());
                } else {
                    cardResponse.setRetCode(CodeEnum.notinchannel.getCode());
                    cardResponse.setRetDesc(CodeEnum.notinchannel.getDesc());
                }
            } else {
                cardResponse.setRetCode(CodeEnum.loginFaild.getCode());
                cardResponse.setRetDesc(CodeEnum.loginFaild.getDesc());
            }
        } catch (Exception e) {
            cardResponse.setRetCode(CodeEnum.failed.getCode());
            cardResponse.setRetDesc(CodeEnum.failed.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse submission(Map map) {
        Map reqBody = (Map) map.get("reqBody");
        CardResponse cardResponse = new CardResponse();
        List orderResult = (List) reqBody.get("orderResult");
        Map param = new HashMap();
        param.put("cityName", reqBody.get("city"));
        param.put("countyName", reqBody.get("country"));
        if (orderResult.size() != 0) {
            try {
                String city = nmg_city_infoMapper.cityInfo(param).getCityCode();
                String orderId = RandomUtil.orderid(city);
                for (int i = 0; i < orderResult.size(); i++) {
                    nmg_order_info nmg_order_info = new nmg_order_info();
                    Map result = (Map) orderResult.get(i);
                    if (reqBody.get("orderId") == null) {
                        nmg_order_info.setOrderId(orderId);
                        nmg_order_info.setOrderOtherPeople(reqBody.get("orderOtherPeople").toString());
                        nmg_order_info.setOrderOtherPhone(reqBody.get("orderOtherPhone").toString());
                        nmg_order_info.setSubTime(DateUtil.formatFullDateToString());
                        nmg_order_info.setChannelName(reqBody.get("channelName").toString());
                        nmg_order_info.setChannelId(reqBody.get("channelId").toString());
                        nmg_order_info.setCity(city);
                        nmg_order_info.setCounty(nmg_county_infoMapper.countyInfo(param).get(0).get("county_id").toString());
                        nmg_order_info.setOrderMeal(result.get("orderMeal").toString());
                        nmg_order_info.setOrderTariff(result.get("orderTariff").toString());
                        nmg_order_info.setOrderDiscount(result.get("orderDiscount").toString());
                        nmg_order_info.setOrderCount(result.get("orderCount").toString());
                        nmg_order_info.setOrderAddressee(reqBody.get("address").toString());
                        nmg_order_info.setOrderPhone(reqBody.get("phone").toString());
                        nmg_order_info.setOrderPeople(reqBody.get("name").toString());
                        nmg_order_infoMapper.insert(nmg_order_info);
                    }
                }
            } catch (Exception e) {
                cardResponse.setRetCode(CodeEnum.failed.getCode());
                cardResponse.setRetDesc(CodeEnum.failed.getDesc());
            }
        } else {
            cardResponse.setRetCode(CodeEnum.nullValue.getCode());
            cardResponse.setRetDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse detail(String data) {
        CardResponse cardResponse = new CardResponse();
        JSONObject jsonObject = JSONObject.parseObject(data).getJSONObject("reqBody");
        String phone = JSONObject.parseObject(data).getString("phone");
        String subTime = jsonObject.getString("subTime");
        String subTImeE = jsonObject.getString("subTImeE");
        String createTime = jsonObject.getString("createTime");
        String createTimeE = jsonObject.getString("createTimeE");
        String state = jsonObject.getString("state");
        if (phone != null) {
            Map param = new HashMap();
            nmg_user_info nmg_user_info = nmg_user_infoMapper.getUserInfoByPhone(phone);
            if (nmg_user_info.getUserRole().equals("3")) {
                param.put("city",nmg_user_info.getCityCode());
            } else {
                param.put("phone", phone);
            }
            if (!subTime.equals("")) {
                param.put("subtime", subTime);
            }
            if (!subTImeE.equals("")) {
                param.put("subtimeE", subTImeE);
            }
            if (!createTime.equals("")) {
                param.put("createtime", createTime);
            }
            if (!createTimeE.equals("")) {
                param.put("createtimeE", createTimeE);
            }
            if (!state.equals("")) {
                param.put("state", state);
            }
            Map result = new HashMap();
            List<Map<String, Object>> detail = nmg_order_infoMapper.queryCountByPhone(param);
            List<Map<String, Object>> results = new ArrayList<>();
            for (int i = 0; i < detail.size(); i++) {
                Map r = new HashMap();
                Map dMap = detail.get(i);
                param.put("orderId", dMap.get("number"));
                List<Map<String, Object>> d = nmg_order_infoMapper.detail(param);
                List arrList = new ArrayList();
                for (int j = 0; j < d.size(); j++) {
                    Map dm = d.get(j);
                    Map arr = new HashMap();
                    arr.put("meal_name", dm.get("meal_name"));
                    arr.put("meal_code", dm.get("meal_code"));
                    arr.put("discount_name", dm.get("discount_name"));
                    arr.put("amount", dm.get("amount"));
                    arr.put("applyTime", dm.get("applyTime"));
                    arr.put("batchId",dm.get("batch_id"));
                    arrList.add(arr);
                    r.put("number", dm.get("number"));
                    r.put("status", dm.get("status"));
                    r.put("arr", arrList);
                }
                results.add(r);
            }
            result.put("detail", results);
            cardResponse.setRspBody(result);
        } else {
            cardResponse.setRetCode(CodeEnum.nullValue.getCode());
            cardResponse.setRetDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse orderInfoDel(String data) {
        CardResponse cardResponse = new CardResponse();
        JSONObject jsonObject = JSONObject.parseObject(data).getJSONObject("reqBody");
        String batchId = jsonObject.getString("batchId");
        if (batchId != null) {
            nmg_order_infoMapper.orderInfoDel(batchId);
        } else {
            cardResponse.setRetCode(CodeEnum.nullValue.getCode());
            cardResponse.setRetDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse orderStateUpdate(String data) {
        CardResponse cardResponse = new CardResponse();
        JSONObject jsonObject = JSONObject.parseObject(data).getJSONObject("reqBody");
        String orderId = jsonObject.getString("orderId");
        String orderState = jsonObject.getString("orderState");
        if (orderId != "" || orderState != "") {
            Map param = new HashMap();
            param.put("orderId", orderId);
            param.put("orderState", orderState);
            nmg_order_infoMapper.orderStateUpdate(param);
        } else {
            cardResponse.setRetCode(CodeEnum.nullValue.getCode());
            cardResponse.setRetDesc(CodeEnum.nullValue.getDesc());
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse orderExport(String data, HttpServletRequest httpServletRequest) throws IOException {
        CardResponse cardResponse = new CardResponse();
        String fileName = path;
        File file1 = new File(fileName);
        if (!file1.exists()) {
            file1.mkdir();
        }
        String file = "";
        String orderId = JSONObject.parseObject(data).getJSONObject("reqBody").getString("orderId");
        try {
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet sheet = hssfWorkbook.createSheet("工单信息");
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
            Map param = new HashMap();
            param.put("orderId",orderId);
            List<Map<String, Object>> result = nmg_order_infoMapper.exportOrder(param);
            for (int i = 0; i < result.size(); i++) {
                Map maps = result.get(i);
                row = sheet.createRow(i + 1);
                if (maps.get("order_id") != null) {
                    row.createCell(0).setCellValue((String) maps.get("order_id"));
                    if (i == 0) {
                        fileName = fileName + maps.get("order_id").toString() + ".xls";
                        file = maps.get("order_id").toString() + ".xls";
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
            String readPath = readFilePath + file;
            cardResponse.setRetDesc(readPath);
        } catch (Exception e) {
            cardResponse.setRetCode(CodeEnum.failed.getCode());
            cardResponse.setRetDesc(CodeEnum.failed.getDesc());
        }
        return cardResponse;
    }

    public CardResponse modify(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data).getJSONObject("reqBody");
        String phone = JSONObject.parseObject(data).getString("phone");
        String orderId = jsonObject.getString("orderId");
        CardResponse cardResponse = new CardResponse();
        Map param = new HashMap();
        Map result = new HashMap();
        Map map = new HashMap();
        nmg_user_info nmg_user_info = nmg_user_infoMapper.getUserInfoByPhone(phone);
        param.put("city", nmg_user_info.getCityCode());
        map.put("meal", nmg_meal_infoMapper.applyCardMeal(param));
        result.put("meal", map);
        map = new HashMap();
        map.put("discount", nmg_discount_infoMapper.applyCardDisc(param));
        result.put("discount", map);
        param = new HashMap();
        param.put("orderId", orderId);
        List<Map<String, Object>> orderCount = nmg_order_infoMapper.getCountById(param);
        if (orderCount.size() > 0) {
            result.put("orderOtherPhone", orderCount.get(0).get("order_other_phone"));
            result.put("orderOtherPeople", orderCount.get(0).get("order_other_people"));
            result.put("orderAddress", orderCount.get(0).get("order_addressee"));
            result.put("channelName",orderCount.get(0).get("channel_name"));
            result.put("channelAddress",orderCount.get(0).get("channel_address"));
            result.put("flag", orderCount.get(0).get("flag"));
            result.put("count", orderCount);
            cardResponse.setRspBody(result);
        }
        return cardResponse;
    }

    @Transactional
    public CardResponse updateOrderInfo(JSONObject data) {
        CardResponse cardResponse = new CardResponse();
        JSONObject jsonObject = data.getJSONObject("reqBody");
        String phone = data.getString("phone");
        String orderId = jsonObject.getString("orderId");
        String orderOtherPeople = jsonObject.getString("orderOtherPeople");
        String orderOtherPhone = jsonObject.getString("orderOtherPhone");
        JSONArray jsonArray = jsonObject.getJSONArray("orderResult");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String batchId = obj.getString("batchId");
            String orderMeal = obj.getString("orderMeal");
            String orderDiscount = obj.getString("orderDiscount");
            String orderCount = obj.getString("orderCount");
            nmg_order_info nmg_order_info = new nmg_order_info();
            nmg_order_info.setOrderId(orderId);
            nmg_order_info.setOrderMeal(orderMeal);
            nmg_order_info.setOrderDiscount(orderDiscount);
            nmg_order_info.setOrderCount(orderCount);
            nmg_order_info.setOrderOtherPeople(orderOtherPeople);
            nmg_order_info.setOrderOtherPhone(orderOtherPhone);
            if (null != batchId) {
                nmg_order_info.setUpdateTime(DateUtil.formatFullDateToString());
                nmg_order_info.setUpdatePeople(phone);
                nmg_order_info.setBatchId(batchId);
                nmg_order_infoMapper.updateOrderInfo(nmg_order_info);
            } else {
                Map param = new HashMap();
                param.put("chargeTel",phone);
                List<Map<String, Object>> myChannelInfo = nmg_channel_infoMapper.myChannelInfo(param);
                Map channel = myChannelInfo.get(0);
                nmg_order_info.setChannelId(channel.get("channel_id").toString());
                nmg_order_info.setChannelName(channel.get("channel_name").toString());
                nmg_order_info.setCity(channel.get("city_code").toString());
                nmg_order_info.setCounty(channel.get("county_id").toString());
                nmg_order_info.setSubTime(DateUtil.formatFullDateToString());
                nmg_order_infoMapper.insert(nmg_order_info);
            }
        }
        return cardResponse;
    }
}
