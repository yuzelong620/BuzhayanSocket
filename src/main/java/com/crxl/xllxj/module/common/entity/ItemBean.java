package com.crxl.xllxj.module.common.entity;

public class ItemBean {
    private int itemId;
    private int itemNum;
	public ItemBean() {
		 
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
	public ItemBean(int itemId, int itemNum) {
		super();
		this.itemId = itemId;
		this.itemNum = itemNum;
	}
	 

}
