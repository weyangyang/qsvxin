package com.qsvxin.net.interf;

import java.util.ArrayList;

import com.qsvxin.bean.VorderFormBean;

public interface AfterAcceptOFParserData {
    /**
     * 受理商城订单信息失败的数据
     * 
     * @param errCode
     *            错误编号
     * @param errMsg
     *            错误信息
     */
public void getErrData(String errCode, String errMsg);
/**
 * 受理商城订单信息成功的数据
 * @param succCode
 * @param succMsg
 * @param mArrayList  
 * @param buyGoodsData  
 */
public void getSuccData(String succCode, String succMsg,ArrayList<VorderFormBean> mArrayList);
/**
 * 受理商城订单信息成功的数据
 * @param succCode
 * @param succMsg
 */
public void getSuccData(String succCode, String succMsg);
/**
 * 获取网络异常
 * @param errCode
 * @param errMsg
 */
public void getNetErrData(int errCode, String errMsg);
/**
 * 获取解析异常数据
 * @param errCode
 * @param errMsg
 */
public void getParserErrData(int errCode, String errMsg);
}
