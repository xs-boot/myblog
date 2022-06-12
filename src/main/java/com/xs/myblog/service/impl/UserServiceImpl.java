package com.xs.myblog.service.impl;


import com.xs.myblog.dao.UserDao;
import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.UserService;
import com.xs.myblog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User checkUser(String username, String password) {
        return userDao.findByUsernameAndPassword(username, MD5Utils.code(password));
    }

    @Override
    public void updatePassword(Long id, String password) {
        userDao.updatePassword(id, password);
    }



    @Override
    public void registerUser(String username, String password, String email) {
        userDao.registerUser(username, MD5Utils.code(password), email);
    }

    @Override
    public void updateInformation(User user) {
        userDao.updateInformation(user);
    }

    @Override
    public List<Integer> getAuthority(Long userId) {
        return userDao.getAuthority(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public void addAuthority(Long userId, int aId) {
        userDao.addAuthority(userId, aId);
    }

    @Override
    public void deleteAuthority(Long userId, int aId) {
        userDao.deleteAuthority(userId, aId);
    }

    @Override
    public User getUserByIdOrName(Long userId, String username) {
        return userDao.getUserByIdOrName(userId, username);
    }

    @Override
    public void addFriend(Long userId, Long adminId) {
        userDao.addFriend(userId, adminId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userDao.deleteFriend(userId, friendId);
    }

    @Override
    public List<Long> getAllFriendIdByUserId(Long userId) {
        return userDao.getAllFriendIdByUserId(userId);
    }

    @Override
    public int getFansNumber(Long userId) {
        return userDao.getFansNumbers(userId);
    }

    @Override
    public List<Blog> getAllBlogsByUserId(Long userId) {
        return userDao.getAllBlogsByUserId(userId);
    }

    @Override
    public List<String> getAuthorityName(Long userId) {
        return userDao.getAuthorityName(userId);
    }

    @Override
    public List<Long> getAllFanIds(Long userId) {
        return userDao.getAllFanIds(userId);
    }

    @Override
    public List<User> getUsersLikeName(String username) {
        return userDao.getUsersLikeName(username);
    }

    @Override
    public String getPasswordByUserId(Long userId) {
        return userDao.getPasswordByUserId(userId);
    }

    @Override
    public User getUserByEmailAndPassword(String email, String password) {
        return userDao.getUserByEmailAndPassword(email, MD5Utils.code(password));
    }

    @Override
    public List<User> getUserByNameAndAuthority(String username, Integer edit, Integer speak, Integer comment) {
        return null;
    }
}
