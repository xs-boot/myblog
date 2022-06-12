package com.xs.myblog.service;



import com.xs.myblog.pojo.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface TagService {

    int saveTag(Tag type);

    Tag getTag(Long id);

    Tag getTagByName(String name);

    Page<Tag> listTag(Pageable pageable);

    List<Tag> listTag();

    List<Tag> listTagTop(Integer size);

    List<Tag> listTag(String ids);

    int insertBlogTag(Long blogId, String tagsId);

    int updateTag(Long id, Tag type);

    int deleteTag(Long id);

    List<Tag> findAllByName(String name);

    void addTypeTag(Long typeId, Long tagId);
    void updateTypeTag(Long afterTagId, Long id);

    Long getTypeTagId(Long tagId);

    List<Tag> findAllTagAndType(Long userId);

    List<Tag> searchTag(String tagname,boolean isMine,Long userId);
}
