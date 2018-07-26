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
public class Publisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    @Autowired
    RedisService redisService;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 信息
     */
    public void sendMessage(final String channel, final String message) {
        taskExecutor.execute(() -> {
            Long publish = redisService.publish(channel, message);
            LOGGER.info("在: {} 频道发布消息{} - {}", channel, message, publish);
        });
        LOGGER.info("发布线程启动:");
    }
}
