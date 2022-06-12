package com.xs.myblog.dao;


import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.BlogViews;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogDao{

    List<Blog> findTopByRecommend(Integer size);



    @Modifying
    @Transactional
    int updateViews(Long id);

    List<String> findGroupYear();

    List<Blog> findByYear(String year);

    Blog findById(Long id);

    List<Blog> findByTypeId(Long typeId);
    List<Blog> findByTagId(Long tagId);

    Page<Blog> findAll(Specification<Blog> blogSpecification, Pageable pageable);

    Page<Blog> findAll(Pageable pageable);

    List<Blog> findAll();

    Blog getBlogById(Long id);

    Long count();

    int insertBlog(Blog blog);

    void deleteById(Long id);

    int updateBlog(Blog blog);

    List<Blog> searchBlog(Blog blog);
    List<Blog> searchBlogByQuery(String query);

    List<Blog> findAllByUser(Integer userId);

    List<Blog> findAllByUserAndPublish(Integer userId);

    List<Blog> findAllBlogAndUser();

    Blog getAllBlogAndUserById(Integer userId, Long blogId);

    void insertBlogViews(@Param(value = "blogId") Long blogId, @Param(value = "typeId") Long typeId, @Param(value = "userIds") String userIds);

    void updateBlogViewsUsers(@Param(value = "blogId") Long blogId, @Param(value = "userId") Long userId);

    List<BlogViews> getBlogViews(String id);

    void setRecommend(Long blogId);
}
