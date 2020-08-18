package com.dream.guava.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class UserService {

    // key: userId, value: userName
    @SuppressWarnings("all")
    private Cache<Long, String> cache;

    private UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    public void clearCache() {
        cache.invalidateAll();
        userDao.clearCounter();
    }

    public long getQueryDBCounter() {
        return userDao.getCounter();
    }

    public String queryWithoutCache(long userId) {
        return userDao.queryUserNameById(userId);
    }

    public String queryWithCache(long userId) {
        String userName = cache.getIfPresent(userId);
        if (userName != null) {
            return userName;
        }
        userName = userDao.queryUserNameById(userId);
        cache.put(userId, userName);
        return userName;
    }

}
