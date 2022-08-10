package com.zl.blog.service;

import com.zl.blog.vo.Result;
import com.zl.blog.vo.TagVo;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-25 10:42
 */
public interface TagService {
    List<TagVo> findTagsByArticleId(long articleId);

    Result hots(int num);

    Result findAll();

    Result findAllDetail();

    Result findDetailById(Long id);
}
