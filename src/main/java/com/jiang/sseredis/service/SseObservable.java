package com.jiang.sseredis.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.function.Consumer;

public class SseObservable {

  private final Long sseTimeoutMs;
  private final KeySetView<Consumer<String>, Boolean> subscriptions;

  public SseObservable(Long sseTimeoutMs) {
    this.sseTimeoutMs = sseTimeoutMs;
    this.subscriptions = ConcurrentHashMap.newKeySet();
  }

  public void send(String message) {
    subscriptions.forEach(it -> it.accept(message));
  }

  public SseEmitter subscribeSse() {
    SseEmitter emitter = new SseEmitter(sseTimeoutMs);

    Consumer<String> subscription = message -> {
      SseEventBuilder event = SseEmitter.event().name("message").data(message);
      trySend(emitter, event);
    };

    subscriptions.add(subscription);
    System.out.println("Subscription added: there are " + subscriptions.size() + " subscribers");

    emitter.onCompletion(() -> {
      subscriptions.remove(subscription);
      System.out.println("Subscription completed: there are " + subscriptions.size() + " subscribers");
    });
    emitter.onError(error -> {
      subscriptions.remove(subscription);
      System.out.println("Subscription crashed: there are " + subscriptions.size() + " subscribers");
    });
    emitter.onTimeout(() -> {
      subscriptions.remove(subscription);
      System.out.println("Subscription timed out: there are " + subscriptions.size() + " subscribers");
    });

    // Firefox doesn't call the EventSource.onopen when the connection is established.
    // Instead, it requires at least one event to be sent. A meaningless comment event is used.
    SseEventBuilder greetingEvent = SseEmitter.event()
        .name("greeting")
        .comment("Hello! This greeting forces Firefox's EventSource.onopen to fire.");
    trySend(emitter, greetingEvent);

    return emitter;
  }

  private void trySend(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
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
}
