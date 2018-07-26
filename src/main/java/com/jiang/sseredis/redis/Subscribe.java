package com.jiang.sseredis.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Jiang Jian
 * @since 2018/7/23
 */
@Component
public class Subscribe {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subscribe.class);

    @Autowired
    RedisService redisService;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;
    /**
     * 订阅频道
     *
     * @param channel 频道
     * @param redisMsgPubSubListener
     */
    public void subscribeChannel(final String channel, final RedisMsgPubSubListener redisMsgPubSubListener) {

        taskExecutor.execute(()->{
            LOGGER.info("在: {} 频道订阅消息", channel);
            redisService.subscribe(redisMsgPubSubListener, channel);
        });
    }
}
