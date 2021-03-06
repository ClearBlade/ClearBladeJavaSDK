package com.clearblade.examples;

import com.clearblade.java.api.*;
import com.clearblade.java.api.auth.UserAuth;

import java.util.concurrent.TimeUnit;

public class UserAuthExample {

    public static InitOptions makeInitOptions() {

        String platformUrl = System.getenv("PLATFORM_URL");
        String messagingUrl = System.getenv("MESSAGING_URL");
        String email = System.getenv("EMAIL");
        String password = System.getenv("PASSWORD");

        InitOptions options = new InitOptions();

        if (platformUrl != null && platformUrl.length() > 0) {
            System.out.println("Found PLATFORM_URL");
            options.setPlatformUrl(platformUrl);
        }

        if (messagingUrl != null && messagingUrl.length() > 0) {
            System.out.println("Found MESSAGING_URL");
            options.setMessagingUrl(messagingUrl);
        }

        options.setAuth(new UserAuth(email, password));
        return options;
    }

    public static void initializeClearBlade() {

        String systemKey = System.getenv("SYSTEM_KEY");
        String systemSecret = System.getenv("SYSTEM_SECRET");

        InitCallback initCb = new InitCallback() {
            @Override
            public void done(boolean results) {
                System.out.print("done initializing\n");
            }

            @Override
            public void error(ClearBladeException exception) {
                System.err.printf("error initializing: %s\n", exception);
                exception.getCause().printStackTrace();
            }
        };

        InitOptions options = makeInitOptions();
        ClearBlade.initialize(systemKey, systemSecret, options, initCb);
    }

    public static void main(String[] args) throws ClearBladeException, InterruptedException {

        initializeClearBlade();

        String identifier = "deviceClientID";
        String topic = "userMessagingTopic";

        MqttClient mqttClient = new MqttClient(identifier, 1);

        System.out.println("Publishing...");

        for (int idx = 0; idx < 100; idx++) {
            // The next line shows one way to publish with the retained flag set to false
            // mqttClient.publish(topic, String.format("userMessagingBody %s", "message:" + idx));

            // The next line shows a second way to publish with the retained flag set to false
            // mqttClient.publish(topic, String.format("userMessagingBody %s", "message:" + idx), false);

            // The next line shows how to publish with the retained flag set to true
            mqttClient.publish(topic, String.format("userMessagingBody %s", "message:" + idx), true);
        };

        // The next line shows how to delete a retained message for the topic
        // mqttClient.publish(topic, "", true);

        TimeUnit.SECONDS.sleep(3);
        mqttClient.disconnect();
    }
}
