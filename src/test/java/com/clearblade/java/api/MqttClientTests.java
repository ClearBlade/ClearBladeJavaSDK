package com.clearblade.java.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.clearblade.java.api.auth.Auth;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttClientTests {

    private final MessageCallback noopMessageCallback = new MessageCallback();

    private Auth mockAuth;
    private org.eclipse.paho.client.mqttv3.MqttClient mockClient;
    private MqttClient spyClient;

    @BeforeEach
    void setupSpyClient() throws ClearBladeException {
        MqttClient client = new MqttClient("url", mockAuth, "systemKey", "identifier", 0, false);
        mockAuth = mock(Auth.class);
        mockClient = mock(org.eclipse.paho.client.mqttv3.MqttClient.class);
        spyClient = spy(client);
        spyClient.mqttClient = mockClient;
    }

    @Test
    void subscribingToOneTopicRegistersOneCallback() throws Exception {
        spyClient.subscribe("topic-0", 0, noopMessageCallback);

        verify(mockClient).subscribe("topic-0", 0);

        assertEquals(1, spyClient.callbackByTopic.size());
        assertEquals(1, spyClient.qosByTopic.size());
    }

    @Test
    void subscribingToTwoTopicsRegistersTwoCallbacks() throws Exception {
        spyClient.subscribe("topic-0", 0, noopMessageCallback);
        spyClient.subscribe("topic-1", 1, noopMessageCallback);

        verify(mockClient).subscribe("topic-0", 0);
        verify(mockClient).subscribe("topic-1", 1);

        assertEquals(2, spyClient.callbackByTopic.size());
        assertEquals(2, spyClient.qosByTopic.size());
    }

    @Test
    void resubscribingWithNoPreviousSubscribeResubscribesToNoTopics() throws Exception {
        spyClient.resubscribe();

        verify(mockClient, times(0)).subscribe(anyString(), anyInt());
    }

    @Test
    void resubscribingWithOnePreviousSubscribeResubscribesToOneTopic() throws Exception {
        spyClient.subscribe("topic-0", 0, noopMessageCallback);
        spyClient.resubscribe();

        verify(mockClient, times(2)).subscribe("topic-0", 0);
    }

    @Test
    void resubscribingWithTwoPreviousSubscribeResubscribesToTwoTopics() throws Exception {
        spyClient.subscribe("topic-0", 0, noopMessageCallback);
        spyClient.subscribe("topic-1", 1, noopMessageCallback);
        spyClient.resubscribe();

        verify(mockClient, times(2)).subscribe("topic-0", 0);
        verify(mockClient, times(2)).subscribe("topic-1", 1);
    }

    @Test
    void resubscribingWithErrorReportsErrorToCallback() throws Exception {
        Throwable throwable = mock(MqttException.class);
        MessageCallback mockCallback = mock(MessageCallback.class);

        spyClient.subscribe("topic-0", 0, mockCallback);

        doThrow(throwable).when(mockClient).subscribe("topic-0", 0);

        spyClient.resubscribe();

        verify(mockCallback, times(1)).error(any());
    }

    @Test
    void unsubscribingFromTopicRemovesCallback() throws Exception {
        spyClient.subscribe("topic-0", 0, noopMessageCallback);
        spyClient.subscribe("topic-1", 1, noopMessageCallback);

        spyClient.unsubscribe("topic-0");

        verify(mockClient).unsubscribe("topic-0");
        assertEquals(1, spyClient.callbackByTopic.size());
        assertEquals(1, spyClient.qosByTopic.size());

        spyClient.unsubscribe("topic-1");

        verify(mockClient).unsubscribe("topic-1");
        assertEquals(0, spyClient.callbackByTopic.size());
        assertEquals(0, spyClient.qosByTopic.size());
    }

    @Test
    void connectCompleteEventCallsResubscribe() {
        spyClient.connectComplete(false, "url");

        verify(spyClient, times(1)).resubscribe();
    }

    @Test
    void connectCompleteEventCallsOnConnectionComplete() {
        MqttClient.OnConnectionComplete mockCallback = mock(MqttClient.OnConnectionComplete.class);

        spyClient.onConnectionComplete(mockCallback);
        spyClient.connectComplete(false, "url");

        verify(mockCallback, times(1)).onConnectionComplete(false, "url");
    }

    @Test
    void connectionLostEventCallsOnConnectionLost() {
        MqttClient.OnConnectionLost mockCallback = mock(MqttClient.OnConnectionLost.class);
        Throwable throwable = new Exception("some error");

        spyClient.onConnectionLost(mockCallback);
        spyClient.connectionLost(throwable);

        verify(mockCallback, times(1)).onConnectionLost(throwable);
    }

    @Test
    void messageArrivedEventOnSubscribedTopicUsesCallback() throws Exception {
        MessageCallback mockCallback = mock(MessageCallback.class);

        spyClient.subscribe("topic-0", 0, mockCallback);

        MqttMessage mockMessage = mock(MqttMessage.class);

        when(mockMessage.getPayload()).thenReturn("foo".getBytes());

        spyClient.messageArrived("topic-0", mockMessage);

        verify(mockCallback, times(1)).done("topic-0", "foo".getBytes());
        verify(mockCallback, times(1)).done("topic-0", "foo");
    }

    @Test
    void messageArrivedOnSubscribedWildcardTopicUsesCallback() throws Exception {
        MessageCallback mockCallback = mock(MessageCallback.class);

        spyClient.subscribe("topic-0", 0, mockCallback);
        spyClient.subscribe("level/+/topic", 0, mockCallback);
        spyClient.subscribe("multi/#", 0, mockCallback);

        MqttMessage mockMessageOne = mock(MqttMessage.class);
        MqttMessage mockMessageTwo = mock(MqttMessage.class);
        MqttMessage mockMessageThree = mock(MqttMessage.class);

        when(mockMessageOne.getPayload()).thenReturn("foo".getBytes());
        when(mockMessageTwo.getPayload()).thenReturn("bar".getBytes());
        when(mockMessageThree.getPayload()).thenReturn("baz".getBytes());

        spyClient.messageArrived("topic-0", mockMessageOne);
        spyClient.messageArrived("level/foo/topic", mockMessageTwo);
        spyClient.messageArrived("multi/foo/topic", mockMessageThree);

        verify(mockCallback, times(1)).done("topic-0", "foo".getBytes());
        verify(mockCallback, times(1)).done("topic-0", "foo");

        verify(mockCallback, times(1)).done("level/foo/topic", "bar".getBytes());
        verify(mockCallback, times(1)).done("level/foo/topic", "bar");

        verify(mockCallback, times(1)).done("multi/foo/topic", "baz".getBytes());
        verify(mockCallback, times(1)).done("multi/foo/topic", "baz");
    }
}
