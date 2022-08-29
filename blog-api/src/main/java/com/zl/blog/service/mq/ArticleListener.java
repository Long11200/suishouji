package com.zl.blog.service.mq;

import com.alibaba.fastjson.JSON;
import com.zl.blog.service.ArticleService;
import com.zl.blog.vo.ArticleMessage;
import com.zl.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

/**
 * @author zyiyi
 */
@Slf4j
@Component
public class ArticleListener{

    @Autowired
    private ArticleService articleService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(queues = "blog-update-article")
    public void onMessage(ArticleMessage message) {
        log.info("收到的消息:{}" ,message);
        //1. 更新查看文章详情的缓存
        Long articleId = message.getArticleId();
        String params = DigestUtils.md5Hex(articleId.toString());
        String redisKey = "view_article::ArticleController::findArticleById::"+params;
        Result articleResult = articleService.findArticleById(articleId);
        redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(articleResult), Duration.ofMillis(5 * 60 * 1000));
        log.info("更新了缓存:{}",redisKey);
        //2. 文章列表的缓存 不知道参数,解决办法 直接删除缓存
        Set<String> keys = redisTemplate.keys("Article*");
        keys.forEach(s -> {
            redisTemplate.delete(s);
            log.info("删除了文章列表的缓存:{}",s);
        });

    }
}