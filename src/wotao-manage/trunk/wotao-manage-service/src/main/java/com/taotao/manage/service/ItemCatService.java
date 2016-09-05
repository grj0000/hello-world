package com.taotao.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.manage.mapper.ItemCatMapper;
import com.taotao.manage.pojo.ItemCat;

@Service
public class ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;
	/**
	 * 全部查询，并且生成树状结构
	 * 
	 * @return
	 */
	public List< ItemCat> queryItemCat(Long parentId) {

		ItemCat record = new ItemCat();
		record.setParentId(parentId);
        return this.itemCatMapper.select(record );
	}
}
