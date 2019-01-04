package cn.leadeon.cardopen.common.resBody;

import cn.leadeon.cardopen.common.CodeEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CardResponse {

    private String retCode;

    private Map rspBody;

    private String retDesc;

    //默认返回成功
    public CardResponse() {
        setRetCode(CodeEnum.success.getCode());
        setRetDesc(CodeEnum.success.getDesc());
        setRspBody(new HashMap());
    }

}
