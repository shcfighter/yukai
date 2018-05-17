package com.zero.service;


import com.zero.model.Menu;

import java.util.List;

public interface IMenuService {

	List<Menu> findMenu();

	List<Menu> findMenuTree(Integer roleId);

	Menu getMenuById(int id);

	List<Menu> findMenus(int userId);
}
