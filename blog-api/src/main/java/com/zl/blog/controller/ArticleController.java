package com.zl.blog.controller;

import com.zl.blog.common.aop.LogAnnotation;
import com.zl.blog.common.cache.Cache;
import com.zl.blog.service.ArticleService;
import com.zl.blog.vo.ArticleVo;
import com.zl.blog.vo.Result;
import com.zl.blog.vo.params.ArticleParam;
import com.zl.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shkstart
 * @create 2022-06-24 0:08
 */
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    //Result是统一结果返回
    @PostMapping
    @LogAnnotation(module = "文章", operation = "获取文章列表")
    //@Cache(expire = 5 * 60 * 1000,name = "hot_article")
    public Result articles(@RequestBody PageParams pageParams) {

        return articleService.listArticle(pageParams);
    }

    /**
     * 最热文章接口
     * @return
     */
    @PostMapping("hot")
    public Result hotArticles() {
        int num = 5;
        return articleService.hotArticles(num);
    }

    /**
     * 最新文章接口
     * @return
     */
    @PostMapping("new")
    public Result newArticles() {
        int num = 5;
        return articleService.newArticles(num);
    }

    /**
     *文章归档
     * @return
     */
    @PostMapping("listArchives")
    public Result listArchives() {
        return articleService.listArchives();
    }

    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long id) {
        ArticleVo articleVo = articleService.findArticleById(id);
        return Result.success(articleVo);
    }

    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }
    @PostMapping("{id}")
    public Result articleById(@PathVariable("id") Long articleId){
        ArticleVo articleVo = articleService.findArticleById(articleId);
        return Result.success(articleVo);
    }

    @PostMapping("search")
    public Result search(@RequestBody ArticleParam articleParam){
        //写一个搜索接口
        String search = articleParam.getSearch();
        return articleService.searchArticle(search);
    }
}
