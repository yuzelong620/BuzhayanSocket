package com.crxl.xllxj.module.util;

import java.util.UUID;
/**
 *id工具类 
 */
public class IdUtil {
     /**
      * 获取随机的uuid
      * @return
      */
	 public static String randomUUID(){
		 return UUID.randomUUID().toString().replace("-", "");
	 }
	 
}
