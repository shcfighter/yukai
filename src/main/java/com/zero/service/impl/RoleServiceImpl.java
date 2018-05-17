package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.RoleMapper;
import com.zero.mapper.RoleMenuMapper;
import com.zero.mapper.UserRoleMapper;
import com.zero.model.Role;
import com.zero.model.UserRole;
import com.zero.model.example.RoleExample;
import com.zero.model.example.RoleMenuExample;
import com.zero.model.example.UserRoleExample;
import com.zero.service.IRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl implements IRoleService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public Role getRoleById(int id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(Role role, int userId) {
        role.setCreater(userId);
        role.setCreateTime(new Date());
        return roleMapper.insertSelective(role);
    }

    @Override
    public int update(Role role, int userId) {
        if(Objects.isNull(role) || Objects.isNull(role.getId())){
            return 0;
        }
        Role roleDb = this.getRoleById(role.getId());
        if (Objects.isNull(roleDb)) {
            return 0;
        }
        BeanUtils.copyProperties(role, roleDb);
        roleDb.setModifier(userId);
        roleDb.setUpdateTime(new Date());
        return roleMapper.updateByPrimaryKeySelective(roleDb);
    }

    @Override
    public int delete(int id, int userId) {
        Role role = this.getRoleById(id);
        if (Objects.isNull(role)) {
            return 0;
        }
        role.setIsDeleted(DeletedEnum.YES.getKey());
        role.setModifier(userId);
        role.setUpdateTime(new Date());
        return roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public long findRowNum(String RoleName, String RoleCode) {
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(RoleName)){
            criteria.andRoleNameLike("%" + RoleName + "%");
        }
        if(StringUtils.isNotEmpty(RoleCode)){
            criteria.andRoleCodeLike("%" + RoleCode + "%");
        }
        return roleMapper.countByExample(example);
    }

    @Override
    public List<Role> findRolePage(String RoleName, String RoleCode, int pageNum, int pageSize) {
        RoleExample example = new RoleExample();
        example.setPage(pageNum);
        example.setLimit(pageSize);
        RoleExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(RoleName)){
            criteria.andRoleNameLike("%" + RoleName + "%");
        }
        if(StringUtils.isNotEmpty(RoleCode)){
            criteria.andRoleCodeLike("%" + RoleCode + "%");
        }
        return roleMapper.selectByExample(example);
    }

    @Transactional
    @Override
    public int authMenuToRole(int roleId, List<Integer> menuIds, int userId) {
        RoleMenuExample example = new RoleMenuExample();
        RoleMenuExample.Criteria criteria = example.createCriteria();
        criteria.andRIdEqualTo(roleId);
        roleMenuMapper.deleteByExample(example);
        return roleMenuMapper.insertBatch(menuIds, roleId, userId);
    }

    @Override
    public int authUserToRole(int roleId, int userId, int loginId) {
        UserRole userRole = new UserRole();
        userRole.setrId(roleId);
        userRole.setuId(userId);
        userRole.setCreater(loginId);
        userRole.setCreateTime(new Date());
        return userRoleMapper.insertSelective(userRole);
    }

    @Override
    public int unauthUserToRole(int roleId, int userId) {
        UserRoleExample example = new UserRoleExample();
        UserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andRIdEqualTo(roleId);
        criteria.andUIdEqualTo(userId);
        return userRoleMapper.deleteByExample(example);
    }
}
