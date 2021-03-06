package com.zero.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.utils.BeanUtils;
import com.zero.common.utils.Md5;
import com.zero.mapper.UserMapper;
import com.zero.model.User;
import com.zero.model.example.UserExample;
import com.zero.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements IUserService{

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    private LoadingCache<Integer, String> userCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, String>() {
                @Override
                public String load(Integer key) {
                    return getUserById(key).getRealName();
                }
            });


    @Override
    public String getUserName(int id) {
        try {
            return userCache.get(id);
        } catch (ExecutionException e) {
            LOGGER.error("缓存获取用户信息失败:", e);
            return null;
        }
    }

    @Override
    public List<User> findUserPage(String phone, String loginName, String realName, Integer pageNum, Integer pageSize) {
        UserExample example = new UserExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(realName)){
            criteria.andRealNameLike("%" + realName + "%");
        }
        if(StringUtils.isNotEmpty(loginName)){
            criteria.andLoginNameLike("%" + loginName + "%");
        }
        if(StringUtils.isNotEmpty(phone)){
            criteria.andPhoneLike("%" + phone + "%");
        }
        return userMapper.selectByExample(example);
    }

    @Override
    public long findRowNum(String phone, String loginName, String realName) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(realName)){
            criteria.andRealNameLike("%" + realName + "%");
        }
        if(StringUtils.isNotEmpty(loginName)){
            criteria.andLoginNameLike("%" + loginName + "%");
        }
        if(StringUtils.isNotEmpty(phone)){
            criteria.andPhoneLike("%" + phone + "%");
        }
        return userMapper.countByExample(example);
    }

    @Override
    public int insert(User user) {
        user.setCreateTime(new Date());
        user.setPassword(Md5.digest("123456"));
        return userMapper.insertSelective(user);
    }

    @Override
    public int update(User user, int userId) {
        if(Objects.isNull(user) || Objects.isNull(user.getId())){
            return 0;
        }
        User userDb = this.getUserById(user.getId());
        if (Objects.isNull(userDb)) {
            return 0;
        }
        BeanUtils.copyProperties(user, userDb);
        userDb.setModifier(userId);
        userDb.setUpdateTime(new Date());
        return userMapper.updateByPrimaryKey(userDb);
    }

    @Override
    public User getUserById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteUserById(int id, int userId) {
        User user = this.getUserById(id);
        if (Objects.isNull(user)) {
            return 0;
        }
        user.setIsDeleted(DeletedEnum.YES.getKey());
        user.setModifier(userId);
        user.setUpdateTime(new Date());
        return userMapper.updateByPrimaryKey(user);
    }

    /**
     * 登录验证
     * @param loginName
     * @param pwd
     * @return
     */
    @Override
    public User login(String loginName, String pwd) {
        User user = userMapper.login(loginName);
        if (Objects.isNull(user)) {
            return null;
        }
        final String currentPwd = Md5.digest(pwd);
        if(StringUtils.equals(currentPwd, user.getPassword())){
            user.setPassword("");
            return user;
        }
        return null;
    }

    @Override
    public int batchDeleteUser(List<Integer> list, int modifier) {
        return userMapper.batchDeleteUser(list, modifier);
    }

    @Override
    public List<User> findUserByRole(int roleId) {
        return Optional.ofNullable(userMapper.findUserByRole(roleId)).orElse(Lists.newArrayList());
    }
}
