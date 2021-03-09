package com.weibo.generator.network;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 *https://www.cnblogs.com/LiLiliang/p/12625783.html
 *SpringBoot access to the websocket
 * @return
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
