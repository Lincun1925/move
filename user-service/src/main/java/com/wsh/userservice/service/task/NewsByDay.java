package com.wsh.userservice.service.task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class NewsByDay {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "* * 6 * * ?")
    private void getNews() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> {
                    try {
                        Map<String, String> map = new HashMap<>();
                        URL url = new URL("https://www.baidu.com");
                        Document document = Jsoup.parse(url, 3000);
                        Element element = document.getElementById("s-hotsearch-wrapper");
                        Elements elements = element.getElementsByTag("li");
                        for (Element el : elements) {
                            Elements aClass = el.getElementsByClass("title-content");
                            String href = aClass.attr("href");
                            String text = aClass.text().substring(2);
                            map.put(text, href);
                        }
                        stringRedisTemplate.opsForHash().delete("newsByDay");
                        stringRedisTemplate.opsForHash().putAll("newsByDay", map);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
