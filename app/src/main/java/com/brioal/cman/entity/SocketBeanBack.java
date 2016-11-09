package com.brioal.cman.entity;

import com.brioal.baselib.util.klog.KLog;

public class SocketBeanBack {

    private String packHeadFlag = "FF10";  //包头标志（2byte 上行FF10下行FF20）
    private String allPackSize = "1D00";    //全包长（2byte低位在前 消费机报文内容长度+14）
    private String packOrder = "0000";        //序列号（2byte 0x00;0x00）
    private String backFlagNo = "06";        //返回标示码（1byte）
    private String netBrigeNum = "00000001";//网桥终端号（4byte 十六进制低位在前）   厕所侠编号
    private String dataTypeFlag = "00";        //数据类型标识域（1byte）
    private String posNo = "01";                //机号
    private String frameNum = "01";            //帧号
    private String packInfoSize = "0B";        //报文内容长度
    private String commandWord = "12";        //命令字功能码
    private String packInfo = "0001102012300002";        //报文体
    //    private String machineSerialNum = "400E00";    //机具序列号（3byte）
//    private String blackMaxNum = "00000000";        //黑名单最大号（4Bytes）
//    private String businessNum = "3030";            //营业号（2Bytes）
    private String posPackCunum = "AE4E";            //（消费机报文累加和异或和）
    private String allPackCunum = "3086";            //校检（2byte 累加和 异或和）

    String msg = "";

    public SocketBeanBack() {
        super();
    }

    //网桥编号 命令字 功能内容
    public SocketBeanBack(String netBrigeNum, String commandWord, String packInfo

    ) {
        super();
        this.netBrigeNum = netBrigeNum;
        this.commandWord = commandWord;
        this.packInfo = packInfo;
        appendString();
        calculateSize();//计算全包长
        calculateCunum();
        KLog.d("处理后的发送内容：" + msg);
        KLog.d("包头：" + packHeadFlag);
        KLog.d("全包长：" + allPackSize);
        KLog.d("序列号：" + packOrder);
        KLog.d("返回码：" + backFlagNo);
        KLog.d("网桥终端号：" + this.netBrigeNum);
        KLog.d("数据类型：" + dataTypeFlag);
        KLog.d("机号：" + posNo);
        KLog.d("桢号：" + frameNum);
        KLog.d("报文内容长度：" + packInfoSize);
        KLog.d("命令子：" + this.commandWord);
        KLog.d("报文体：" + this.packInfo);
//        KLog.d("机具序列号" + machineSerialNum);
//        KLog.d("黑名单" + blackMaxNum);
//        KLog.d("营业号" + businessNum);
        KLog.d("消费机报文累加亦或和" + posPackCunum);
        KLog.d("全累加和亦或和" + allPackCunum);
    }

    //计算累加亦或和
    private void calculateCunum() {
        //计算报文校验
        int sum = 0;
        int xrl = 0;
        if (packInfo.length() > 0) {
            for (int i = 0; i <= ((packInfo.length() - 4) < 0 ? 0 : (packInfo.length() - 4)); i += 2) {
                sum += Integer.valueOf(packInfo.substring(i, i + 2), 16);
                xrl ^= Integer.valueOf(packInfo.substring(i, i + 2), 16);
            }
        }
        String sumCunum = addZeroForNum(Integer.toHexString(sum), 2, true).toUpperCase();//累加和
        String xrlCunum = addZeroForNum(Integer.toHexString(xrl), 2, true).toUpperCase();//异或和
        posPackCunum = xrlCunum+sumCunum ;
        appendString();
        sum = 0;
        xrl = 0;
        for (int i = 0; i <= msg.length()-6; i += 2) {
            sum += Integer.valueOf(msg.substring(i, i + 2), 16);
            xrl ^= Integer.valueOf(msg.substring(i, i + 2), 16);
        }
        sumCunum = addZeroForNum(Integer.toHexString(sum), 2, true).toUpperCase();//累加和
        xrlCunum = addZeroForNum(Integer.toHexString(xrl), 2, true).toUpperCase();//异或和
        allPackCunum = xrlCunum+sumCunum ;
        appendString();
    }

    public void appendString() {
        msg = packHeadFlag + allPackSize + packOrder + backFlagNo + netBrigeNum + dataTypeFlag + posNo + frameNum + packInfoSize + commandWord + packInfo + posPackCunum + allPackCunum;
    }

    //计算包长(全包长，报文长）
    public void calculateSize() {
        packInfoSize = addZeroForNum(Integer.toHexString(packInfo.length() / 2 + 2), 2, false).toUpperCase();//报文内容长度
        allPackSize = Integer.toHexString(msg.length() / 2).toUpperCase() + "00";//全包长
    }

    @Override
    public String toString() {
        return msg;
    }

    /*数字不足位数左补0
       *
       * @param str
       * @param strLength
       */
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
}
