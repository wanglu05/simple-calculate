package calculate.enums;

import java.util.HashMap;
import java.util.Optional;

/**
 * 函数枚举
 * 	实现新增函数，需要实现MeterCalculate的functionCalculateFactory方法
 * @author wanglu
 */
public enum FuncEnum {

	/**
	 * ABS(数字)：取绝对值。例如：ABS(-123.23)=123.23
	 */
	ABS(1, "ABS"),

	/**
	 * INT(数字)：向下取舍为最接近的整数。例如：INT(123.23)=123，INT(-123.23)=-124
	 */
	INT(2, "INT"),

	/**
	 * ROUND(数字，小数位数)：四舍五入保留几位小数。例如：ROUND(1234.1234,2)=1234.12
	 */
	ROUND(3, "ROUND"),

	/**
	 * MAX(数字1，数字2，数字3...)：取最大值的数字。例如：MAX(1,2,3)= 3
	 */
	MAX(4, "MAX"),

	/**
	 * MIN(数字1，数字2，数字3...)：取最小值的数字。例如：MIN(1,2,3)= 1
	 */
	MIN(5, "MIN"),

	/**
	 * MOD(数字1，数字2)：取数字1除以数字2以后的余数。例如：MOD(5,2)=1
	 */
	MOD(6, "MOD"),

	/**
	 * POWER(数字1，数字2)：求数字1的数字2次方(幂函数)。例如POWER(5,2)=25
	 */
	POWER(7, "POWER"),

	/**
	 * QUOTIENT(数字1，数字2)：数字1除以数字2的商数。例如：QUOTIENT(5,2)=2
	 */
	QUOTIENT(8, "QUOTIENT"),

	/**
	 * SQRT(数字)：数字的平方根。例如：SQRT(9)=3。
	 */
	SQRT(9, "SQRT"),

	/**
	 * TROWC(数字)：直接返回数字的整数部分。例如：TROWC(123.12)=123。TROWC(-123.12)=-123
	 */
	TROWC(10, "TROWC"),

	/**
	 * 此函数与IN操作类似，不同之处是CONTAIN值包含两个参数
	 */
	CONTAIN(11, "CONTAIN"),

	/**
	 * 做包含匹配，返回0,1,；eg:IN("3rwere","r","e")
	 * 参数至少大于等于2，第一个参数为原内容，后面的参数为匹配
	 * 有一个未匹配上返回0，否则返回1
	 */
	IN(12, "IN"),
	NOT(13, "!"),
	/**
	 * 汇总函数：根据类型和类型值统计当前批次中的分摊数量
	 * 	包含两个参数，第一个数字，可转移为SumTypeEnum枚举，第二个是SumType所对应的选项值
	 * 	使用此函数，需实现FilterSumTypeFunctionInterface接口
	 */
	SUMTYPE(14, "SUMTYPE");

	FuncEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	static HashMap<String, FuncEnum> funcEnumHashMap = new HashMap<>();

    public static Optional<String> getFuncNameByCode(Integer code) {
        Optional<String> operateName = Optional.empty();
        for (FuncEnum funcEnum : values()) {
            if (funcEnum.code.equals(code)) {
                operateName = Optional.of(funcEnum.name);
                break;
            }
        }
        return operateName;
    }

	public static FuncEnum getFuncEnumByName(String name) {
		if (funcEnumHashMap.isEmpty()) {
			for (FuncEnum enumData : values()) {
				funcEnumHashMap.put(enumData.getName(), enumData);
			}
		}
		return funcEnumHashMap.get(name);
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
