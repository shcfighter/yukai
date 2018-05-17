package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.ValidEnum;
import com.zero.mapper.MenuMapper;
import com.zero.mapper.RoleMenuMapper;
import com.zero.model.Menu;
import com.zero.model.RoleMenu;
import com.zero.model.example.MenuExample;
import com.zero.model.example.RoleMenuExample;
import com.zero.service.IMenuService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MenuServiceImpl implements IMenuService {

    @Resource
    private MenuMapper menuMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Menu> findMenu() {
        MenuExample example = new MenuExample();
        example.setOrderByClause("sort asc");
        MenuExample.Criteria criteria = example.createCriteria();
        criteria.andIsValidEqualTo(ValidEnum.YES.getKey());
        return menuMapper.selectByExample(example);
    }

    @Override
    public List<Menu> findMenuTree(Integer roleId) {
        List<Menu> list = this.findMenu();
        checked(list, this.getMenuByRole(roleId));
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        return this.sortMenus(list, null);
    }

    private void checked(List<Menu> menus, List<RoleMenu> roleMenus){
        if(CollectionUtils.isEmpty(roleMenus)){
            return ;
        }
        menus.forEach(menu -> {
            roleMenus.forEach(roleMenu -> {
                if(menu.getId().equals(roleMenu.getmId())){
                    menu.setChecked(true);
                }
            });
        });
    }

    private List<RoleMenu> getMenuByRole(Integer roleId){
        if(Objects.isNull(roleId)){
            return Lists.newArrayList();
        }
        RoleMenuExample example = new RoleMenuExample();
        RoleMenuExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andRIdEqualTo(roleId);
        return Optional.ofNullable(roleMenuMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }

    /**
     * 组织菜单树
     * @param menus
     * @return
     */
    private List<Menu> sortMenus(List<Menu> menus, List<Menu> pMenus) {
        if (CollectionUtils.isEmpty(menus)) {
            return Lists.newArrayList();
        }

        if(CollectionUtils.isEmpty(pMenus)){
            List<Menu> firstMenus = Lists.newArrayList();
            menus.forEach(menu -> {
                if(null == menu.getpId()){
                    firstMenus.add(menu);
                }
            });
            this.sortMenus(menus, firstMenus);
            return firstMenus;
        }

        for (Menu pMenu : pMenus) {
            List<Menu> childrenMenus = Lists.newArrayList();
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

    @Override
    public Menu getMenuById(int id) {
        return menuMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Menu> findMenus(int userId) {
        return sortMenus(menuMapper.findMenus(userId), null);
    }
}
