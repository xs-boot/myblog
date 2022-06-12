package com.xs.myblog.dao;


import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserDao {

    User findByUsernameAndPassword(@Param(value = "username")String username,@Param(value = "password") String password);

    void updatePassword(Long id, String password);

    void registerUser(@Param(value = "username") String username, @Param(value = "password") String password, @Param(value = "email") String email);

    void updateInformation(User user);

    List<Integer> getAuthority(Long userId);

    List<String> getAuthorityName(Long userId);

    List<User> getAllUsers();

    void deleteAuthority(@Param(value = "userId") Long userId, @Param(value = "aId") int aId);

    void addAuthority(@Param(value = "userId") Long userId, @Param(value = "aId") int aId);

    User getUserByIdOrName(@Param(value = "userId") Long userId, @Param(value = "username") String username);

    void addFriend(@Param(value = "userId") Long userId, @Param(value = "adminId") Long adminId);

    void deleteFriend(@Param(value = "userId") Long userId, @Param(value = "friendId") Long friendId);

    List<Long> getAllFriendIdByUserId(Long userId);

    int getFansNumbers(Long userId);

    List<Blog> getAllBlogsByUserId(Long userId);

    List<Long> getAllFanIds(Long userId);

    List<User> getUsersLikeName(String username);

    String getPasswordByUserId(Long userId);

    User getUserByEmailAndPassword(@Param(value = "email") String email, @Param(value = "password") String password);

    List<User> getUserByNameAndAuthority(@Param(value = "username")String username,
                                         @Param(value = "edit")Integer edit,
                                         @Param(value = "speak")Integer speak,
                                         @Param(value = "comment")Integer comment);

    //管路员功能
    List<User> getAllUser();
}
