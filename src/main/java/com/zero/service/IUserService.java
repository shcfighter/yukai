package com.zero.service;


import com.zero.model.User;

import java.util.List;

public interface IUserService {

	List<User> findUserPage(String phone, String email, String realName, int pageNum, int pageSize);

	long findRowNum(String phone, String email, String realName);

	int insert(User user);

	int update(User user, int userId);

	User getUserById(int id);

	int deleteUserById(int id, int userId);

	User login(String loginName, String pwd);

    int batchDeleteUser(List<Integer> list, int modifier);
}
