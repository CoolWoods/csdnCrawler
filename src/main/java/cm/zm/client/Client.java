package cm.zm.client;

import cm.zm.main.CSDNCrawler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Client extends Thread{
    public static void main(String[] args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true) {
            long startTime = System.currentTimeMillis();
            System.out.println(dateFormat.format(new Date()));
            try {
                new CSDNCrawler("crawler0").start();
                new CSDNCrawler("crawler1").start();
                new CSDNCrawler("crawler2").start();
                new CSDNCrawler("crawler3").start();
                new CSDNCrawler("crawler4").start();
                new CSDNCrawler("crawler5").start();
                Random random = new Random();
                int time = 1000*30 + random.nextInt(100);
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            Double sometime = (endTime - startTime) / 1000 / 60.0;
            System.out.println("程序运行：" + sometime + "分钟");
        }
    }
}
