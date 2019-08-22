package constant;

/**
 * @author wanglu
 * @create 2019-03-18 15:29
 **/
public class Constants {

    public static final String EMPTY                                     = "";
    public static final String ZERO                                      = "0";
    public static final String COMMA                                     = ",";
    public static final String STRING_ONE                                = "1";
    public static final String EMPTY_BLANK                               = " ";
    public static final String VARIABLE_HEADER                           = "数值";
    public static final String CONDITION_HEADER                          = "如果";
    public static final String CONDITION_HEADER_OR                       = "否则";
    public static final String CONDITION_HEADER_GO                       = "则";
    public static final String CONDITION_HEADER_GO_OR                    = "否则如果";
    public static final String CONDITION_HEADER_FINISH                   = "如果完";
    public static final String CONDITION_TWO_EQUAL                       = "==";
    public static final String EQUAL                                     = "=";
    public static final String SIGN_ADD                                  = "+";
    public static final String SIGN_SUBTRACT                             = "-";
    public static final String SIGN_ADD_SUBTRACT                         = "+-";
    public static final String SIGN_SUBTRACT_ADD                         = "-+";
    public static final String SIGN_MULTIPLY                             = "*";
    public static final String SIGN_DIVIDE                               = "/";
    public static final String SIGN_REMAINDER                             = "%";
    public static final String TRUE                                      = "true";
    public static final String FALSE                                     = "false";
    public static final String SIGN_NOT                                  = "!";
    public static final String CHARSET_NAME                              = "utf8";
    public static final String REGEX_BLANK                               = "[\\s]*";
    public static final String BRACKET_START                             = "(";
    public static final String BRACKET_END                               = ")";
    public static final String ANNOTATION                                = "//";
    public static final String FUN_EXCEPTION                             = "函数{0}定义参数异常";
    public static final String SLASH_SINGLE                              = "\\'";
    public static final String REPLACE_SINGLE                            = "${odd}";
    public static final String SLASH_DOUBLE                              = "\\\"";
    public static final String REPLACE_DOUBLE                            = "${even}";
    public static final String SIGN_SINGLE                               = "'";
    public static final String SIGN_DOUBLE                               = "\"";
    public static final String BRACKET_RIGHT                             = "]";
    public static final String BRACKET_LEFT                              = "[";
    public static final String VERTICAL_BAR                              = "|";
    public static final Integer DEFAULT_INT                              = 0;
    public static final String DEFAULT_DATE_FORMAT                       = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT                   = "yyyy-MM-dd HH:mm:ss";

    /**
     * 自定义变量放入变量池中的初始化值
     */
    public static final Object DEFAULT_VARIABLE_POOL_VALUE = "0000";

    /**
     * 字符串临时变量开始标识
     */
    public static final String STRING_VARIABLE_START_SIGN = "字符串临时变量";
    public static final String STRING_VARIABLE_END_SIGN = "号";


    /**
     * 计量表必须存在的变量值
     * 请勿随意更改位置,会使用到索引位置
     */
    public static final String[] METER_MUST_VARIABLE = { "分摊用量", "分摊金额", "分摊比例", "分摊单价" };

    /**
     * 房间表信息系统变量
     * 请勿随意更改位置,会使用到索引位置
     */
    public static final String[] ROOM_SYSTEM_VARIABLE = { "房间表_仪表编号", "房间表_仪表名称", "房间表_表计类型",  "房间表_表属性", "房间表_是否反向", "房间表_仪表倍率", "房间表_初始读数", "房间表_初始化累计用量", "房间表_初始化年份" };

    /**
     * 房间抄表信息系统变量
     * 请勿随意更改位置,会使用到索引位置
     */
    public static final String[] ROOM_READ_SYSTEM_VARIABLE = {"房间表_本次止数", "房间表_新表起数", "房间表_新表止数", "房间表_损耗", "房间表_本次抄表日期", "房间表_用量", "房间表_旧表用量", "房间表_合计用量", "房间表_本年累计用量", "房间表_单价", "房间表_应收金额", "房间表_是否启用新表"};

    /**
     *
     * 将不换行空格（NO-BREAK SPACE，Unicode 0x00a0，UTF-8编码：0xC2A0）替换为普通空格。
     *
     * 用于避免因数据库字符集不兼容导致这个字符变为问号“?”的情况。
     * 网页很容易传递出类似空格的符号但非空格16进制为C2A0
     */
    public static String noBreakSpaceToSpace(String str) {
        if (str == null) {
            return null;
        }
        char nbsp = 0x00a0;
        return str.replace(nbsp, ' ');
    }

    /**
     * 将字符串转换为进制
     * @param str
     * @return
     */
    public static String convertSixteen(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder builder = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            builder.append(chars[bit]);
            bit = bs[i] & 0x0f;
            builder.append(chars[bit]);
            //builder.append(' ');
        }
        return builder.toString().trim();
    }

    public static void main(String[] args) {
        String str  = " ";
        System.out.println(convertSixteen(str));
        str = noBreakSpaceToSpace(str);
        System.out.println(convertSixteen(str));
    }
}
