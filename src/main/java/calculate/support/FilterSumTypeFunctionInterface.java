package calculate.support;


import calculate.enums.SumTypeEnum;

/**
 * 函数过滤
 *
 * @author wanglu
 * @create 2019-04-28 17:55
 **/
public interface FilterSumTypeFunctionInterface {
    /**
     * 过滤SumType函数功能
     *  当执行SumType函数时候
     * @param sumTypeEnum 函数类型
     * @param value 函数参数
     * @return
     */
    Object execute(SumTypeEnum sumTypeEnum, String value);
}
