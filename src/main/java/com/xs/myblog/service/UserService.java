package com.xs.myblog.service;


import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.User;


import java.util.List;

public interface UserService {

    User checkUser(String username, String password);

    void updatePassword(Long id, String password);

    void registerUser(String username,String password,String email);

    void updateInformation(User user);

    List<Integer> getAuthority(Long userId);

    List<User> getAllUsers();

    void addAuthority(Long userId, int aId);

    void deleteAuthority(Long userId, int aId);

    User getUserByIdOrName(Long userId, String username);

    void addFriend(Long userId, Long adminId);

    void deleteFriend(Long userId, Long friendId);

    List<Long> getAllFriendIdByUserId(Long userId);

    int getFansNumber(Long userId);

    List<Blog> getAllBlogsByUserId(Long userId);

    List<String> getAuthorityName(Long userId);

    List<Long> getAllFanIds(Long userId);

    List<User> getUsersLikeName(String username);

    String getPasswordByUserId(Long userId);

    User getUserByEmailAndPassword(String email, String password);

    List<User> getUserByNameAndAuthority(String username, Integer edit, Integer speak, Integer comment);
}
