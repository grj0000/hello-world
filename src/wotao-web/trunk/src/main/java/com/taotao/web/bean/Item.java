package com.taotao.web.bean;

import org.apache.commons.lang3.StringUtils;

public class Item extends com.taotao.manage.pojo.Item{
	
	public String[] getImages(){
		
//		return super.getImage().split(",");//性能较低
		return StringUtils.split(super.getImage(), ",");
		
		//StringUtils.split(super.getImage(), ",");内置下面的语句
		/*if(null==super.getImage()){
			return null;
		}*/
	}
}
