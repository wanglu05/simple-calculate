package calculate.enums;

/**
 * 变量类型
 *
 * @author wanglu
 * @create 2019-03-18 16:19
 **/
public enum VariateTypeEnum {
    BIG_DECIMAL(0, "任意精度带符号的十进制数"),
    INTEGER(1, "整数"),
    STRING(2, "字符串");
    private int value;
    private String desc;

    VariateTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static VariateTypeEnum fromValue(Integer value) {
        for (VariateTypeEnum s : VariateTypeEnum.values()) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
