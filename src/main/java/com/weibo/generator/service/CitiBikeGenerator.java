package com.weibo.generator.service;

import com.weibo.generator.network.Network;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CitiBikeGenerator {
    private static int sendNumberPerSecond = 60;

    public static boolean generate(Network client) throws IOException {
        String citiBikeDataPath = "data/JC-202007-citibike-tripdata.csv";
        ClassPathResource resource = new ClassPathResource(citiBikeDataPath);
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {

            is = resource.getInputStream();
            isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            String data;
            try {
                //skip csv header line
                reader.readLine();
            } catch (Exception exception) {
            }

            long endTime;
            long sleepInSec = 0;
            while (client.serverIsOk()) {
                if (client.hasControlMessage()) {
                    client.sendControlSignal();
                }
                long startTime = System.currentTimeMillis();
                int hasSentNum = sendNumberPerSecond;
                while (hasSentNum > 0 && (data = reader.readLine()) != null) {
                    client.send(data, "bank");
                    hasSentNum--;
                }

                endTime = System.currentTimeMillis();
                sleepInSec = 1000 - (endTime - startTime);

                if (sleepInSec < 0) {
                    continue;
                }
                System.out.println(sleepInSec/1000.0);
                System.out.println("Thread will sleep for: " + sleepInSec / 1000.0);
                Thread.sleep(sleepInSec);
            }


            reader.close();
            isr.close();
            is.close();
        } catch (Exception exception) {
            if (reader != null) {
                reader.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return true;

    }
}
