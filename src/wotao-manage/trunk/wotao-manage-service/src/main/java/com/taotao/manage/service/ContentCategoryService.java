package com.taotao.manage.service;

import org.springframework.stereotype.Service;

import com.taotao.manage.pojo.ContentCategory;


@Service
public class ContentCategoryService extends BaseService<ContentCategory> {

	public void saveForParent(ContentCategory contentCategory) {

		this.saveSelective(contentCategory);
		ContentCategory parent = this.queryById(contentCategory.getParentId());
		if (!parent.getIsParent()) {
			parent.setIsParent(true);
			this.updateSelective(parent);
		}
	}
}
