package calculate;


import calculate.enums.VariateTypeEnum;

import java.io.Serializable;

/**
 * 变量DTO
 *
 * @author wanglu
 * @create 2019-03-18 15:17
 **/
public class VariateDto implements Serializable {

    private static final long serialVersionUID = 7959135944845711886L;
    public VariateDto () {

    }
    public VariateDto (String name, Object value, int type, boolean isTemporary) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.isTemporary = isTemporary;
        this.valid = true;
        if (null != this.value) {
            this.length = this.value.toString().length();
        }
    }
    public VariateDto (String name, Object value, int type) {
        this(name, value, type, false);
    }

    public VariateDto (String name, Object value) {
        this(name, value, VariateTypeEnum.BIG_DECIMAL.getValue());
    }

    /**
     * 变量名称
     */
    private String name;

    /**
     * 变量值
     */
    private Object value;

    /**
     * 变量类型
     */
    private int type;

    /**
     * 变量长度
     */
    private int length;

    /**
     * 是否是临时变量
     */
    private boolean isTemporary;

    /**
     * 是否有效
     */
    private boolean valid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
