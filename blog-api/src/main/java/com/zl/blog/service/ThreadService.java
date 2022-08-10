package com.zl.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zl.blog.dao.mapper.ArticleMapper;
import com.zl.blog.dao.pojo.Article;
import org.apache.tomcat.jni.Thread;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author shkstart
 * @create 2022-06-28 20:25
 */
@Component
public class ThreadService {


    @Async("taskExecutor")
    public void updateViewCount(ArticleMapper articleMapper,Article article){

        Article articleUpdate = new Article();
        articleUpdate.setViewCounts(article.getViewCounts() + 1);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());
        queryWrapper.eq(Article::getViewCounts,article.getViewCounts());
        articleMapper.update(articleUpdate,queryWrapper);
    }
}
