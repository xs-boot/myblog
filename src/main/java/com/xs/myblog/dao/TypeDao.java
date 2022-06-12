package com.xs.myblog.dao;


import com.xs.myblog.pojo.Tag;
import com.xs.myblog.pojo.Type;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface TypeDao {

    Type findByName(String name);

    List<Type> findTop(Integer size);

    int update(Type type);

    Type findById(Long id);

    Page<Type> findAll(Pageable pageable);

    List<Type> findAll();

    List<Type> findAllByName(String name);

    int deleteById(Long id);
    int getCountBlogUserType(Long id);

    int save(Type type);

    List<Tag> getTags(Long typeId);

    List<Tag> getTagsByUserId(@Param(value = "typeId") Long typeId,@Param(value = "userId") Long userId);
}
