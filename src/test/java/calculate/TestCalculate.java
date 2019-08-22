package calculate;

import calculate.enums.*;
import calculate.support.*;
import com.alibaba.fastjson.JSON;
import constant.Constants;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 测试表达式
 *
 * @author wanglu
 * @create 2019-03-25 9:54
 **/
public class TestCalculate {

    static String content = "数值 加压电上次读数\n" +
            "数值 加压电本次读数\n" +
            "数值 加压用电\n" +
            "数值     用水量\n" +
            "用水量=收费标准+1\n" +


            "如果 IN(\"3rwere\",\"r\", \"e\") 则\n"+
            /*"用水量=(4+POWER(5,2)-1)*4-(25/5+5)\n" +
            "如果 !(min(用水量,4)>50)==CONTAIN(\"ymf\",\"f\") 则\n"+
                "用水量=123321233333211\n" +
            "如果完\n"+*/

            "如果 (max(43.3,52,44,0.2,99)>= 99 && !(max(99,33)/3==33))==true 则\n"+
            "   加压用电=88888888\n" +
            "如果完\n" +
            "用水量=3\n" +
            "如果 !!(!(max(43.3,99)-2<=66) || 1==2 && min(用水量,4)>3 || (max(44,33) > 10 &&  7>9)) != true 则\n"+
            //"如果 !(用水量!=(3*2)) || 用水量<3 则\n"+
            "用水量=MOD(5,2)\n" +
            "否则如果 用水量==26 || 1==1 则\n"+
            "   加压用电=MAX(4.0038,4)+2\n" +
            "   用水量=334\n" +
            "否则如果 用水量==28 则\n"+
            "   加压用电=MAX(4.0038,4)+3\n" +
            "   用水量=335\n" +
            "否则如果 1==1 则\n"+
            "   加压用电=MAX(4.0038,4)+36\n" +
            "   用水量=3366\n" +
            "否则\n" +
            "   用水量=QUOTIENT(5,2)\n" +
            "   加压用电=2\n" +
            "如果完\n"+

            "用水量=sqrt(-43.1)\n" +
            "用水量=sqrt(1)\n" +
            "用水量=int(-43.1)\n" +
            "用水量=TROWC(-43.1)\n" +
            "用水量=max(43.3,52,44,0.2,99)\n" +
            "用水量=IN(\"3rwere\",\"r\", \"e\")\n" +
            //"如果 IN(\"3rwere\",\"r\", \"e\")  +  1   == false 则\n"+


            "如果 (max(43.3,52,44,0.2,99)>= \"99\")==true 则\n"+
            "   加压用电=88888888\n" +
            "如果完\n" +
            "如果 房间编号==\"SNG-001-2\"+\"3\" 则\n"+
            "   加压用电=77777777\n" +
            "如果完\n" +
            "如果 IN(\"3rwere\",\"r\", \"e\") 则\n"+
            "   加压用电=99999999999999999\n" +
            "如果完\n" +
            "如果 contain(\"3rwere\",\"r\") != 1 则\n"+
            "   加压用电=66666\n" +
            "如果完\n" +
            "如果 房间编号==\"SNG-001-2\"+\"3\" 则\n"+
            "   加压用电=QUOTIENT(5,2)\n" +
            "如果完\n" +
            "用水量=POWER(5,2)\n" +
            "如果 用水量!=26 则\n"+
            "用水量=MOD(5,2)\n" +
            "否则如果 用水量==25 则\n"+
            "   加压用电=MAX(4.0038,4)+2\n" +
            "   用水量=33\n" +
            "否则\n" +
            "   用水量=QUOTIENT(5,2)\n" +
            "如果完\n" +
            "加压电上次读数=(5.11+2)\n" +
            "用水量=5+ROUND(5.1438+1,3)\n" +
            "加压电本次读数=4.343434333\n" +
            "加压用电=ROUND(((加压电本次读数+5-加压电上次读数*(加压电上次读数+加压电本次读数*(MIN(加压电本次读数,0.5)/2)))-(加压电上次读数+3)*2)*3,2)\n" ;

    //"费用_金额=round(费用_计费行度*round(加压用电*收费标准_单价/用水量,4),1)\n" +
    //"费用_单价=收费标准_单价\n";
    public static void main(String[] args) throws Exception {
        String date = "2019-13-4 23:23:33";
        long nanoTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            RegexHelper.simpleDateRegex.matcher(date).matches();
        }
        System.out.println("=============================================simpleDateRegex："+  (System.nanoTime()-nanoTime));
        nanoTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            DateUtils.parseDate(date, Constants.DEFAULT_DATE_FORMAT, Constants.DEFAULT_DATETIME_FORMAT);
        }
        System.out.println("=============================================parseDate："+  (System.nanoTime()-nanoTime));
        System.out.println("============================================="+  DateUtils.parseDate(date, Constants.DEFAULT_DATE_FORMAT, Constants.DEFAULT_DATETIME_FORMAT  ));
        System.out.println("============================================="+  new LocalDate(new Date()).toDate());
        BigDecimal a = new BigDecimal("3.00000000");
        BigDecimal b = new BigDecimal("1.00000000000000000000");

        System.out.println(b.multiply(a).setScale(15, BigDecimal.ROUND_HALF_UP));//.setScale(6, BigDecimal.ROUND_HALF_UP)
        content ="数值 分摊用量\n" +
                 "数值 分摊金额\n" +
                 "如果  5/2+2*房间表_初始读数%1-(-1-1) > 15 则\n" +
                    "分摊用量 =  公区表_合计用量/分摊户数*0.8\n" +
                 "否则\n" +
                    "分摊用量 =  公区表_合计用量 /分摊户数*1.5\n" +
                 "如果完\n" +
                 "分摊用量 = round(分摊用量, 2)\n" +
                 "分摊金额 = round(分摊用量* 公区表_单价, 2)\n"+
                "分摊金额 = SUMTYPE(6, '3434')\n"+
                "分摊用量 = SUMTYPE(2, 2)\n";

        content = "数值 分摊用量\n" +
                "数值 分摊金额\n" +
                "分摊用量= 公区表_合计用量 / 分摊户数 \n" +
                "分摊用量= 公区表_合计用量 / 分摊户数 \n" +
                "如果  房间表_本次抄表日期 <\"2019-04-29\" 则\n" +
                "  分摊金额=分摊用量* 公区表_单价 *0.88\n" +
                "否则 \n" +
                "  分摊金额= 房间表_应收金额 \n" +
                "如果完\n";
        /*HashMap<String, Object> SYSTEM_VARIABLE = new HashMap<>();
        SYSTEM_VARIABLE.put("费用_金额", 0.5);
        SYSTEM_VARIABLE.put("房间编号", "SNG-001-23");*/
        for (int i = 1; i <= 1; i++) {
            System.out.println("============================================="+i);
            final MeterCalculate meterCalculate = new MeterCalculate(content);
            System.out.println(">>>>>>>>>>>>>>>>"+JSON.toJSONString(meterCalculate.getFunctionList()));
            meterCalculate.filterGetVariateInterface = new FilterGetVariateInterface() {
                @Override
                public Object filterVariateValue(String key, Object value, Boolean isSystem) {
                    BigDecimal shareDosageByVariable = meterCalculate.getShareDosageByVariable();
                    if (key.equals("公区表_单价")) {
                        return "2.0000";
                    }
                    if (key.equals("分摊用量")) {
                        return "0.0000";
                    }
                    return value;
                }
            };
            meterCalculate.filterSumTypeFunctionInterface = new FilterSumTypeFunctionInterface() {
                @Override
                public Object execute(SumTypeEnum sumTypeEnum, String value) {
                    if (SumTypeEnum.BALANCE_STATUS.getCode().equals(sumTypeEnum.getCode())) {
                        return 10;
                    }
                    if (SumTypeEnum.IS_PUBLIC.getCode().equals(sumTypeEnum.getCode())) {
                        return 15;
                    }
                    return null;
                }
            };
            meterCalculate.putSystemVariable("费用_金额", 0.5);
            meterCalculate.putSystemVariable("房间编号", "SNG-001-23");
            // 得到模板中的系统变量
            System.out.println(JSON.toJSONString(meterCalculate.getContentSystemVariableKey()));
            meterCalculate.compile();
            //meterCalculate.verifyContent();
            System.out.println(JSON.toJSONString(meterCalculate.VARIABLE_POOL()));
        }
    }
}
