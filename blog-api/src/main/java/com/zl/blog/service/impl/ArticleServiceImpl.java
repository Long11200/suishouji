package com.zl.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zl.blog.dao.dos.Archives;
import com.zl.blog.dao.mapper.ArticleBodyMapper;
import com.zl.blog.dao.mapper.ArticleMapper;
import com.zl.blog.dao.mapper.ArticleTagMapper;
import com.zl.blog.dao.pojo.Article;
import com.zl.blog.dao.pojo.ArticleBody;
import com.zl.blog.dao.pojo.ArticleTag;
import com.zl.blog.dao.pojo.SysUser;
import com.zl.blog.service.*;
import com.zl.blog.utils.UserThreadLocal;
import com.zl.blog.vo.*;
import com.zl.blog.vo.params.ArticleParam;
import com.zl.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shkstart
 * @create 2022-06-24 11:28
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records, true, true));
    }

    @Override
    public Result hotArticles(int num) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle,Article::getAuthorId);
        queryWrapper.last("limit " + num);
        List<Article> articleList = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articleList, false, false));
    }

    @Override
    public Result newArticles(int num) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle, Article::getCreateDate,Article::getAuthorId);
        queryWrapper.last("limit " + num);
        List<Article> articleList = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articleList, false, false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Override
    public Result findArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        ArticleVo articleVo = copy(article, true, true, true, true);
        //查看完文章后,新增阅读数
        //新增阅读数为更新操作,更新时写锁,阻塞了读操作,无法直接返回数据了
        //更新增加了接口的耗时,如果一旦更新出问题,不能影响查看文章的操作
        //解决:线程池 把更新操作扔到线程池中去执行,和主线程就不相关了
        threadService.updateViewCount(articleMapper, article);
        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParam articleParam) {
        //此接口加入登录拦截
        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1.发布文章 构建Article对象
         * 2.标签 将标签加入到 标签_文章关联表中
         * 3.body 内容存储 加入到内容_文章关联表中
         */
        Article article = new Article();
        boolean isEdit = false;
        if (articleParam.getId() != null){
            article = new Article();
            article.setId(articleParam.getId());
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
            articleMapper.updateById(article);
            isEdit = true;
        }else{
            article = new Article();
            article.setAuthorId(sysUser.getId());
            article.setWeight(Article.Article_Common);
            article.setViewCounts(0);
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCommentCounts(0);
            article.setCreateDate(System.currentTimeMillis());
            article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
            //插入之后 会生成一个文章id
            this.articleMapper.insert(article);
        }
        //tag
        List<TagVo> tags = articleParam.getTags();
        if (tags != null){
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                if (isEdit){
                    //先删除
                    LambdaQueryWrapper<ArticleTag> queryWrapper = Wrappers.lambdaQuery();
                    queryWrapper.eq(ArticleTag::getArticleId,articleId);
                    articleTagMapper.delete(queryWrapper);
                }
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        if (isEdit){
            ArticleBody articleBody = new ArticleBody();
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParam.getBody().getContent());
            articleBody.setContentHtml(articleParam.getBody().getContentHtml());
            LambdaUpdateWrapper<ArticleBody> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(ArticleBody::getArticleId,article.getId());
            articleBodyMapper.update(articleBody, updateWrapper);
        }else {
            ArticleBody articleBody = new ArticleBody();
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParam.getBody().getContent());
            articleBody.setContentHtml(articleParam.getBody().getContentHtml());
            articleBodyMapper.insert(articleBody);

            article.setBodyId(articleBody.getId());
            articleMapper.updateById(article);
        }
        Map<String,String> map = new HashMap<>();
        map.put("id",article.getId().toString());

        if (isEdit) {
            //发布消息,更新缓存
            ArticleMessage articleMessage = new ArticleMessage();
            articleMessage.setArticleId(article.getId());
            rabbitTemplate.convertAndSend("blog-update-article",articleMessage);
        }

        return Result.success(map);
    }

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, true, true, false, false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, true, true, isBody, isCategory));
        }
        return articleVoList;
    }


    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            SysUser user = sysUserService.findUserById(authorId);
            UserVo userVo = new UserVo();
            userVo.setAvatar(user.getAvatar());
            userVo.setId(user.getId().toString());
            user.setNickname(user.getNickname());
            articleVo.setAuthor(userVo);
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    @Override
    public Result searchArticle(String search) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle,Article::getAuthorId);
        queryWrapper.like(Article::getTitle,search);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }

}
/*

 */