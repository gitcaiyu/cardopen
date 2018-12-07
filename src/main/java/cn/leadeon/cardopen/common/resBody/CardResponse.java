package cn.leadeon.cardopen.common.resBody;

import lombok.Data;

import java.util.List;

@Data
public class CardResponse {

    private String resCode;

    private List resBody;

    private String resDesc;

}
