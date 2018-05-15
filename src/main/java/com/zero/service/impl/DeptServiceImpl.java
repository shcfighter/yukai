package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.DeptMapper;
import com.zero.model.Dept;
import com.zero.model.example.DeptExample;
import com.zero.service.IDeptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class DeptServiceImpl implements IDeptService {

    @Resource
    private DeptMapper deptMapper;

    @Override
    public Dept getDeptById(int id) {
        return deptMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(Dept dept, int userId) {
        dept.setCreater(userId);
        dept.setCreateTime(new Date());
        return deptMapper.insertSelective(dept);
    }

    @Override
    public int update(Dept dept, int userId) {
        if(Objects.isNull(dept) || Objects.isNull(dept.getId())){
            return 0;
        }
        Dept deptDb = this.getDeptById(dept.getId());
        if (Objects.isNull(deptDb)) {
            return 0;
        }
        BeanUtils.copyProperties(dept, deptDb);
        deptDb.setModifier(userId);
        deptDb.setUpdateTime(new Date());
        return deptMapper.updateByPrimaryKeySelective(deptDb);
    }

    @Override
    public int delete(int id, int userId) {
        Dept dept = this.getDeptById(id);
        if (Objects.isNull(dept)) {
            return 0;
        }
        dept.setIsDeleted(DeletedEnum.YES.getKey());
        dept.setModifier(userId);
        dept.setUpdateTime(new Date());
        return deptMapper.updateByPrimaryKeySelective(dept);
    }

    @Override
    public long findRowNNum(String deptName, String deptCode) {
        DeptExample example = new DeptExample();
        DeptExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(deptName)){
            criteria.andDeptNameLike("%" + deptName + "%");
        }
        if(StringUtils.isNotEmpty(deptCode)){
            criteria.andDeptCodeLike("%" + deptCode + "%");
        }
        return deptMapper.countByExample(example);
    }

    @Override
    public List<Dept> findDeptPage(String deptName, String deptCode, int pageNum, int pageSize) {
        DeptExample example = new DeptExample();
        example.setPage(pageNum);
        example.setLimit(pageSize);
        DeptExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(deptName)){
            criteria.andDeptNameLike("%" + deptName + "%");
        }
        if(StringUtils.isNotEmpty(deptCode)){
            criteria.andDeptCodeLike("%" + deptCode + "%");
        }
        return deptMapper.selectByExample(example);
    }

    @Override
    public List<Dept> findDept(String deptName, String deptCode) {
        DeptExample example = new DeptExample();
        DeptExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(deptName)){
            criteria.andDeptNameLike("%" + deptName + "%");
        }
        if(StringUtils.isNotEmpty(deptCode)){
            criteria.andDeptCodeLike("%" + deptCode + "%");
        }
        return deptMapper.selectByExample(example);
    }

    @Override
    public List<Dept> findDeptTree() {
        List<Dept> list = this.findDept(null, null);
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }

        return this.sortMenus(list, null);
    }

    /**
     * 组织部门树
     * @param menus
     * @return
     */
    private List<Dept> sortMenus(List<Dept> menus, List<Dept> pMenus) {
        if (CollectionUtils.isEmpty(menus)) {
            return Lists.newArrayList();
        }

        if(CollectionUtils.isEmpty(pMenus)){
            List<Dept> firstMenus = Lists.newArrayList();
            menus.forEach(menu -> {
                if(null == menu.getpId()){
                    firstMenus.add(menu);
                }
            });
            this.sortMenus(menus, firstMenus);
            return firstMenus;
        }

        for (Dept pMenu : pMenus) {
            List<Dept> childrenMenus = Lists.newArrayList();
            menus.forEach(menu -> {
                if(pMenu.getId().equals(menu.getpId())){
                    childrenMenus.add(menu);
                }
            });
            pMenu.setChildren(childrenMenus);
            if (0 == childrenMenus.size()) {
                continue;
            }
            this.sortMenus(menus, childrenMenus);
        }

        return Lists.newArrayList();
    }
}
