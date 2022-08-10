package com.zl.blog.service;

import com.zl.blog.vo.Result;
import com.zl.blog.vo.params.CommentParam;

/**
 * @author shkstart
 * @create 2022-06-28 21:12
 */
public interface CommentsService {

    /**
     * 根据文章id查询所有的评论
     * @param articleId
     * @return
     */
    Result commentsByArticleId(Long articleId);

    /**
     * 评论功能
     * @param commentParam
     * @return
     */
    Result comment(CommentParam commentParam);
}
