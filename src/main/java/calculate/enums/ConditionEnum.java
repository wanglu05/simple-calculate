package calculate.enums;

import java.util.HashMap;
import java.util.Optional;

/**
 * 条件枚举
 * @author wanglu
 */
public enum ConditionEnum {

	LT(1, "<"),
	GT(2, ">"),
	LE(3, "<="),
	GE(4, ">="),
	EE(5, "=="),
	NE(6, "!="),
	AND(7, "&&"),
	OR(8, "||");

	ConditionEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	static HashMap<String, ConditionEnum> conditionEnumHashMap = new HashMap<>();

    public static Optional<String> getConditionNameByCode(Integer code) {
        Optional<String> operateName = Optional.empty();
        for (ConditionEnum funcEnum : values()) {
            if (funcEnum.code.equals(code)) {
                operateName = Optional.of(funcEnum.name);
                break;
            }
        }
        return operateName;
    }

	public static ConditionEnum getConditionEnumByName(String name) {
		if (conditionEnumHashMap.isEmpty()) {
			for (ConditionEnum enumData : values()) {
				conditionEnumHashMap.put(enumData.getName(), enumData);
			}
		}
		return conditionEnumHashMap.get(name);
	}
    
	private Integer code;
	private String name;

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}
}
