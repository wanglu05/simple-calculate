package calculate;


import calculate.enums.*;
import constant.Constants;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计量表计算类
 *
 * @author wanglu
 * @create 2019-03-18 15:15
 **/
public class MeterCalculate extends AbstractCalculate{

    public MeterCalculate () {

    }

    public MeterCalculate (String content) {
        this(content, null);
    }

    public MeterCalculate (String content, HashMap<String, Object> systemVariable) {
        if (null != systemVariable) {
            SYSTEM_VARIABLE = systemVariable;
        }
        this.content = content;
    }


    /**
     * 执行编译方法
     * @author WangLu
     * @return
     * @date 2019/3/18 15:43
     */
    @Override
    public void compile (String content) throws Exception {
        super.compile(content);
        try {
            throwException(null == this.content, "编译内容为空");
            loggerPrint(">>>>>{}计算开始Start", this.isVerify ? "验证" : Constants.EMPTY);
            long timeSpan = System.currentTimeMillis();
            BufferedReader br = this.getContentBufferedReader();
            String line;
            /**
             * condition 用于如果的条件判断分为3个值
             * 0 初始状态，1判断为真，-1判断为假
             */
            int condition = 0;
            // 第一次的如果判读单
            boolean firstIf = true;
            /**
             * 如果当前条件有多层，如果..否则如果..否则如果..否则..如果完
             * 当前层执行有一个为true，那么当前层的所有行获取此值都是true，在[如果完]后，将设置为false
             */
            boolean currentConditionResult = false;
            while ((line = br.readLine()) != null) {
                this.currentRowNumber++;
                this.currentRow = line;
                line = Constants.noBreakSpaceToSpace(line).trim();
                if (line.length() <= 0) {
                    continue;
                }
                // 注释跳过
                if (line.startsWith(Constants.ANNOTATION)) {
                    continue;
                }
                // 获取定义的变量
                if (line.startsWith(Constants.VARIABLE_HEADER)) {
                    initDefaultPoolVariable(line);
                    continue;
                }
                // 如果是验证操作
                if (this.isVerify) {
                    this.verifyRow(line);
                    continue;
                }
                // [否则如果]时，如果以上判断为真，则伺候的语句不执行
                if (currentConditionResult && line.startsWith(Constants.CONDITION_HEADER_GO_OR)) {
                    condition = -1;
                    continue;
                }
                // 如果判断条件是假，到‘否则’此处下面语句需要执行
                if (line.startsWith(Constants.CONDITION_HEADER_OR) && !line.startsWith(Constants.CONDITION_HEADER_GO_OR)) {
                    if (condition == 1) {
                        // 调整为-1，不执行否则后面的语句
                        condition = -1;
                        continue;
                    }
                    if (currentConditionResult) {
                        continue;
                    }
                    // 调整为1，执行否则下面的语句
                    condition = 1;
                    continue;
                }
                // 处理掉行中的所有空格
                line = line.replaceAll(Constants.REGEX_BLANK, Constants.EMPTY);
                // 字符串的值替换
                line = stringVariableReplace(line);
                if (line.startsWith(Constants.CONDITION_HEADER_FINISH)) {
                    throwException(!line.equals(Constants.CONDITION_HEADER_FINISH) || condition == 0, "条件行【{0}】终止条件异常操作", this.currentRow);
                    condition = 0;
                    currentConditionResult = false;
                    continue;
                }
                // 条件表达式
                if (line.startsWith(Constants.CONDITION_HEADER)
                        || (condition == -1 && line.startsWith(Constants.CONDITION_HEADER_GO_OR))) {
                    throwException(line.startsWith(Constants.CONDITION_HEADER) && condition != 0, "条件行【{0}】操作之前未收到终止操作", this.currentRow);
                    // 当前如果表达式，判断值condition不为0，说明上一个执行条件未结束
                    throwException(!firstIf && condition != 0 && line.startsWith(Constants.CONDITION_HEADER), "当前语句【{0}】之上有未结束的条件", this.currentRow);
                    // 返回true说明条件为真，否则为假
                    currentConditionResult = conditionCalculate(line);
                    condition = currentConditionResult ? 1 : -1;
                    firstIf = false;
                    continue;
                }
                // 条件判断是假，不执行当前行
                if (condition == -1) {
                    continue;
                }
                // 赋值操作
                if (line.contains(Constants.EQUAL) && !line.contains(Constants.CONDITION_HEADER)) {
                    setVariablePool(line);
                    continue;
                }
                throwException("无法识别的行【{0}】", this.currentRow);
            }
            loggerPrint(">>>>>计算结束End,总耗时：{}ms", System.currentTimeMillis() - timeSpan);
            throwException(condition != 0, "条件语句无终止操作或终止异常");
        } catch (NumberFormatException e) {
            throwException("【{0}】处理时,数据转换异常或无法识别的变量,error:{1}", this.currentRow, e.getMessage());
        }
    }

    /**
     * 验证操作调用
     * @param line
     */
    private void verifyRow (String line) {
        if (line.equals(Constants.CONDITION_HEADER_OR) || line.equals(Constants.CONDITION_HEADER_FINISH)) {
            return;
        }
        // 字符串的值替换
        line = stringVariableReplace(line);
        // 处理掉行中的所有空格
        line = line.replaceAll(Constants.REGEX_BLANK, Constants.EMPTY);
        Matcher matcher = RegexHelper.chineseSymbols.matcher(line);
        throwException(matcher.find(), "不允许包含特殊符号以及中文下的符号和括号");
        // 处理掉行中的所有空格
        line = line.replaceAll(Constants.REGEX_BLANK, Constants.EMPTY);
        if (line.startsWith(Constants.CONDITION_HEADER)
                || line.startsWith(Constants.CONDITION_HEADER_OR)
                || line.startsWith(Constants.CONDITION_HEADER_GO_OR)) {
            conditionCalculate(line);
        }
        else if (line.contains(Constants.EQUAL)) {
            setVariablePool(line);
        } else {
            throwException("无法识别的行【{0}】", this.currentRow);
        }
    }


    /**
     * 系统变量的赋值和复杂情况的赋值
     * @author WangLu
     * @param  lineStr 当前操作行
     * @return
     * @date 2019/3/15 15:58
     */
    private void setVariablePool (String lineStr) {
        Matcher matcher = RegexHelper.variableValueRegex.matcher(lineStr);
        if (!matcher.find()) {
            throwException("【{0}】定义表达式错误有问题！", lineStr);
        }
        String varKey = matcher.group(1).trim();
        if (!VARIABLE_POOL.containsKey(varKey)) {
            throwException("此变量[{0}]未定义", varKey);
        }
        loggerPrint(">>>>>>>>>>变量【{}】赋值操作", varKey);
        String expression = matcher.group(2).trim();
        // 可能会涉及到多层计算
        expression = multilayerCalculate(expression);
        setVariablePool(varKey, expression);
        loggerPrint(">>>>>>>>>>>>>>>>变量【{}】赋值【{}】", varKey, expression);
    }

    /**
     * 条件判断操作
     *  如果  ..=|==.. 则?
     * @author WangLu
     * @param
     * @return
     * @date 2019/3/17 0:34
     * @desc
     */
    public boolean conditionCalculate (String expressionContent) {
        Matcher matcher = RegexHelper.conditionExpressionRegex.matcher(expressionContent);
        if (!matcher.find()) {
            throwException("【{0}】定义条件判断语法错误！", this.currentRow);
        }
        String expression = matcher.group("content").trim();
        // 匹配完整的表达式情况，可能出现  true|false==(!(判断表达式))==true|false
        Matcher fullMatcher = RegexHelper.expressionFormulaRegex.matcher(expression);
        if (!fullMatcher.find()) {
            throwException("【{0}】定义条件判断公式错误！", expression);
        }
        String content = fullMatcher.group("content").trim();
        String contentExt;
        /**
         * 将所有判断和函数处理完成
         */
        for (;;) {
            // 将条件语句中的函数和算法处理完成
            contentExt = multilayerCalculate(content, true);
            contentExt = conditionCalculateByExpression(contentExt);
            if (contentExt.equals(content)) {
                break;
            }
            content = contentExt;
        }
        boolean result = judgeBoolean(content);
        if (null != fullMatcher.group("bool") || null != fullMatcher.group("boolEnd")) {
            String bool = null == fullMatcher.group("bool")?fullMatcher.group("boolEnd"):fullMatcher.group("bool");
            String sign = null == fullMatcher.group("sign")?fullMatcher.group("signEnd"):fullMatcher.group("sign");
            boolean _true = bool.equalsIgnoreCase(Constants.TRUE);
            // 判断 ==情况
            result = sign.equals(ConditionEnum.EE.getName()) ? _true == result :_true != result;
        }
        loggerPrint(">>>>>>>>>>条件表达式【{}】判断为【{}】", expressionContent, result);
        return result;
    }

    /**
     * 条件计算
     * @param exp
     * @return 返回true或false
     */
    private String conditionCalculateByExpression (String exp) {
        // 按表达式先后顺序执行，优先处理大于小于，再处理且操作，最后处理或操作
        exp = executeConditionCalculateByExpression(exp, RegexHelper.conditionExpressionFormulaRegex);
        exp = executeConditionCalculateByExpression(exp, RegexHelper.conditionExpressionAndRegex);
        exp = executeConditionCalculateByExpression(exp, RegexHelper.conditionExpressionOrRegex);
        return exp;
    }

    private String executeConditionCalculateByExpression (String exp, Pattern regex) {
        Matcher matcher = regex.matcher(exp);
        String result;
        while (matcher.find()) {
            result = conditionCalculateBySign(matcher.group("sign"), calculateVariableSplit(matcher.group("left")), calculateVariableSplit(matcher.group("right"))) ? Constants.TRUE : Constants.FALSE;
            exp = exp.replace(matcher.group(0), result);
            matcher = regex.matcher(exp);
        }
        return exp;
    }

    /**
     * 条件计算
     * @author WangLu
     * @return
     * @date 2019/3/19 19:37
     */
    private boolean conditionCalculateBySign (String sign, String exp_one, String exp_two) {
        boolean result = false;
        if (sign.equals(ConditionEnum.EE.getName()) || sign.equals(ConditionEnum.NE.getName())) {
            if (exp_two.equalsIgnoreCase(Constants.TRUE) || exp_two.equalsIgnoreCase(Constants.FALSE)) {
                // 如果条件判断是true或者false的判断，那么直接转化为bool判断
                result = judgeBoolean(exp_one) == judgeBoolean(exp_two);
            } else {
                // 判断两边结果是否一致
                result = equalCalculate(exp_one, exp_two);
            }
            if (sign.equals(ConditionEnum.NE.getName())) {
                result = !result;
            }
        } else if (sign.equals(ConditionEnum.GE.getName()) || sign.equals(ConditionEnum.LE.getName())) {
            int compareTo = convertBigDecimal(exp_one).compareTo(convertBigDecimal(exp_two));
            result = compareTo >= 0;
            // 如果是相等就不用取反
            if (sign.equals(ConditionEnum.LE.getName()) && compareTo != 0) {
                result = !result;
            }
        } else if (sign.equals(ConditionEnum.GT.getName()) || sign.equals(ConditionEnum.LT.getName())) {
            int compareTo = convertBigDecimal(exp_one).compareTo(convertBigDecimal(exp_two));
            // 相等情况直接返回false
            if (compareTo == 0) {
                return false;
            }
            result = compareTo > 0;
            if (sign.equals(ConditionEnum.LT.getName())) {
                result = !result;
            }
        } else if (sign.equals(ConditionEnum.AND.getName())) {
            result = judgeBoolean(exp_one) && judgeBoolean(exp_two);
        } else if (sign.equals(ConditionEnum.OR.getName())) {
            result = judgeBoolean(exp_one) || judgeBoolean(exp_two);
        }
        else {
            throwException("无效的表达式符号【{0}】", sign);
        }
        return result;
    }


    public String multilayerCalculate (String expression) {
        return multilayerCalculate(expression, false);
    }

    /**
     * 多层的计算
     *  所有计算优先处理括号中的表达式，然后
     * @author WangLu
     * @param expression 计算表达式
     * @param isCondition 是否是条件判断表达式
     * @date 2019/3/16 1:51
     */
    public String multilayerCalculate (String expression, Boolean isCondition) {
        Matcher matcher = RegexHelper.calculateRegex.matcher(expression);
        String funSign, matchCalculate, calculateValue;
        // 循环匹配
        while (matcher.find()) {
            // 匹配函数头
            funSign = matcher.group(1);
            // 匹配上的表达式,此匹配的表达式前后会带'()'
            matchCalculate = matcher.group(2);
            if (!StringUtils.isEmpty(funSign)) {
                // 如果匹配函数头有值，那么执行函数
                calculateValue = functionCalculateFactory(funSign, matchCalculate.replace(Constants.BRACKET_START, Constants.EMPTY).replace(Constants.BRACKET_END, Constants.EMPTY)).toString();
                loggerPrint(">>>>>>>>>>>>>>>>>>>>函数【{}】得到值【{}】", matcher.group(0), calculateValue);
            } else {
                // 计算表达式得到的值
                calculateValue = calculateVariableSplit(matchCalculate.replace(Constants.BRACKET_START, Constants.EMPTY).replace(Constants.BRACKET_END, Constants.EMPTY));
                // 如果是条件判断，那么保留括号替代
                if (isCondition) {
                    // 执行一次条件运算
                    calculateValue = conditionCalculateByExpression(calculateValue);
                }
            }
            expression = expression.replace(matcher.group(0), calculateValue);
            matcher = RegexHelper.calculateRegex.matcher(expression);
        }
        // 如果是条件判断，最后函数由条件主方法去处理
        if (isCondition) {
            return expression;
        }
        // 如果执行完了，还有括号，那说明就有可能存在错误的函数表达式无法匹配上
        if (expression.contains(Constants.BRACKET_START)) {
            throwException("执行语句{0}可能包含错误的函数或非法表达式", expression);
        }
        // 处理完后只剩函数，需要计算一遍
        return calculateVariableSplit(expression);
    }

    /**
     * 函数处理工厂，分函数处理
     * @author WangLu
     * @param
     * @return
     * @date 2019/3/16 20:04
     */
    public Object functionCalculateFactory (String funSign, String funContent) {
        String[] params = funContent.split(Constants.COMMA);
        String calculateValue;
        // 为了避免函数中又出现表达式，做一遍表达式计算
        for (int i = 0; i < params.length; i++) {
            // 当前参数可能会是一个表达式
            calculateValue = calculateVariableSplit(params[i]);
            funContent = funContent.replace(params[i], calculateValue);
            params[i] = calculateValue;
        }
        try {
            FuncEnum funcEnum = FuncEnum.getFuncEnumByName(funSign.toUpperCase());
            throwException(null == funcEnum, "无效的函数[{0}]", funSign);
            if (null != filterFunctionInterface) {
                return filterFunctionInterface.filterFunction(funcEnum, params);
            }
            switch (funcEnum) {
                case ROUND:
                    throwException(params.length != 2, Constants.FUN_EXCEPTION, funSign);
                    int two = Integer.parseInt(params[1]);
                    return convertBigDecimal(params[0]).setScale(two, BigDecimal.ROUND_HALF_UP);
                case TROWC:
                    throwException(params.length != 1, Constants.FUN_EXCEPTION, funSign);
                    return convertBigDecimal(params[0]).setScale(0, BigDecimal.ROUND_DOWN);
                case INT:
                    throwException(params.length != 1, Constants.FUN_EXCEPTION, funSign);
                    BigDecimal decimal = convertBigDecimal(params[0]);
                    BigDecimal decimalResult = decimal.setScale(0, BigDecimal.ROUND_DOWN);
                    // 如果是负数，并且有小数位数非0，那么转换整数后需要-1，eg:INT(-43.2) = -44
                    if (decimalResult.compareTo(decimal) > 0) {
                        decimalResult = decimalResult.subtract(BigDecimal.ONE);
                    }
                    return decimalResult;
                case ABS:
                    throwException(params.length != 1, Constants.FUN_EXCEPTION, funSign);
                    return convertBigDecimal(params[0]).abs();
                case MOD:
                    throwException(params.length != 2, Constants.FUN_EXCEPTION, funSign);
                    BigDecimal bigTwo = convertBigDecimal(params[1]);
                    return convertBigDecimal(params[0]).divideAndRemainder(bigTwo)[1];
                case POWER:
                    throwException(params.length != 2, Constants.FUN_EXCEPTION, funSign);
                    int intPow = Integer.parseInt(params[1]);
                    return convertBigDecimal(params[0]).pow(intPow);
                case SQRT:
                    throwException(params.length != 1, Constants.FUN_EXCEPTION, funSign);
                    return sqrt(convertBigDecimal(params[0]));
                case QUOTIENT:
                    throwException(params.length != 2, Constants.FUN_EXCEPTION, funSign);
                    BigDecimal bigQuo = convertBigDecimal(params[1]);
                    return convertBigDecimal(params[0]).divide(bigQuo).setScale(0, BigDecimal.ROUND_DOWN);
                case MAX:
                    throwException(params.length < 2, Constants.FUN_EXCEPTION, funSign);
                    BigDecimal bigMax = convertBigDecimal(params[0]);
                    for (int i = 1, len = params.length; i < len; i++) {
                        bigMax = bigMax.max(convertBigDecimal(params[i]));
                    }
                    return bigMax;
                case MIN:
                    throwException(params.length < 2, Constants.FUN_EXCEPTION, funSign);
                    BigDecimal bigMin = convertBigDecimal(params[0]);
                    for (int i = 1, len = params.length; i < len; i++) {
                        bigMin = bigMin.min(convertBigDecimal(params[i]));
                    }
                    return bigMin;
                case CONTAIN:
                    throwException(params.length > 2, Constants.FUN_EXCEPTION, funSign);
                case IN:
                    throwException(params.length < 2, Constants.FUN_EXCEPTION, funSign);
                    boolean flag;
                    for (int i = 1; i < params.length; i++) {
                        flag = params[0].contains(params[i]);
                        if (!flag) {
                            return 0;
                        }
                    }
                    return 1;
                case NOT:
                    return !judgeBoolean(conditionCalculateByExpression(funContent)) ? Constants.TRUE : Constants.FALSE;
                case SUMTYPE:
                    throwException(params.length != 2, Constants.FUN_EXCEPTION, funSign);
                    SumTypeEnum sumType = SumTypeEnum.getSumTypeByCode(Integer.valueOf(params[0]));
                    throwException(null == sumType, Constants.FUN_EXCEPTION, funSign);
                    if (null != filterSumTypeFunctionInterface) {
                        return filterSumTypeFunctionInterface.execute(sumType, params[1]);
                    }
                    return Constants.DEFAULT_INT;
                default:
                    throwException("无效的函数[{0}]", funSign);
            }
        } catch (NumberFormatException e) {
            throwException("处理函数{0}的参数{1}转换异常,error:{2}", funSign, funContent, e.getMessage());
        }
        return funContent;
    }

    /**
     * 计算变量
     * 拆分执行有可能是a+b*c的联合函数，按优先级执行
     * @author WangLu
     * @param
     * @return
     * @date 2019/3/15 17:51
     */
    public String calculateVariableSplit (String expression) {
        // 优先执行乘除计算
        String returnValue = calculateVariableSplitCore(expression, RegexHelper.calculateSignBowMultiplyAndDivide);
        // 然后执行加减计算
        returnValue = calculateVariableSplitCore(returnValue, RegexHelper.calculateSignBowAddAndSubtract);
        if (expression.trim().equals(returnValue) || returnValue.contains(Constants.STRING_VARIABLE_START_SIGN)) {
            returnValue = getVariableValueByPoolOrSystem(returnValue, expression).toString();
            /// 直接通过上面定义的方法取值
            /*if (SYSTEM_VARIABLE.containsKey(returnValue)) {
                returnValue = SYSTEM_VARIABLE.get(returnValue).toString();
                loggerPrint(">>>>>>>>>>>>>>>>>>>>表达式中系统变量【{}】取值【{}】", expression, returnValue);
            }
            // 当前参数可能会是定义变量
            if (VARIABLE_POOL.containsKey(returnValue)) {
                returnValue = VARIABLE_POOL.get(returnValue).getValue().toString();
                loggerPrintNotReplaceTemporaryValue(">>>>>>>>>>>>>>>>>>>>表达式中变量【{}】取值【{}】", false, expression, returnValue);
                throwException(Constants.DEFAULT_VARIABLE_POOL_VALUE.equals(returnValue), "未赋值变量【{}】操作", returnValue);
            }*/
        } else {
            loggerPrint(">>>>>>>>>>>>>>>>执行完成表达式【{}】得到值【{}】", expression, returnValue);
        }
        return returnValue;
    }

    public String calculateVariableSplitCore (String expression, Pattern firstPattern) {
        if (null == firstPattern) {
            firstPattern = RegexHelper.calculateSignBowMultiplyAndDivide;
        }
        expression = expression.trim();
        //loggerPrint(">>>>>>>>>>>>>>拆分执行表达式【{}】", expression);
        // 优先匹配乘除法运算
        Matcher matcher = firstPattern.matcher(expression);
        String matchCalculate, value;
        while (matcher.find()) {
            matchCalculate = matcher.group(1);
            // 处理特殊情况 eg: +-6-32处理成-6-32
            if (expression.startsWith(Constants.SIGN_ADD_SUBTRACT)
                    || expression.startsWith(Constants.SIGN_SUBTRACT_ADD)) {
                expression = Constants.SIGN_SUBTRACT.concat(expression.substring(2, expression.length()));
            }
            // 处理负数的函数情况，eg：-6-32
            if (expression.startsWith(Constants.SIGN_SUBTRACT)) {
                matchCalculate = Constants.SIGN_SUBTRACT.concat(matchCalculate);
            }
            // eg: +6-32
            if (expression.startsWith(Constants.SIGN_ADD)) {
                matchCalculate = Constants.SIGN_ADD.concat(matchCalculate);
            }
            value = calculateVariable(matchCalculate);
            loggerPrint(">>>>>>>>>>>>>>>>>>>>表达式【{}】得到值【{}】", matchCalculate, value);
            expression = expression.replace(matchCalculate, value);
            matcher = firstPattern.matcher(expression);
        }
        return expression;
    }

    /**
     * 计算函数保证传递数据为
     *  a+b或a*b之类的但运算
     * @author WangLu
     * @param
     * @return
     * @date 2019/3/15 23:53
     */
    public String calculateVariable (String expression) {
        Matcher matcher = RegexHelper.calculateSignBow.matcher(expression);
        if (!matcher.find()) {
            throwException("无效的运算表达式【{0}】", expression);
        }
        String left = matcher.group(1);
        String sign = matcher.group(2);
        String right = matcher.group(3);
        // 在取值前判断是否是字符串的+操作
        boolean flag = left.contains(Constants.STRING_VARIABLE_START_SIGN) || right.contains(Constants.STRING_VARIABLE_START_SIGN);
        left = getVariableValueByPoolOrSystem(left, expression).toString();
        right = getVariableValueByPoolOrSystem(right, expression).toString();
        switch (sign) {
            case Constants.SIGN_ADD:
                if (flag) {
                    return setStringVariable(left.concat(right));
                }
                return addCalculate(left, right);
            case Constants.SIGN_SUBTRACT:
                return subtractCalculate(left, right);
            case Constants.SIGN_MULTIPLY:
                return multiplytCalculate(left, right);
            case Constants.SIGN_DIVIDE:
                throwException(convertBigDecimal(right).compareTo(BigDecimal.ZERO) == 0, "计算过程中被除数为0,无效的数据运算");
                return divideCalculate(left, right);
            case Constants.SIGN_REMAINDER:
                // 求余操作
                return remainderCalculate(left, right);
            default:
                throwException("无效的运算符[{0}]", sign);
        }
        return Constants.ZERO;
    }

    /**
     * 清除临时变量
     * @author WangLu
     * @return
     * @date 2019/3/18 16:37
     */
    private void clearTemporaryData() {
        for (Iterator<Map.Entry<String, VariateDto>> it = VARIABLE_POOL.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, VariateDto> item = it.next();
            if (item.getValue().isTemporary()) {
                it.remove();
            }
        }
    }

    /**
     * 得到自定义变量值放入变量池中，并默认初始值DEFAULT_VARIABLE_POOL_VALUE
     * @author WangLu
     * @param lineStr 当前操作行
     * @return
     * @date 2019/3/15 15:27
     */
    public void initDefaultPoolVariable (String lineStr) {
        Matcher matcher = RegexHelper.variableRegex.matcher(lineStr);
        if (!matcher.find()) {
            throwException("无法匹配的变量定义！");
        }
        String key = matcher.group(1).trim();
        boolean flag = RegexHelper.variableNoNormalRegex.matcher(key).find()
                || key.equals(Constants.EMPTY)
                || key.contains(Constants.CONDITION_HEADER)
                || key.contains(Constants.CONDITION_HEADER_GO)
                || key.contains(Constants.CONDITION_HEADER_FINISH)
                || key.contains(Constants.CONDITION_HEADER_OR)
                || key.contains(Constants.VARIABLE_HEADER)
                || key.contains(Constants.STRING_VARIABLE_START_SIGN);
        throwException (flag, "【{0}】变量定义不能包含特殊符号和系统关键语句,只允许数字,字母,汉字,下划线", lineStr);
        throwException (this.SYSTEM_VARIABLE.containsKey(key), "【{0}】变量不允许与系统变量同名", key);
        VARIABLE_POOL.put(key, new VariateDto(key, Constants.DEFAULT_VARIABLE_POOL_VALUE));
    }

    /**
     * 获取 分摊用量
     * @return
     */
    public BigDecimal getShareDosageByVariable () {
        try {
            VariateDto variateDto = this.VARIABLE_POOL.get(Constants.METER_MUST_VARIABLE[0]);
            if (null == variateDto
                    || Constants.DEFAULT_VARIABLE_POOL_VALUE.equals(variateDto.getValue())) {
                return null;
            }
            return convertBigDecimal(variateDto.getValue());
        } catch (NumberFormatException e) {
            throwExceptionNotShowRow("【分摊用量】数据转换错误!");
        }
        return null;
    }

    /**
     * 获取 分摊金额
     * @return
     */
    public BigDecimal getReceivableByVariable () {
        try {
            VariateDto variateDto = this.VARIABLE_POOL.get(Constants.METER_MUST_VARIABLE[1]);
            if (null == variateDto
                    || Constants.DEFAULT_VARIABLE_POOL_VALUE.equals(variateDto.getValue())) {
                return null;
            }
            return convertBigDecimal(variateDto.getValue());
        } catch (NumberFormatException e) {
            throwExceptionNotShowRow("【分摊金额】数据转换错误!");
        }
        return null;
    }

    /**
     * 获取 分摊比列
     * @return
     */
    public BigDecimal getShareProportionByVariable () {
        try {
            VariateDto variateDto = this.VARIABLE_POOL.get(Constants.METER_MUST_VARIABLE[2]);
            if (null == variateDto
                    || Constants.DEFAULT_VARIABLE_POOL_VALUE.equals(variateDto.getValue())) {
                return null;
            }
            return convertBigDecimal(variateDto.getValue());
        } catch (NumberFormatException e) {
            throwExceptionNotShowRow("【分摊比列】数据转换错误!");
        }
        return null;
    }

    /**
     * 获取 分摊单价
     * @return
     */
    public BigDecimal getUnitPriceByVariable () {
        try {
            VariateDto variateDto = this.VARIABLE_POOL.get(Constants.METER_MUST_VARIABLE[3]);
            if (null == variateDto
                    || Constants.DEFAULT_VARIABLE_POOL_VALUE.equals(variateDto.getValue())) {
                return null;
            }
            return convertBigDecimal(variateDto.getValue());
        } catch (NumberFormatException e) {
            throwExceptionNotShowRow("【分摊单价】数据转换错误!");
        }
        return null;
    }
}
