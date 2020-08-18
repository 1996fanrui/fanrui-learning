package com.dream.guava.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserCacheLoadService {

    // key: userId, value: userName
    @SuppressWarnings("all")
    private LoadingCache<Long, String> cache;

    private UserDao userDao;

    public UserCacheLoadService() {
        this.userDao = new UserDao();
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, String>() {
                    @Override
                    public String load(Long userId) throws Exception {
                        return userDao.queryUserNameById(userId);
                    }
                });
    }

    public void clearCache() {
        cache.invalidateAll();
        userDao.clearCounter();
    }

    public long getQueryDBCounter() {
        return userDao.getCounter();
    }

    public String queryWithCache(long userId) {
        try {
            return cache.get(userId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }
}
