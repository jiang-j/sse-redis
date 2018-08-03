package com.jiang.sseredis.config;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jiangjian on 2018/8/2.
 */
public class TestBlockingQueue {

    private static LinkedBlockingQueue<TcpMsg> queue = new LinkedBlockingQueue<>();
    private static String msg = "<<--添加的消息--";
    private static long times = 0;//第xxx次添加消息

    public static void main(String[] args) {
        addMsg();
        new SendThread().start();
    }

    private static void addMsg() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TcpMsg tcpMsg = new TcpMsg();
                times += 1;
                tcpMsg.msg = msg + times;
                tcpMsg.time = System.currentTimeMillis();
                addMsg(tcpMsg);
            }
        }, 3000, 1000);
    }

    private static void addMsg(TcpMsg tcpMsg) {
        if (!queue.contains(tcpMsg)) {
//            queue.add(tcpMsg);
            try {
                queue.put(tcpMsg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class SendThread extends Thread {
        @Override
        public void run() {
            TcpMsg tcpMsg;
            try {
                while (!Thread.interrupted() && (tcpMsg = queue.take()) != null) {
                    System.out.print(tcpMsg.msg + "  " + tcpMsg.time + "\r\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class TcpMsg {
        public String msg;
        public long time;
    }
}
