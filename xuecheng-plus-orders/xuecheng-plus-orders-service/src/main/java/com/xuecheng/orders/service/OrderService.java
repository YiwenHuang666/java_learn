package com.xuecheng.orders.service;

import com.huang.messagesdk.model.po.MqMessage;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

public interface OrderService {
    /**
     * @description 创建商品订单
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付交易记录(包括二维码)
     * @author Mr.M
     * @date 2022/10/4 11:02
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    public XcPayRecord getPayRecordByPayno(String payNo);

    public void saveAliPayStatus(PayStatusDto payStatusDto) ;

    public PayRecordDto queryPayResult(String payNo);

    public void notifyPayResult(MqMessage message);
}
