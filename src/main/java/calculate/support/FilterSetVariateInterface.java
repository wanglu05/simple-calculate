package calculate.support;


/**
 * 设置变量过滤
 *
 * @author wanglu
 * @create 2019-04-02 14:00
 **/
public interface FilterSetVariateInterface {
    /**
     * 过滤变量设置
     * 当设置变量时优先调用此方法，此方法返回值为此key所设置值
     * @param key
     * @param value
     * @return
     */
    Object filterVariateValue (String key, Object value);
}
