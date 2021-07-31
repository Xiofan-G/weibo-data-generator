package com.weibo.generator.network;

import com.alibaba.fastjson.JSON;
import com.weibo.generator.entity.ControlMessage;
import com.weibo.generator.service.CitiBikeGenerator;
import com.weibo.generator.service.DataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/control/{sid}")
@Component
public class Network {
    // count online user's number
    private static final AtomicInteger onlineNum = new AtomicInteger();
    //Use thread-safe Set to hold every client's session
    private static final ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    @Autowired
    private KafkaTemplate<String, String> kafkaClient;
    private ControlMessage controlMessage;
    private boolean serverIsOk = true;

    @Value("${kafka.producer.topic.data}")
    private String dataTopic;
    @Value("${kafka.producer.topic.control}")
    private String controlTopic;

    public static void addOnlineCount() {
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    public void sendMessage(Session session, String message) throws IOException {
        if (session != null) {
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }

    private void sendToClient(String userName, String message) {
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String userID) {
        sessionPools.put(userID, session);
        addOnlineCount();
        System.out.println(userID + " joined！current user's number is " + onlineNum);
        try {
            sendMessage(session, "Welcome " + userID + " to join the websocket server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(@PathParam(value = "sid") String userId) {
        sessionPools.remove(userId);
        subOnlineCount();
        System.out.println(userId + " disconnected！current user's number is " + onlineNum);
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        System.out.println(String.format("received client's ：%s", message));
        try {
            this.controlMessage = JSON.parseObject(message, ControlMessage.class);
            this.controlMessage.setTimestamp();
            this.sendToClient(this.controlMessage.getUserId(), "{\"msg\":\"Received\"}");
            Client client = (Client) SpringUtil.getBean("client");
            KafkaTemplate<String, String> kafkaClient = (KafkaTemplate<String, String>) SpringUtil.getBean("kafkaClient");
            kafkaClient.send(client.getControlTopic(), this.controlMessage.toString());
            this.controlMessage = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    public void start() throws InterruptedException, IOException {
       while (true) {
           System.out.println("=======Generating weibo data=======");
           try {
                DataGenerator.generate(this);
           } catch (Exception exception) {
                DataGenerator.generate(this);
          }
       }


  //      while (true) {
     //       System.out.println("=======Generating Citibike data=======");
    //        try {
        //        CitiBikeGenerator.generate(this);
       //     } catch (Exception exception) {
        //        CitiBikeGenerator.generate(this);
       //     }
     //   }
  }

    public void send(String msg, String... topic) {
        ListenableFuture<SendResult<String, String>> future;
        if (topic.length == 0) {
            future = this.kafkaClient.send(kafkaClient.getDefaultTopic(), msg);
        } else {
            future = this.kafkaClient.send(topic[0], msg);
        }

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println(result.toString());
                if (!serverIsOk)
                    serverIsOk = true;
            }

            @Override
            public void onFailure(Throwable ex) {
                ex.printStackTrace();
                serverIsOk = false;
            }
        });

    }

    public void sendControlSignal() {
        if (this.controlMessage != null) {
            this.send(this.controlMessage.toString(), controlTopic);
            this.resetControlSignal();
        }
    }

    public void setControlMessage(ControlMessage message) {
        this.controlMessage = message;
    }

    public boolean hasControlMessage() {
        return this.controlMessage != null;
    }

    private void resetControlSignal() {
        this.controlMessage = null;
    }

    public boolean serverIsOk() {
        return serverIsOk;
    }
}
