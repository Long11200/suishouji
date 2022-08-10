package com.zl.blog.service;

import com.zl.blog.vo.CategoryVo;
import com.zl.blog.vo.Result;

import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-28 11:47
 */
public interface CategoryService {

    CategoryVo findCategoryById(Long categoryId);

    /**
     * 查找所有标签
     * @return
     */
    Result findAll();


    /**
     * 查找所有标签详情
     * @return
     */
    Result findAllDetail();

    Result categoryDetailById(Long id);
}
