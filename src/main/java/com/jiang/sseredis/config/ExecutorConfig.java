package com.jiang.sseredis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * @author Jiang Jian
 * @since 2018/7/20
 */
@Configuration
public class ExecutorConfig {

    private static int CORE_POOL_SIZE = 7;
    private static int MAX_POOL_SIZE = 500;

    @Bean(name="sseTaskExecutor")
    public ThreadPoolTaskExecutor sseTaskExecutor(){
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        poolTaskExecutor.setThreadNamePrefix("sse-redis-task-executor-");
        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        //线程池维护线程所允许的空闲时间
        poolTaskExecutor.setKeepAliveSeconds(30000);
        poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return poolTaskExecutor;
    }

    @Bean(name="subTaskExecutor")
    public ThreadPoolTaskExecutor subTaskExecutor(){
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        poolTaskExecutor.setThreadNamePrefix("sub-redis-task-executor-");
        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        //线程池维护线程所允许的空闲时间
        poolTaskExecutor.setKeepAliveSeconds(30000);
        poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return poolTaskExecutor;
    }

    @Bean(name="pubTaskExecutor")
    public ThreadPoolTaskExecutor pubTaskExecutor(){
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        poolTaskExecutor.setThreadNamePrefix("pub-redis-task-executor-");
        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        //线程池维护线程所允许的空闲时间
        poolTaskExecutor.setKeepAliveSeconds(30000);
        poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return poolTaskExecutor;
    }
}
