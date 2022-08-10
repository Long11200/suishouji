package com.zl.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zl.blog.dao.mapper.ArticleMapper;
import com.zl.blog.dao.mapper.CommentMapper;
import com.zl.blog.dao.pojo.Article;
import com.zl.blog.dao.pojo.Comment;
import com.zl.blog.dao.pojo.SysUser;
import com.zl.blog.service.CommentsService;
import com.zl.blog.service.SysUserService;
import com.zl.blog.utils.UserThreadLocal;
import com.zl.blog.vo.CommentVo;
import com.zl.blog.vo.Result;
import com.zl.blog.vo.UserVo;
import com.zl.blog.vo.params.CommentParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-28 21:13
 */
@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Result commentsByArticleId(Long articleId) {
        /**
         * 1.根据文章id 查询 评论列表 从comment表中查询
         * 2.根据作者id 查询作者的信息
         * 3.判断 如果 level == 1
         *      查询子评论 根据评论的父评论(parent_id) 进行查询
         */
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>();
        queryWrapper.eq(Comment::getArticleId,articleId);
        queryWrapper.eq(Comment::getLevel,1);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList = copyList(comments);
        return Result.success(commentVoList);
    }

    @Override
    public Result comment(CommentParam commentParam) {
        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParam.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParam.getParent();
        if (parent == null || parent == 0) {
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParam.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        this.commentMapper.insert(comment);
        Article article = articleMapper.selectById(commentParam.getArticleId());
        article.setCommentCounts(article.getCommentCounts() + 1);
        this.articleMapper.updateById(article);

        CommentVo commentVo = copy(comment);
        return Result.success(commentVo);
    }

    private List<CommentVo> findChildrenComments(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        return  copyList(commentMapper.selectList(queryWrapper));
    }

    private List<CommentVo> copyList(List<Comment> comments) {
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment: comments) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setId(String.valueOf(comment.getId()));
        //加入作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = this.sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);

        Integer level = comment.getLevel();
        //如果是一级评论,则加入子评论
        if(level == 1) {
            Long id = comment.getId();
            List<CommentVo> commentVoList = findChildrenComments(id);
            commentVo.setChildrens(commentVoList);
        }
        //如果此评论是二级评论,填充to User 记录给谁评论
        if(level > 1) {
            Long toUid = comment.getToUid();
            UserVo toUserVo = this.sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }
        return commentVo;
    }
}
