package calculate.support;


/**
 * 设置变量过滤
 *
 * @author wanglu
 * @create 2019-04-02 14:00
 **/
public interface FilterGetVariateInterface {
    /**
     * 过滤变量获取值
     * 当获取变量值时优先调用此方法，此方法返回值为此key所设置值
     * @param key
     * @param value
     * @param isSystem 是否是系统变量
     * @return
     */
    Object filterVariateValue(String key, Object value, Boolean isSystem);
}
