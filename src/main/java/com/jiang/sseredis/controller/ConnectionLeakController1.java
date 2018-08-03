package com.jiang.sseredis.controller;

import com.jiang.sseredis.redis.Publisher;
import com.jiang.sseredis.redis.RedisMsgPubSubListener;
import com.jiang.sseredis.redis.Subscribe;
import com.jiang.sseredis.service.SseObservable1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;


@RestController
@RequestMapping("")
@SuppressWarnings("unused")
public class ConnectionLeakController1{

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionLeakController1.class);

  private static final long SSE_TIMEOUT_MS = 30_000L;

  public static final String PREFIX_CHANNEL = "TEST:";

  private final SseObservable1 sseObservable1;

  @Autowired
  private ThreadPoolTaskExecutor sseTaskExecutor;

  @Autowired
  private Subscribe subscribe;

  @Autowired
  Publisher publisher;

  public ConnectionLeakController1() {
    this.sseObservable1 = new SseObservable1(SSE_TIMEOUT_MS,PREFIX_CHANNEL);
  }

  @GetMapping("/order")
  public SseEmitter home(@RequestParam(name = "uniCode") String uniCode) {
    final SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
    try {
      LOGGER.info("当前查询：{}",uniCode);
      sseTaskExecutor.execute(() ->{
        RedisMsgPubSubListener listener = new RedisMsgPubSubListener(sseObservable1);
        String uid = getTime17() + randomBigletter(6);
        String channel = PREFIX_CHANNEL + uniCode + "-" + uid;
        sseObservable1.subscribeSse(emitter,uniCode,listener,channel,uid);
        subscribe.subscribeChannel(channel,listener);
      });
    }catch (Exception e){
      e.printStackTrace();
    }
    return emitter;
  }

  //@Scheduled(fixedRate = 3000L)
  public void herpDerp() {
    String result = sseObservable1.getOrderList("085600170360180505");
    if (null == result){
      return;
    }
    publisher.sendMessage("test-085600170360180505",result);
  }

  @GetMapping("/callback")
  public String callback(@RequestParam(name = "uniCode") String uniCode){
    String result = sseObservable1.getOrderList(uniCode);
    if (null == result){
      return "查不到该订单";
    }
    String channel = PREFIX_CHANNEL + uniCode;
    publisher.sendMessage(channel,result);
    return "success";
  }

  @GetMapping("/callAllBack")
  public String callAllBack(){
    String unicodes = "085600170360180505";
    String[] st = unicodes.split(",");
    Stream.of(st).forEach(unicode -> {
      sseTaskExecutor.execute(() -> {
        String result = sseObservable1.getOrderList(unicode);
        if (null == result){
          return;
        }
        String channel = PREFIX_CHANNEL + unicode;
        publisher.sendMessage(channel,result);
      });
    });
    return "success";
  }

  public static String randomBigletter(int length) {
    char[] ss = new char[length];
    int i = 0;
    while (i < length) {
      i = i % length;
      ss[i] = (char) ('A' + Math.random() * 26);//随机生成一个A-Z之间的字符
      i++;
    }
    String aa = new String(ss);
    return aa;
  }

  public static String getTime17() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    Date now = new Date();
    String nowString = sdf.format(now);
    return nowString;
  }

}
