package com.zero.common;

public class BaseExample {

	/**
	 * 每页默认为10条
	 */
	private Integer limit;
	/**
	 * 偏移量
	 */
	private Integer offset = 0;
	/**
	 * 第几页
	 */
	private int page = 1;

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return limit * (page - 1);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void putPage(int limit, int page) {
		this.limit = limit;
		this.page = page;
	}
}
