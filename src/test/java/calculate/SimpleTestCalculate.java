package calculate;

import calculate.enums.SumTypeEnum;
import calculate.support.FilterGetVariateInterface;
import calculate.support.FilterSumTypeFunctionInterface;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试表达式
 *
 * @author wanglu
 * @create 2019-03-25 9:54
 **/
public class SimpleTestCalculate {
    protected static Logger logger = LoggerFactory.getLogger(SimpleTestCalculate.class);

    static String content = "数值 加压电上次读数\n" +
            "数值 加压电本次读数\n" +
            "数值 加压用电\n" +
            "数值     用水量\n" +
            "用水量=收费标准+1\n" +
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

    public static void main(String[] args) throws Exception {
        HashMap<String, Object> systemVariable = Maps.newHashMap();
        systemVariable.put("费用_金额", 0.5);
        systemVariable.put("房间编号", "SNG-001-23");
        MeterCalculate meterCalculate = new MeterCalculate(content, systemVariable);
        meterCalculate.putSystemVariable("收费标准", 66);
        meterCalculate.compile();
        logger.info(">>>>>{}", JSON.toJSONString(meterCalculate.VARIABLE_POOL()));
    }
}
