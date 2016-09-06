package com.taotao.manage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.manage.mapper.ItemCatMapper;
import com.taotao.manage.pojo.ItemCat;

@Service
public class ItemCatService extends BaseService<ItemCat> {

    @Autowired
    private ItemCatMapper itemCatMapper;

    /**
     * 全部查询，并且生成树状结构
     * 
     * @return
     */
//    public List<ItemCat> queryItemCatByParentId(Long parentId) {
//
//        ItemCat record = new ItemCat();
//        record.setParentId(parentId);
//        return this.itemCatMapper.select(record);
//    }

//    @Override
//    public Mapper<ItemCat> getMapper() {
//        return this.itemCatMapper;
//    }
}
