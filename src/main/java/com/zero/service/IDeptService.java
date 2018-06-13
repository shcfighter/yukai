package com.zero.service;

import com.zero.model.Dept;

import java.util.List;

public interface IDeptService {

    Dept getDeptById(int id);

    int insert(Dept dept, int userId);

    int update(Dept dept, int userId);

    int delete(int id, int userId);

    long findRowNum(String deptName, String deptCode);

    List<Dept> findDeptPage(String deptName, String deptCode, int pageNum, int pageSize);

    List<Dept> findDept(String deptName, String deptCode);

    List<Dept> findDeptTree();
}
