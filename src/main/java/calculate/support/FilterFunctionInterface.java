package calculate.support;


import calculate.enums.FuncEnum;

/**
 * 函数过滤
 *
 * @author wanglu
 * @create 2019-04-28 17:55
 **/
public interface FilterFunctionInterface {
    /**
     * 过滤函数功能
     * 当设置变量时优先调用此方法，此方法返回函数处理值
     * @param funcEnum 函数类型
     * @param params 函数参数
     * @return
     */
    Object filterFunction(FuncEnum funcEnum, String[] params);
}
