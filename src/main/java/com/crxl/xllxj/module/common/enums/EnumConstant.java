package com.crxl.xllxj.module.common.enums;

/** 常量 */
public interface EnumConstant { 
	 
	/** 渠道号 */
	public enum ChannelId {
		test;
	}

	/** 序列号的类型 */
	public enum IdentityId {

		none(0, 0), user(1, 1000001), room(2, 100001);

		/**
		 * @param id
		 * @param initValue
		 */
		private IdentityId(int id, int initValue) {
			this.id = id;
			this.initValue = initValue;
		}

		int id;
		int initValue;

		public int getInitValue() {
			return initValue;
		}

		/**
		 * 获取类型
		 */
		public int getId() {
			return id;
		}
	}

}
