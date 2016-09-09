package com.taotao.manage.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.manage.pojo.ContentCategory;
import com.taotao.manage.service.ContentCategoryService;


@Controller
@RequestMapping("content/category")
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ContentCategory>> queryListByCategoryId(
			@RequestParam(value = "id", defaultValue = "0") Long parentId) {
		if (null == parentId) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		try {
			ContentCategory record = new ContentCategory();
			record.setParentId(parentId);
			List<ContentCategory> list = this.contentCategoryService
					.queryListByWhere(record);
			if (null == list || list.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.status(HttpStatus.OK).body(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				null);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ContentCategory> saveContentCategory(
			ContentCategory contentCategory) {
		if (null == contentCategory) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		try {
			contentCategory.setId(null);
			contentCategory.setIsParent(false);
			contentCategory.setSortOrder(1);
			contentCategory.setStatus(1);
			this.contentCategoryService.saveForParent(contentCategory);
			return ResponseEntity.status(HttpStatus.CREATED).body(
					contentCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				null);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Void> renameContentCategory(
			ContentCategory contentCategory) {
		if (null == contentCategory) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		try {

			this.contentCategoryService.updateSelective(contentCategory);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteContentCategory(
			ContentCategory contentCategory) {
		try {
			//将本节点id添加到ids集合，ids集合用来封装所有将被删除的节点的id
			List<Object> ids = new ArrayList<Object>();
			ids.add(contentCategory.getId());
			findAllSubNode(contentCategory.getId(), ids);
			
			//删除所有子节点
			this.contentCategoryService.deleteByIds(ContentCategory.class, "id",
					ids);
			//看看本节点有没有兄弟节点
			ContentCategory find = new ContentCategory();
			find.setParentId(contentCategory.getParentId());
			List<ContentCategory> list = this.contentCategoryService
					.queryListByWhere(find);
			//如果没有兄弟节点，则设置本节点的父节点的isParent属性为false
			if(null==list||list.isEmpty()){
				ContentCategory parent= new ContentCategory();
				parent.setIsParent(false);
				parent.setId(contentCategory.getParentId());
				this.contentCategoryService.updateSelective(parent);
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	//递归方法查找所有子节点并且把子节点的id加入ids集合
	private void findAllSubNode(Long parentId, List<Object> ids) {
		ContentCategory find = new ContentCategory();
		find.setParentId(parentId);
		List<ContentCategory> list = this.contentCategoryService
				.queryListByWhere(find);
		for (ContentCategory contentCategory : list) {
			ids.add(contentCategory.getId());
			if (contentCategory.getIsParent()) {
				findAllSubNode(contentCategory.getId(), ids);
			}
		}
	}
}
