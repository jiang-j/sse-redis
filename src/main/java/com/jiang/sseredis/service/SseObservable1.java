package com.jiang.sseredis.service;

import com.jiang.sseredis.redis.RedisMsgPubSubListener;
import com.jiang.sseredis.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.function.Consumer;

public class SseObservable1 {

    private final Long sseTimeoutMs;

    private final ConcurrentHashMap<String,KeySetView<Consumer<String>, Boolean>> concurrentHashMap;

    public SseObservable1(Long sseTimeoutMs) {
        this.sseTimeoutMs = sseTimeoutMs;
        this.concurrentHashMap =  new ConcurrentHashMap<>();
    }

    public void send(String uniCode,String orders) {
        KeySetView<Consumer<String>, Boolean> consumers = concurrentHashMap.get(uniCode);
      if (null != consumers && consumers.size() > 0){
          consumers.forEach(it -> it.accept(orders));
      }else {
          return;
      }
    }

    public void subscribeSse(SseEmitter emitter,String uniCode,RedisMsgPubSubListener listener,String channel) {

        Consumer<String> subscription = orderInfos -> {
            SseEventBuilder event = SseEmitter.event().id(uniCode).name("message").reconnectTime(sseTimeoutMs).data(orderInfos);
            trySend(emitter, event);
        };


        final KeySetView<Consumer<String>, Boolean> consumers =
          Optional.ofNullable(concurrentHashMap.get(uniCode)).orElseGet(() -> ConcurrentHashMap.newKeySet());

        consumers.add(subscription);
        concurrentHashMap.put(uniCode,consumers);

        System.out.println("Subscription added: there are " + concurrentHashMap.size() + " subscribers");

        emitter.onCompletion(() -> {
            consumers.remove(subscription);
            Optional.ofNullable(consumers).orElseGet(() -> concurrentHashMap.remove(uniCode));

            System.out.println("Subscription completed: there are " + concurrentHashMap.size() + " subscribers");
            listener.unsubscribe(channel);
        });

        emitter.onError(error -> {
            consumers.remove(subscription);
            System.out.println("Subscription crashed: there are " + concurrentHashMap.size() + " subscribers");
            //listener.unsubscribe("test-"+uniCode);
        });

        emitter.onTimeout(() -> {
            consumers.remove(subscription);
            System.out.println("Subscription timed out: there are " + concurrentHashMap.size() + " subscribers");
            //listener.unsubscribe("test-"+uniCode);
        });

        // Firefox doesn't call the EventSource.onopen when the connection is established.
        // Instead, it requires at least one event to be sent. A meaningless comment event is used.
        SseEventBuilder greetingEvent = SseEmitter.event().id(uniCode)
            .name("greeting").reconnectTime(sseTimeoutMs)
            .data(getOrderList(uniCode));
        trySend(emitter, greetingEvent);

        return;
    }

      private void trySend(SseEmitter emitter, SseEventBuilder event) {
        try {
          emitter.send(event);
        } catch (Exception ex) {
          // This is normal behavior when a client disconnects.
          try {
            emitter.completeWithError(ex);
            System.out.println("Marked SseEmitter as complete with an error.");
          } catch (Exception completionException) {
            System.out.println("Failed to mark SseEmitter as complete on error.");
          }
        }
      }

    public String getOrderList(String uniCode){
        String url = "http://101.37.119.210/issp/ApiService";
        Map<String,String> param = new HashMap<>();
        param.put("method","xforceplus.toc.getOrderByPollNew");
        param.put("APIKey","VQrXtzr5fIDr4foNmrRdIw==");
        param.put("groupFlag","Walmart");
        param.put("storeCode","11210");
        param.put("uniCode",uniCode);
        param.put("unAccount","UN112995");
        String result = null;
        try {
            HttpResponse response = HttpUtils.doGet(url,null,null,new HashMap<>(1),param);
            result = HttpUtils.convertStreamToString(response.getEntity().getContent());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
