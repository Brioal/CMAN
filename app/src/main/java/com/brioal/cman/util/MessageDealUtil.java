package com.brioal.cman.util;

import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.interfaces.OnDeviceStatusChangedListener;


/**
 * Created by brioal on 16-10-30.
 * Email : brioal@foxmail.com
 * Gihub : https://github.com/Brioal
 */

public class MessageDealUtil {
    public static boolean judege(String s) {
        KLog.e(s);
        int packInfoSize = Integer.valueOf(s.substring(28, 30), 16);//报文内容长度
        String packInfo = s.substring(32, 32 + (packInfoSize - 2) * 2);//报文内容
        int sum = 0;
        int xrl = 0;
        String sumCounnm = "";
        String xrlCounnm = "";
        KLog.e("PackageInfo" + packInfo);
        KLog.e("PackageInfoSize" + packInfoSize);
        if (!packInfo.isEmpty()) {
            sum = Integer.valueOf(packInfo.substring(0, 2), 16);
            xrl = Integer.valueOf(packInfo.substring(0, 2), 16);
            for (int i = 0; i <= packInfo.length() - 2; i += 2) {
                sum += Integer.valueOf(packInfo.substring(i, i + 2), 16);
                xrl ^= Integer.valueOf(packInfo.substring(i, i + 2), 16);
            }
            sumCounnm = addZeroForNum(Integer.toHexString(sum).toUpperCase(), 2, true);
            xrlCounnm = addZeroForNum(Integer.toHexString(xrl).toUpperCase(), 2, true);
            KLog.e("报文内容计算的亦或和" + xrlCounnm);
            KLog.e("报文内容计算的累加和" + sumCounnm);
            KLog.e("报文内容报文的亦或和" + s.substring(s.length() - 10, s.length() - 8));
            KLog.e("报文内容报文的累加和" + s.substring(s.length() - 8, s.length() - 6));
            if (!xrlCounnm.equals(s.substring(s.length() - 10, s.length() - 8))) {
//                return false;
            }
            if (!sumCounnm.equals(s.substring(s.length() - 8, s.length() - 6))) {
//                return false;
            }
        }
        sum = 0;
        xrl = 0;
        for (int i = 0; i <= s.length() - 8; i += 2) {
//            KLog.d("Str"+s.substring(i, i + 2));
//            KLog.d("Int"+Integer.valueOf(s.substring(i, i + 2), 16));
            sum += Integer.valueOf(s.substring(i, i + 2), 16);
            xrl ^= Integer.valueOf(s.substring(i, i + 2), 16);
        }
        sumCounnm = addZeroForNum(Integer.toHexString(sum).toUpperCase(), 2, true);
        xrlCounnm = addZeroForNum(Integer.toHexString(xrl).toUpperCase(), 2, true);
        KLog.e("总内容计算的累加和" + sumCounnm);
        KLog.e("总内容计算的亦或和" + xrlCounnm);
        KLog.e("总内容报文的累加和" + s.substring(s.length() - 4, s.length() - 2));
        KLog.e("总内容报文的亦或和" + s.substring(s.length() - 6, s.length() - 4));
        if (!xrlCounnm.equals(s.substring(s.length() - 6, s.length() - 4))) {
//            return false;
        }
        if (!sumCounnm.equals(s.substring(s.length() - 4, s.length() - 2))) {
//            return false;
        }
        return true;
    }

    public static String addZeroForNum(String str, int strLength, boolean jump) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);//左补0
                //    sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        } else {
            if (jump) {
                return str.substring(strLen - 2, strLen);
            }
        }

        return str;
    }

    public static void DealMessage(String msg, OnDeviceStatusChangedListener changedListener) {
        if (!judege(msg)) {
            return;
        }
        KLog.e(msg);
        if (msg.length() <= 64) {
            return;
        }
        if (!msg.substring(0, 4).equals("FF10")) {
            return;
        }
        if (!msg.substring(30, 32).equals("10")) {
            return;
        }
        int jiare = Integer.parseInt(msg.substring(33, 34));//加热状态 0->关 1->开
        int chushi = Integer.parseInt(msg.substring(35, 36));//除湿状态 0->关 1->开
        int chuchou = Integer.parseInt(msg.substring(37, 38));//除臭状态 0->关 1->开
        int clience = Integer.parseInt(msg.substring(39, 40));//静音状态 0->关 1->开
        int auto = Integer.parseInt(msg.substring(41, 42));//控制状态 1->自动 2->手动
        int shidu_min = Integer.valueOf(msg.substring(42, 44), 16);//湿度范围 0 -- 100
        int shidu_max = Integer.valueOf(msg.substring(44, 46), 16);//湿度范围 0 -- 100
        int wendu_int = Integer.valueOf(msg.substring(46, 48), 16);//温度范围 -30 -- 50
        int wendu_min = 0;//温度最小值
        int wendu_max = 0;//温度最大值
        if (wendu_int > 100) {
            wendu_min = wendu_int - 256;
        } else {
            wendu_min = wendu_int;
        }
        wendu_int = Integer.valueOf(msg.substring(48, 50), 16);
        if (wendu_int > 100) {
            wendu_max = wendu_int - 256;
        } else {
            wendu_max = wendu_int;
        }
        int qiwei_max = Integer.valueOf(msg.substring(52, 54), 16);//最大气味
        int shuixiang = Integer.parseInt(msg.substring(55, 56));//水箱状态 0->正常 1 -> 异常
        int jiankang = Integer.parseInt(msg.substring(57, 58));//健康状态 0->正常 1->异常

        int currentWendu = Integer.valueOf(msg.substring(60, 62), 16);//当前温度
        int currentShidu = Integer.valueOf(msg.substring(62, 64), 16);//当前湿度
        int currentQiwei = Integer.valueOf(msg.substring(64, 66), 16);//当前气味
        int currentVolume = Integer.valueOf(msg.substring(58, 60), 16);//当前音量
        KLog.e("收到空闲报文");
        KLog.d("当前温度：" + currentWendu + "℃");
        KLog.d("当前湿度：" + currentShidu + "%");
        KLog.d("当前气味：" + currentQiwei);
        KLog.d("当前音量：" + currentVolume);
        KLog.d("当前水箱状态：" + shuixiang);
        KLog.d("当前健康状态：" + jiankang);
        KLog.d("当前加热开关状态：" + jiare);
        KLog.d("当前除湿开关状态：" + chushi);
        KLog.d("当前除臭开关状态：" + chuchou);
        KLog.d("当前控制模式：" + auto);
        KLog.d("当前静音模式：" + clience);
        KLog.d("-----------------------------------");
        KLog.d("温度范围：" + wendu_min + "->" + wendu_max);
        KLog.d("湿度范围：" + shidu_min + "->" + shidu_max);
        KLog.d("气味最大值：" + qiwei_max);
        if (currentWendu > 100) {
            currentWendu = currentWendu - 256;
        }
        if (changedListener != null) {
            changedListener.changed(wendu_min, wendu_max, shidu_min, shidu_max, qiwei_max, auto, clience, shuixiang, jiankang, currentWendu, currentShidu, currentQiwei, jiare, chushi, chuchou, currentVolume);
        }

    }
}
