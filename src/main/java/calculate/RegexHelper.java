package calculate;

import calculate.enums.FuncEnum;
import constant.Constants;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则操作帮助类
 *
 * @author wanglu
 * @create 2019-03-18 15:14
 **/
public class RegexHelper {

    /**
     * 自定义参数匹配正则
     */
    static Pattern variableRegex = Pattern.compile("数值\\s+(.+)");

    /**
     * 验证是否包含特殊符号与"[A-Za-z0-9_]"相反
     */
    static Pattern variableNoNormalRegex = Pattern.compile("[^A-Za-z0-9_\\u4e00-\\u9fa5]+");

    /**
     * 匹配括号中的计算表达式或函数表达式
     */
    // static String CORE_REGEX = "({0})?(\\([^\\(!].[^\\(!]*?\\))";
    static String CORE_REGEX = "({0})?(\\([^(!)]*?\\))";


    /**
     * 系统函数
     */
    static HashSet<String> SYSTEM_FUNCTION = new HashSet<String>() {
        {
            for (FuncEnum funcEnum : FuncEnum.values()) {
                add(funcEnum.getName());
            }
            // 初始化函数后，初始化匹配函数的正则文本
            StringBuilder stringBuilder = new StringBuilder();
            for (String key : this) {
                stringBuilder.append(key).append(Constants.VERTICAL_BAR);
                // 同时新增小写的匹配
                stringBuilder.append(key.toLowerCase()).append(Constants.VERTICAL_BAR);
            }
            CORE_REGEX = MessageFormat.format(CORE_REGEX, stringBuilder.substring(0, stringBuilder.length() -1));
        }
    };

    /**
     * 自定义变量的赋值匹配
     * 也用于匹配等号的左右的数据（可能此变量是需要经过一系列计算得出）
     */
    static Pattern variableValueRegex = Pattern.compile("(.*)\\s*\\= *(.*)\\s*");

    /**
     * 匹配字符串值
     * 支持单引号的字符串和双引号的字符串
     */
    static Pattern stringVariableRegex = Pattern.compile("(?<sign>[\"'])(?<str>.+?)\\1");
    // static Pattern stringVariableRegex = Pattern.compile("\"(.+?)\"");

    /**
     *  用于判断true和false取值的异常匹配
     */
    static Pattern booleanVariableRegex = Pattern.compile("^(!*)?(true|false|0|1)$");

    /**
     * 匹配括号中的计算表达式(并过滤匹配函数)
     */
    static Pattern calculateRegex = Pattern.compile(CORE_REGEX); // "(\\([^\\(].[^\\(]*?\\))"


    /**
     * 匹配数字
     * 允许00.01的此类型操作
     */
    static Pattern numberRegex = Pattern.compile("^[+\\-]?[0-9]+([.][0-9]+){0,1}$");

    // static Pattern conditionExpressionRegex = Pattern.compile("(如果|否则如果)\\s*([^\\=]+?)(?:(==|!=|>=|<=|>|<)([^\\=\\!]+))?(\\s*则)");

    /**
     * 条件的匹配
     */
    static Pattern conditionExpressionRegex = Pattern.compile("(如果|否则如果)\\s*(?<content>.+)(\\s*则)$");

    // static Pattern expressionFormulaRegex = Pattern.compile("^((?<bool>true|false)\\s*(?<sign>==|!=))?(?<versa>\\!?)(?<content>.+?)((?<signEnd>==|!=)\\s*(?<boolEnd>true|false))?$"); //, Pattern.CASE_INSENSITIVE

    static Pattern expressionFormulaRegex = Pattern.compile("^((?<bool>true|false)\\s*(?<sign>==|!=))?(?<content>.+?)((?<signEnd>==|!=)\\s*(?<boolEnd>true|false))?$"); //, Pattern.CASE_INSENSITIVE

    // static Pattern conditionExpressionFormulaRegex = Pattern.compile("([^\\\\=]+?)(==|!=|>=|<=|>|<)([^\\\\=\\\\!]+)?");
    static Pattern conditionExpressionFormulaRegex = Pattern.compile("(?<left>[^=&><||()]+?)(?<sign>==|!=|>=|<=|>|<)(?<right>[^=&!><||()]+)?");

    /**
     * 条件运算且
     */
    static Pattern conditionExpressionAndRegex = Pattern.compile("(?<left>[^=&><||()]+?)(?<sign>\\&\\&)(?<right>[^=&!><||()]+)?");

    /**
     * 条件运算或
     */
    static Pattern conditionExpressionOrRegex = Pattern.compile("(?<left>[^=&><||()]+?)(?<sign>\\|\\|)(?<right>[^=&!><||()]+)?");


    /**
     * 匹配计算公式的前后两端
     * 2--2 匹配为 2，-，-2;而非2-，-，2
     */
    static Pattern calculateSignBow = Pattern.compile("(.*[^*/+\\-%])([*/+\\-%]{1})(.*)");

    /**
     * 匹配乘法和除法
     *  与加法分开匹配，因为执行优先级高于加减
     */
    static Pattern calculateSignBowMultiplyAndDivide = Pattern.compile("([^+\\-*/&|<=>%]+[*/%]\\-?[^+\\-*/&|<=>%]+)");

    /**
     * 匹配加法和减法
     */
    static Pattern calculateSignBowAddAndSubtract = Pattern.compile("([^+\\-*/&|<=>!]+[+\\-]\\-?[^+\\-*/&|<=>]+)");

    /**
     * 匹配中文符号
     */
    static Pattern chineseSymbols = Pattern.compile("[，。、‘；’】【（；）！·“”~]");

    /**
     * 匹配时间
     *  此项匹配时间比较简单，没有强校验时间类型，比如大于12月，超过60分钟，只要格式正确，就算日期匹配成功
     */
    static Pattern simpleDateRegex = Pattern.compile("^\\d{4}-[01]?\\d-[0-3]?\\d( +[0-2]\\d:[0-6]\\d:[0-6]\\d)?$");

    public static void main(String[] args) {
        String str = "'的方式\"的收费是打发斯\"蒂芬随\\'碟附\\'送撒地方'";
        str = str.replace("\\'", "@@").replace("\"", "@@");
        Matcher matcher = Pattern.compile("([\"'])(.+?)\\1").matcher(str);
        while (matcher.find()) {
            String b = matcher.group(1);
            String m = matcher.group(2);
            int i = matcher.groupCount();
            m = m.replace("@@", "\\'").replace("@@", "\"");
            str = str.replace("@@", "\\'").replace("@@", "\"").replace(b.concat(m).concat(b), "");
            matcher =  Pattern.compile("([\"'])(.+?)\\1").matcher(str);
        }

    }
}
