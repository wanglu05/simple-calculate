package calculate.enums;

import java.util.HashMap;

/**
 * 类型汇总函数的Type枚举
 * @author wanglu
 */
public enum SumTypeEnum {

	BUILDING_TYPE(1, "房间类型-多层、小高层、别墅、电梯花园洋房、高层、无电梯花园洋房、商铺、物业用房、会所、酒店式公寓、商务公寓（SOHO）、写字楼、精装公寓、车位、其它、洋房、销售型非住宅、商业、医院、驾驶舱、已出租"),
	IS_PUBLIC(2, "是否公区-是、否"),
	RENOVATION(3, "装修类型-毛坯、精装、简装"),
	PROPERTY_RIGHTS(4, "产权性质-自有产权、业主产权"),
	LIVING_TYPE(5, "居住状态-已装修、已入住、空置"),
	BALANCE_STATUS(6, "结算状态-1:未售2:已售款未清3:已售未接4:已售已接"),
	PROPERTY_USES(7, "房间使用状态-产权人自用、产权人出租");

	SumTypeEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	static HashMap<String, SumTypeEnum> SumTypeEnumByNameHashMap = null;
	static HashMap<Integer, SumTypeEnum> SumTypeEnumByCodeHashMap = null;

    public static SumTypeEnum getSumTypeByCode(Integer code) {
		/// old code
        /*Optional<String> operateName = Optional.absent();
        for (SumTypeEnum funcEnum : values()) {
            if (funcEnum.code.equals(code)) {
                operateName = Optional.of(funcEnum.name);
                break;
            }
        }*/
		if (null == SumTypeEnumByCodeHashMap) {
			SumTypeEnumByCodeHashMap = new HashMap<>();
			for (SumTypeEnum enumData : values()) {
				SumTypeEnumByCodeHashMap.put(enumData.getCode(), enumData);
			}
		}
		return SumTypeEnumByCodeHashMap.get(code);
    }

	public static SumTypeEnum getSumTypeByName(String name) {
		if (null == SumTypeEnumByNameHashMap) {
			SumTypeEnumByNameHashMap = new HashMap<>();
			for (SumTypeEnum enumData : values()) {
				SumTypeEnumByNameHashMap.put(enumData.getName(), enumData);
			}
		}
		return SumTypeEnumByNameHashMap.get(name);
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
