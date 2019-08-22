package calculate;

import calculate.enums.VariateTypeEnum;
import calculate.support.*;

import com.google.common.collect.Sets;
import constant.Constants;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计算基础类
 *
 * @author wanglu
 * @create 2019-03-18 17:24
 **/
public abstract class AbstractCalculate implements Serializable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long serialVersionUID = 5620414771855536933L;

    /**
     * 设置变量过滤
     */
    public FilterSetVariateInterface filterSetVariateInterface;

    /**
     * 读取变量过滤
     */
    public FilterGetVariateInterface filterGetVariateInterface;

    /**
     * 函数过滤
     */
    public FilterFunctionInterface filterFunctionInterface;

    /**
     * SumType函数执行
     */
    public FilterSumTypeFunctionInterface filterSumTypeFunctionInterface;

    /**
     * 变量池,用于存储当前自定义变量和数据
     */
    protected HashMap<String, VariateDto> VARIABLE_POOL = new HashMap<>();

    /**
     * 系统变量定义和初始化
     *  初始一般会由调用者来传递初始化，此处默认初始化值为Constants.STRING_ONE
     */
    protected HashMap<String, Object> SYSTEM_VARIABLE = new HashMap<String, Object>(){{
       for (String name : Constants.BASIS_SYSTEM_VARIABLE) {
            put(name, Constants.STRING_ONE);
        }

        for (String name : Constants.PUBLIC_AREA_READ_SYSTEM_VARIABLE) {
            put(name, Constants.STRING_ONE);
        }

        for (String name : Constants.ROOM_SYSTEM_VARIABLE) {
            put(name, Constants.STRING_ONE);
        }

        for (String name : Constants.ROOM_READ_SYSTEM_VARIABLE) {
            put(name, Constants.STRING_ONE);
        }

        for (String name : Constants.PUBLIC_AREA_SYSTEM_VARIABLE) {
            put(name, Constants.STRING_ONE);
        }
    }};


    /**
     * 过滤掉临时变量
     */
    public HashMap<String, VariateDto> VARIABLE_POOL() {
        HashMap<String, VariateDto> variablePool = new HashMap<>();
        for (Iterator<Map.Entry<String, VariateDto>> it = VARIABLE_POOL.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, VariateDto> item = it.next();
            if (!item.getValue().isTemporary() && item.getValue().isValid()) {
                variablePool.put(item.getKey(), item.getValue());
            }
        }
        return variablePool;
    }

    public HashMap<String, Object> SYSTEM_VARIABLE() {
        return SYSTEM_VARIABLE;
    }

    public Object putSystemVariable(String key, Object value) {
        return this.SYSTEM_VARIABLE.put(key, value);
    }

    public void putSystemVariable(HashMap<String, Object> SYSTEM_VARIABLE) {
        this.SYSTEM_VARIABLE = SYSTEM_VARIABLE;
    }

    protected Boolean isVerify = false;

    /**
     * 当前执行行
     */
    protected int currentRowNumber;

    /**
     * 当前执行行内容（内容可能会被替换为临时变量）
     */
    protected String currentRow;

    /**
     * 整个计算表达式内容
     */
    public String content;

    public void compile(String content) throws Exception {
    }
    public void compile() throws Exception {
        compile(this.content);
    }

    protected BufferedReader getContentBufferedReader () {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes(Charset.forName(Constants.CHARSET_NAME))), Charset.forName(Constants.CHARSET_NAME)));
    }

    /**
     * 检验表达式内容
     *  内容中可能存在系统变量，需要将系统变量设置进SYSTEM_VARIABLE，以便判断是否处理
     * @author WangLu
     * @date 2019/3/19 10:20
     */
    public void verifyContent () throws Exception {
        this.isVerify = true;
        throwException(StringUtils.isEmpty(this.content), "内容不能为空");
        this.compile(this.content);
        this.isVerify = false;
        this.currentRowNumber = 0;
        this.currentRow = Constants.EMPTY;
        // 在为非验证模式的情况下执行一遍，判断逻辑条件是否完整
        this.compile(this.content);
        // 验证三个变量
        for (String variable : Constants.METER_MUST_VARIABLE) {
            if (variable.equals(Constants.METER_MUST_VARIABLE[2])
                    || variable.equals(Constants.METER_MUST_VARIABLE[3])) {
                continue;
            }
            if (!VARIABLE_POOL.containsKey(variable)) {
                throwExceptionNotShowRow("计算公式必须存在[分摊用量]和[分摊金额]两个数值变量!");
            }
            if (Constants.DEFAULT_VARIABLE_POOL_VALUE.equals(VARIABLE_POOL.get(variable).getValue())) {
                throwExceptionNotShowRow("参数[分摊用量]和[分摊金额]表达式结束前必须有赋值!", variable);
            }
        }
    }

    /**
     * 将一些字符串（单引号或双引号包裹的字符串）替换为临时变量放入变量池
     *  因为字符串中会出现转义的单引号或双引号，所以在匹配前需要将其替换为特殊符号然后进行匹配
     * @author WangLu
     * @param
     * @return
     * @date 2019/3/17 13:41
     */
    public String stringVariableReplace (String rowStr) {
        rowStr = rowStr.replace(Constants.SLASH_DOUBLE, Constants.REPLACE_DOUBLE).replace(Constants.SLASH_SINGLE, Constants.REPLACE_SINGLE);
        Matcher matcher = RegexHelper.stringVariableRegex.matcher(rowStr);
        String temporary_name;
        while (matcher.find()) {
            temporary_name = matcher.group("str").replace(Constants.REPLACE_DOUBLE, Constants.SLASH_DOUBLE).replace(Constants.REPLACE_SINGLE, Constants.SLASH_SINGLE);
            temporary_name = setStringVariable(temporary_name);
            rowStr = rowStr.replace(matcher.group(0), temporary_name);
            // RegexHelper.calculateRegex.matcher(rowStr);
            loggerPrintNotReplaceTemporaryValue(">>>>>>>>>>>>>>>>>>>>字符串【{}】替换为临时变量【{}】", false, matcher.group("str"), temporary_name);
            matcher = RegexHelper.stringVariableRegex.matcher(rowStr);
        }
        throwException(rowStr.contains(Constants.SIGN_SINGLE)
                || rowStr.contains(Constants.SIGN_DOUBLE)
                || rowStr.contains(Constants.REPLACE_DOUBLE)
                || rowStr.contains(Constants.REPLACE_SINGLE), "无效或意外未关闭的字符串定义");
        //  .replace(Constants.REPLACE_DOUBLE, Constants.SLASH_DOUBLE).replace(Constants.REPLACE_SINGLE, Constants.SLASH_SINGLE)
        return rowStr;
    }

    Integer STRING_SIGN_NUMBER = 0;

    /**
     * 设置String变量，并返回key值
     * @author WangLu
     * @param content 变量值
     * @return 存储变量的key
     * @date 2019/3/17 14:28
     * @desc
     */
    public String setStringVariable(String content) {
        String keyName;
        try {
            if (RegexHelper.simpleDateRegex.matcher(content).matches()) {
                Date date = DateUtils.parseDate(content, Constants.DEFAULT_DATE_FORMAT, Constants.DEFAULT_DATETIME_FORMAT);
                content = String.valueOf(date.getTime());
            }
        } catch (ParseException e) {
            // 如果字符串不是时间类型，那就不用管
            throwException("填写时间有误[{0}]", content);
        }
        synchronized (STRING_SIGN_NUMBER) {
            keyName = Constants.STRING_VARIABLE_START_SIGN + (STRING_SIGN_NUMBER++) + Constants.STRING_VARIABLE_END_SIGN;
            VARIABLE_POOL.put(keyName, new VariateDto(keyName, content, VariateTypeEnum.STRING.getValue(), true));
        }
        return keyName;
    }

    /**
     * 得到编译语句中的系统变量
     * @author WangLu
     * @return
     * @date 2019/3/18 17:54
     */
    public Set<String> getContentSystemVariableKey () throws Exception {
        Set<String> systemVariableKey = new HashSet<>();
        if (null == SYSTEM_VARIABLE) {
            return systemVariableKey;
        }
        Pattern systemRegex;
        for (String key : SYSTEM_VARIABLE.keySet()) {
            // 匹配纯粹的系统变量，排除包含系统变量名称的自定义变量
            systemRegex = Pattern.compile("[^A-Za-z0-9_\\u4e00-\\u9fa5](" + key + ")[^A-Za-z0-9_\\u4e00-\\u9fa5]");
            if (systemRegex.matcher(this.content).find()) {
                systemVariableKey.add(key);
            }
        }
        return systemVariableKey;
    }

    private static Set<String> FUN_SETS = null;

    /**
     * 获取当前表达式中的函数
     * @return
     * @throws Exception
     */
    public Set<String> getFunctionList () throws Exception {
        if (null != FUN_SETS) {
            return FUN_SETS;
        }
        FUN_SETS = Sets.newHashSet();
        BufferedReader br = this.getContentBufferedReader();
        String line, funSign;
        while ((line = br.readLine()) != null) {
            Matcher matcher = RegexHelper.calculateRegex.matcher(line);
            while (matcher.find()) {
                // 匹配函数头
                funSign = matcher.group(1);
                if (!StringUtils.isEmpty(funSign)) {
                    FUN_SETS.add(funSign);
                }
                line = line.replace(matcher.group(0), Constants.EMPTY);
                matcher = RegexHelper.calculateRegex.matcher(line);
            }
        }
        return FUN_SETS;
    }

    /**
     * 设置变量值
     * @author WangLu
     * @param key 变量key
     * @param value 变量value
     * @return
     * @date 2019/3/18 15:06
     */
    protected void setVariablePool(String key, Object value) {
        throwException(null == value, "变量【{0}】赋值null,处理错误", key);
        Matcher matcher = RegexHelper.numberRegex.matcher(value.toString().trim());
        throwException(!matcher.find(), "变量【{0}】赋值【{1}】无效的赋值操作,只允许处理数字", key, value);
        // 接口过滤
        if (null  != filterSetVariateInterface) {
            value = filterSetVariateInterface.filterVariateValue(key, value);
        }
        if (VARIABLE_POOL.containsKey(key)) {
            VARIABLE_POOL.get(key).setValue(value);
        } else {
            VARIABLE_POOL.put(key, new VariateDto(key, value));
        }
    }

    /**
     * 获取变量池或系统变量中的值
     * 如果变量池中没有此值返回key
     * @author WangLu
     * @param key 需要获取的key值
     * @param expression 来源表达式，用于异常提示
     * @return
     * @date 2019/3/17 12:13
     */
    public Object getVariableValueByPoolOrSystem(String key, String expression) {
        Object value = null;
        if (VARIABLE_POOL.containsKey(key)) {
            value = VARIABLE_POOL.get(key).getValue();
            throwException(key.equals(Constants.DEFAULT_VARIABLE_POOL_VALUE) && null != expression, "表达式【{0}】含有未初始化变量,不允许计算操作", expression);
            // 接口过滤
            if (null  != filterGetVariateInterface) {
                value = filterGetVariateInterface.filterVariateValue(key, value, false);
            }
        }
        else if (SYSTEM_VARIABLE.containsKey(key)) {
            value = SYSTEM_VARIABLE.get(key);
            // 接口过滤
            if (null  != filterGetVariateInterface) {
                value = filterGetVariateInterface.filterVariateValue(key, value, true);
            }
        }
        return null == value ? key : value;
    }

    public Object getVariableValueByPoolOrSystem(String key) {
        return getVariableValueByPoolOrSystem(key, null);
    }

    /*public Set<String> getContentSystemVariableKey () throws Exception {
        Set<String> systemVariableKey = new HashSet<>();
        if (null == SYSTEM_VARIABLE) {
            return systemVariableKey;
        }
        /// 1.8的操作
        // systemVariableKey.addAll(SYSTEM_VARIABLE.keySet().stream().filter(key -> this.content.contains(key)).collect(Collectors.toList()));
        for (String key : SYSTEM_VARIABLE.keySet()) {
            if (this.content.contains(key)) {
                systemVariableKey.add(key);
            }
        }
        return systemVariableKey;
    }*/
    /**
     * 用于统一控制日志输出
     * @param msg
     * @param arguments
     */
    protected void loggerPrint (String msg, Object... arguments) {
        loggerPrintNotReplaceTemporaryValue(msg, true, arguments);
    }

    protected void loggerPrintNotReplaceTemporaryValue (String msg, boolean replaceTemporary, Object... arguments) {
        if (replaceTemporary && null != arguments && arguments.length > 0) {
            // 将字符串的临时变量替换为真实值打出日志
            String arg;
            for (Map.Entry<String, VariateDto> entry : VARIABLE_POOL.entrySet()) {
                for (int i = 0, len = arguments.length; i < len; i++) {
                    arg = arguments[i].toString();
                    if (arg.contains(entry.getKey()) && entry.getKey().contains(Constants.STRING_VARIABLE_START_SIGN)) {
                        arguments[i] = arg.replace(entry.getKey(), Constants.SIGN_DOUBLE + entry.getValue().getValue().toString() + Constants.SIGN_DOUBLE);
                    }

                }
            }
        }
        logger.info(msg, arguments);
    }

    public void throwExceptionNotShowRow(String format, Object... arguments) {
        if (StringUtils.isEmpty(format)) {
            throw new RuntimeException("未确认的异常抛出！");
        }
        throw new CalculateException(MessageFormat.format(format, arguments));
    }

    public void throwException(String format, Object... arguments) {
        if (StringUtils.isEmpty(format)) {
            throw new RuntimeException("未确认的异常抛出！");
        }
        throwException(true, format, arguments);
    }

    public void throwException(boolean trueThrow, String format, Object... arguments) {
        if (trueThrow) {
            throw new CalculateException(MessageFormat.format("第 " + this.currentRowNumber + " 行," + format, arguments));
        }
    }


    /**
     * 转换bool，除0和false以外都返回true
     * @author WangLu
     * @param val 判断值
     * @return
     * @date 2019/3/18 11:54
     * @desc
     */
    public boolean judgeBoolean (Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return false;
        }
        String content = val.toString();
        /**
         * 去掉两边都有括号包裹的情况
         */
        while (content.startsWith(Constants.BRACKET_START) && content.endsWith(Constants.BRACKET_END)) {
            content = content.substring(1, content.length() - 1);
        }
        boolean matches = RegexHelper.booleanVariableRegex.matcher(content).matches();
        throwException(!matches, "条件处理类型错误无效的bool类型转换或未关闭的括号表达式,请检查表达式是否正确,error:【{0}】", val);
        /**
         * 判断有多个取反的
         * 判断!!!false的情况，如果!有且是!的数量为奇数那么就是取相反
         */
        boolean opposite = content.toString().split(Constants.SIGN_NOT).length % 2 == 0 ? true : false;
        content = content.toString().replace(Constants.SIGN_NOT, Constants.EMPTY);
        boolean result = !content.equals(Constants.ZERO) && !content.equalsIgnoreCase(Constants.FALSE);
        return opposite ? !result : result;
    }

    /**
     * 判断两个值是否相等
     * 正则匹配否否是数字，如果是转化为bigDecimal进行数值比较，如果转换失败，直接用String的equal比较
     * @param one
     * @param two
     * @return
     */
    public static boolean equalCalculate (String one, String two) {
        try {
            if (RegexHelper.numberRegex.matcher(one).matches() && RegexHelper.numberRegex.matcher(two).matches() ) {
                BigDecimal bigOne = convertBigDecimal(one);
                BigDecimal bigTwo = convertBigDecimal(two);
                return bigOne.compareTo(bigTwo) == 0;
            } else {
                return one.equals(two);
            }
        } catch (NumberFormatException e) {
            return one.equals(two);
        }
    }

    /**
     * 加操作
     * @param one
     * @param two
     * @return
     */
    public static String addCalculate (Object one, Object two) {
        BigDecimal bigOne = convertBigDecimal(one);
        BigDecimal bigTwo = convertBigDecimal(two);
        return bigOne.add(bigTwo).stripTrailingZeros().toPlainString();
    }

    /**
     * 减操作
     * @param one
     * @param two
     * @return
     */
    public static String subtractCalculate (Object one, Object two) {
        BigDecimal bigOne = convertBigDecimal(one);
        BigDecimal bigTwo = convertBigDecimal(two);
        return bigOne.subtract(bigTwo).stripTrailingZeros().toPlainString();
    }

    /**
     * 求余操作
     * @param one
     * @param two
     * @return
     */
    public static String remainderCalculate (Object one, Object two) {
        BigDecimal bigOne = convertBigDecimal(one);
        BigDecimal bigTwo = convertBigDecimal(two);
        // 不允许返回止数(0E-8)的情况，这样会导致后续的计算错误问题
        return bigOne.remainder(bigTwo).stripTrailingZeros().toPlainString();
    }

    /**
     * 乘操作
     * @param one
     * @param two
     * @return
     */
    public static String multiplytCalculate (Object one, Object two) {
        BigDecimal bigOne = convertBigDecimal(one);
        BigDecimal bigTwo = convertBigDecimal(two);
        // 不允许返回止数(0E-8)的情况，这样会导致后续的计算错误问题
        return bigOne.multiply(bigTwo).stripTrailingZeros().toPlainString();
    }

    /**
     * 除操作
     * @param one
     * @param two
     * @return
     */
    public String divideCalculate (Object one, Object two) {
        BigDecimal bigOne = convertBigDecimal(one);
        BigDecimal bigTwo = convertBigDecimal(two);
        try {
            // 不允许返回止数(0E-8)的情况，这样会导致后续的计算错误问题
            return bigOne.divide(bigTwo).stripTrailingZeros().toPlainString();
        } catch (ArithmeticException e) {
            /**
             * 取一个最大15 位的余数
             * 否则如果除不尽时，会抛出异常 Non-terminating decimal expansion; no exact representable decimal result.
             */
            return bigOne.divide(bigTwo, 15, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        }
    }

    /**
     * 转化为BigDecimal，如果转换失败返回0
     * @param str
     * @return
     */
    public static BigDecimal convertBigDecimal(Object str) {
        try {
            BigDecimal decimal = new BigDecimal(str.toString());
            return decimal;
        } catch (NumberFormatException e) {
            throw new NumberFormatException(str.toString());
        }
        /*try {
            BigDecimal decimal = new BigDecimal(str.toString());
            return decimal;
        } catch (NumberFormatException e) {
            throwException("处理错误的数据【{0}】,error:{1}", str, e.getMessage());
        }
        return BigDecimal.ZERO;*/
    }

    static final BigDecimal TWO_BIGDECIMAL = new BigDecimal("2");

    static final BigDecimal MORE_BIGDECIMAL = new BigDecimal("0.0000000000000000000001");

    /**
     * 开平方根
     * @param num
     * @return
     */
    public static BigDecimal sqrt(BigDecimal num) {
        if(num.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal x = num.divide(TWO_BIGDECIMAL, MathContext.DECIMAL128);
        while(x.subtract(x = sqrtIteration(x, num)).abs().compareTo(MORE_BIGDECIMAL) > 0){

        }
        return x;
    }
    private static BigDecimal sqrtIteration(BigDecimal x, BigDecimal n) {
        return x.add(n.divide(x, MathContext.DECIMAL128)).divide(TWO_BIGDECIMAL, MathContext.DECIMAL128);
    }
}
