package com.weibo.generator.weiboApi;

import com.weibo.generator.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class GenerateWeiboData implements CommandLineRunner {
    @Autowired
    private Network network;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=======Start generating weibo data=======");
        try {
            network.start();
        } catch (Exception exception) {
            network.start();
        }
    }
}
