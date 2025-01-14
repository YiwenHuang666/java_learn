package com.xuecheng.orders.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.huang.base.exception.XueChengPlusException;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import com.xuecheng.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Api(value = "订单支付接口", tags = "订单支付接口")
@Slf4j
@Controller
public class OrderController {

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Autowired
    OrderService orderService;


    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String id = user.getId();


        PayRecordDto QrCode = orderService.createOrder(id, addOrderDto);
        return QrCode;
    }


    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestpay(String payNo, HttpServletResponse httpResponse) throws IOException, AlipayApiException {
        XcPayRecord payRecordByPayno = orderService.getPayRecordByPayno(payNo);
        if (payRecordByPayno == null) {
            XueChengPlusException.cast("支付记录不存在");
        }

        if (payRecordByPayno.getStatus().equals("601002")) {
            XueChengPlusException.cast("已支付，无需再次支付");
        }

        AlipayClient alipayClient = new
                DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY,
                AlipayConfig.FORMAT, AlipayConfig.CHARSET,
                ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
////异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl("http://wvvxpp.natappfree.cc/orders/receivenotify");
////同步跳转地址，仅支持http/https
//        request.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
/******必传参数******/
        JSONObject bizContent = new JSONObject();
//商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", payRecordByPayno.getPayNo());
//支付金额，最小值0.01元
        bizContent.put("total_amount", payRecordByPayno.getTotalPrice());
//订单标题，不可使用特殊符号
        bizContent.put("subject", payRecordByPayno.getOrderName());
        //手机网站支付默认传值QUICK_WAP_WAY
        bizContent.put("product_code", "QUICK_WAP_WAY");

        request.setBizContent(bizContent.toString());
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "POST");
        String form = response.getBody();
        httpResponse.setContentType("text/html;charset=UTF-8");
        httpResponse.getWriter().write(form);//直接将完整的表单 html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @ApiOperation("查询支付结果")
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDto payresult(String payNo) throws IOException {
//查询支付结果
        PayRecordDto payRecordDto = orderService.queryPayResult(payNo);

        return payRecordDto;
    }


    @ApiOperation("接收支付结果通知")
    @PostMapping("/receivenotify")
    public void receivenotify(HttpServletRequest request, HttpServletResponse out) throws UnsupportedEncodingException, AlipayApiException {
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
//验签
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");
        if (verify_result) {//验证成功
//商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
//支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
//交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
//appid
            String app_id = new String(request.getParameter("app_id").getBytes("ISO-8859-1"), "UTF-8");
//total_amount
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
//交易成功处理
            if (trade_status.equals("TRADE_SUCCESS")) {
//处理逻辑。。。
                PayStatusDto payStatusDto = new PayStatusDto();

                payStatusDto.setTrade_status(trade_status);
                payStatusDto.setTrade_no(trade_no);
                payStatusDto.setOut_trade_no(out_trade_no);
                payStatusDto.setApp_id(app_id);
                payStatusDto.setTotal_amount(total_amount);

                orderService.saveAliPayStatus(payStatusDto);
            }
        }else {
            XueChengPlusException.cast("支付验证失败");
        }
    }


}
