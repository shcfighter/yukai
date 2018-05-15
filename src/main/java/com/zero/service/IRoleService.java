package com.zero.service;

import com.zero.model.Role;

import java.util.List;

public interface IRoleService {

    Role getRoleById(int id);

    int insert(Role role, int userId);

    int update(Role role, int userId);

    int delete(int id, int userId);

    long findRowNum(String RoleName, String RoleCode);

    List<Role> findRolePage(String RoleName, String RoleCode, int pageNum, int pageSize);
}
