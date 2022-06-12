package com.xs.myblog.service;


import com.xs.myblog.pojo.Tag;
import com.xs.myblog.pojo.Type;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {

    int saveType(Type type);

    Type getType(Long id);

    Type getTypeByName(String name);

    Page<Type> listType(Pageable pageable);

    List<Type> listType();

    List<Type> findAllByName(String name);

    List<Type> listTypeTop(Integer size);

    int updateType(Long id, Type type);

    int deleteType(Long id);

    List<Type> getAllType();

    List<Tag> getTags(Long typeId);

    List<Tag> getTagsByUserId(Long typeId,Long userId);
}
