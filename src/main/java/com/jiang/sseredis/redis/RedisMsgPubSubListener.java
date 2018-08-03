package com.jiang.sseredis.redis;

import com.jiang.sseredis.controller.ConnectionLeakController1;
import com.jiang.sseredis.service.SseObservable1;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 *
 */
public class RedisMsgPubSubListener extends JedisPubSub {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMsgPubSubListener.class);

    private SseObservable1 sseObservable;

    public RedisMsgPubSubListener(SseObservable1 sseObservable) {
        this.sseObservable = sseObservable;
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

    @Override
    public void unsubscribe(String... channels) {
        super.unsubscribe(channels);
    }

    @Override
    public void subscribe(String... channels) {
        super.subscribe(channels);
    }

    @Override
    public void psubscribe(String... patterns) {
        super.psubscribe(patterns);
    }

    @Override
    public void punsubscribe() {
        super.punsubscribe();
    }

    @Override
    public void punsubscribe(String... patterns) {
        super.punsubscribe(patterns);
    }

    @Override
    public void onMessage(String channel, String message) {
        sseObservable.send(channel,message);
        LOGGER.info("在: {} 频道收到消息: {}", channel, message);
        //this.unsubscribe();
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {

    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.info("在: {} 频道订阅消息 - {}", channel, subscribedChannels);
        //System.out.println("channel:" + channel + "is been subscribed:" + subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {

    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {

    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOGGER.info("在: {} 频道取消订阅消息 - {}", channel, subscribedChannels);
        //System.out.println("channel:" + channel + "is been unsubscribed:" + subscribedChannels);
    }
}
