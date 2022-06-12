package com.xs.myblog.pojo;

public class BlogViews {
    private Integer id;
    private Long blogId;
    private Long typeId;
    private String userIds;

    public BlogViews() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    @Override
    public String toString() {
        return "BlogViews{" +
                "id=" + id +
                ", blogId=" + blogId +
                ", typeId=" + typeId +
                ", userIds='" + userIds + '\'' +
                '}';
    }
}
