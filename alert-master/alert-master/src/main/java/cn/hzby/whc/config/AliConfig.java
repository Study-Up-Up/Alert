package cn.hzby.whc.config;

import cn.hzby.whc.service.ParameterRuleService;
import cn.hzby.whc.util.JsonToHashMap;
import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.common.AckMessageException;
import com.aliyun.mq.http.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//程序自动启动
@Component
public class AliConfig implements ApplicationRunner {

    //注入钉钉报警服务
    @Autowired
    ParameterRuleService parameterRuleService;

    //打印控制台
    private static final Logger LOGGER= LoggerFactory.getLogger(AliConfig.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //首次启动查询所有规则放入Redis中
        parameterRuleService.setAllRulesToRedis();

        //首次启动查询所有组合规则放到Redis中
        parameterRuleService.setRuleCombinationToRedis();

        MQClient mqClient = new MQClient(
                // 设置HTTP接入域名（此处以公共云生产环境为例）
                "",
                // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
                "",
                // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
                ""
        );

        // 所属的 Topic
        final String topic = "";
        // 您在控制台创建的 Consumer ID(Group ID)
        final String groupId = "";
        // Topic所属实例ID，默认实例为空
        final String instanceId = "";

        final MQConsumer consumer;
        if (instanceId != null && instanceId != "") {
            consumer = mqClient.getConsumer(instanceId, topic, groupId, null);
        } else {
            consumer = mqClient.getConsumer(topic, groupId);
        }

        // 在当前线程循环消费消息，建议是多开个几个线程并发消费消息
        do {
              List<Message> messages = null;
            try {
                // 长轮询消费消息
                // 长轮询表示如果topic没有消息则请求会在服务端挂住3s，3s内如果有消息可以消费则立即返回
                messages = consumer.consumeMessage(
                        3,// 一次最多消费3条(最多可设置为16条)
                        3// 长轮询时间3秒（最多可设置为30秒）
                );
            } catch (Throwable e) {
                e.printStackTrace();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            // 没有消息
            if (messages == null || messages.isEmpty()) {
                //System.out.println(Thread.currentThread().getName() + ": no new message, continue!");
                continue;
            }

            // 处理业务逻辑
            for (final Message message : messages) {
                //控制消息的转换
                Map<String,String> stringMap=JsonToHashMap.toHashMap(message.getMessageBodyString());
                //插入Redis
                parameterRuleService.setMachine(stringMap);
                //实时处理
                parameterRuleService.realTimeRedis(stringMap.get("dn"));
            }


            // Message.nextConsumeTime前若不确认消息消费成功，则消息会重复消费
            // 消息句柄有时间戳，同一条消息每次消费拿到的都不一样
            {
                List<String> handles = new ArrayList<>();
                for (Message message : messages) {
                    handles.add(message.getReceiptHandle());
                }
                try {
                    consumer.ackMessage(handles);
                } catch (Throwable e) {
                    // 某些消息的句柄可能超时了会导致确认不成功
                    if (e instanceof AckMessageException) {
                        AckMessageException errors = (AckMessageException) e;
                        System.out.println("Ack message fail, requestId is:" + errors.getRequestId() + ", fail handles:");
                        if (errors.getErrorMessages() != null) {
                            for (String errorHandle : errors.getErrorMessages().keySet()) {
                                System.out.println("Handle:" + errorHandle + ", ErrorCode:" + errors.getErrorMessages().get(errorHandle).getErrorCode()
                                        + ", ErrorMsg:" + errors.getErrorMessages().get(errorHandle).getErrorMessage());
                            }
                        }
                        continue;
                    }
                    e.printStackTrace();
                }
            }
        } while (true);
    }
}
