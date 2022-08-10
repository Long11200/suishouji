package com.zl.blog.service;

import com.zl.blog.vo.ArticleVo;
import com.zl.blog.vo.Result;
import com.zl.blog.vo.params.ArticleParam;
import com.zl.blog.vo.params.PageParams;

/**
 * @author shkstart
 * @create 2022-06-24 11:23
 */

public interface ArticleService {

    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 最热文章
     * @param num
     * @return
     */
    Result hotArticles(int num);

    /**
     * 最新文章
     * @param num
     * @return
     */
    Result newArticles(int num);

    /**
     * 文章归档
     * @return
     */
    Result listArchives();

    ArticleVo findArticleById(Long id);

    Result publish(ArticleParam articleParam);

    /**
     * 文章搜索
     * @param search
     * @return
     */
    Result searchArticle(String search);
}
