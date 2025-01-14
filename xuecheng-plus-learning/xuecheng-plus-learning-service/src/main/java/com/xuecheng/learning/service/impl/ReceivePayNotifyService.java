package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.huang.base.exception.XueChengPlusException;
import com.huang.messagesdk.model.po.MqMessage;
import com.huang.messagesdk.service.MqMessageService;
import com.rabbitmq.client.Channel;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReceivePayNotifyService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    MqMessageService mqMessageService;
    @Autowired
    MyCourseTablesService myCourseTablesService;

    //监听消息队列接收支付结果通知
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message, Channel channel) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//获取消息
        MqMessage mqMessage = JSON.parseObject(message.getBody(),
                MqMessage.class);
        log.debug("学习中心服务接收支付结果:{}", mqMessage);
//消息类型
        String messageType = mqMessage.getMessageType();
//订单类型,60201 表示购买课程
        String businessKey2 = mqMessage.getBusinessKey2();
//这里只处理支付结果通知
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType) &&
                "60201".equals(businessKey2)) {
//选课记录 id
            String choosecourseId = mqMessage.getBusinessKey1();
//添加选课
            boolean b = myCourseTablesService.saveChooseCourseStauts(choosecourseId);
            if (!b) {
//添加选课失败，抛出异常，消息重回队列
                XueChengPlusException.cast("收到支付结果，添加选课失败");
            }
        }
    }
}
