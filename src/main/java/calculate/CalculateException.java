package calculate;

import org.springframework.util.StringUtils;

import java.text.MessageFormat;

/**
 * 计算异常类
 *
 * @author wanglu
 * @create 2019-03-18 15:22
 **/
public class CalculateException extends RuntimeException {

    private static final long serialVersionUID = -8870636611755004587L;

    public CalculateException(String format) {
        super(format);
    }

    public CalculateException(String format, Object... arguments) {
        super(MessageFormat.format(format, arguments));
    }

    /**
     * @author WangLu
     * @param format 输入字符串
     * @param arguments ...参数集合
     * @return RuntimeException运行时异常
     * @date 2019/3/15 15:47
     */
    public static void throwException(String format, Object... arguments) {
        if (StringUtils.isEmpty(format)) {
            throw new RuntimeException("未确认的异常抛出！");
        }
        throw new CalculateException (format, arguments);
    }

    public static void throwException(boolean trueThrow, String format, Object... arguments) {
        if (trueThrow) {
            throwException(format, arguments);
        }
    }
}
