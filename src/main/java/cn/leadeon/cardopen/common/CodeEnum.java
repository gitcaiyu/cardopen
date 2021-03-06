package cn.leadeon.cardopen.common;

public enum CodeEnum {

    success("操作成功", "000000"), failed("操作失败", "999999"), nullValue("参数为空", "999998"), notValieMobile("当前手机号码不是内蒙移动号", "999997"), loginFaild("当前用户不存在","999996"),notinchannel("当前用户不在渠道中","999995");

    private String desc;

    public String getDesc() {
        return desc;
    }

    private String code;

    public String getCode() {
        return code;
    }

    CodeEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

}
