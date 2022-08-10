package com.zl.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zl.blog.dao.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shkstart
 * @create 2022-06-28 21:14
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
