package com.taotao.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.mapper.ItemMapper;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;

@Service
public class ItemService extends BaseService<Item>{

    @Autowired
    private ItemDescService itemDescService;
    
    @Autowired
    private ItemMapper itemMapper;
    
    public void save(Item item, String desc) {

        // 设置初始数据
        item.setStatus(1);
        item.setId(null);// 强制设置id为null
        
        this.save(item);
        
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        // 保存描述数据
        this.itemDescService.save(itemDesc);
    }

    public PageInfo<Item> queryItemList(Integer page, Integer rows) {
        Example example = new Example(Item.class);
        example.setOrderByClause("updated DESC");
        
        PageHelper.startPage(page, rows);
        
        List<Item> list = this.itemMapper.selectByExample(example);
        
        return new PageInfo<Item>(list);
    }

        public void updateItem(Item item, String desc) {
            
            item.setCreated(null);
            item.setStatus(null);
            this.updateSelective(item);
            
            ItemDesc itemDesc = new ItemDesc();
            itemDesc.setItemId(item.getId());
            itemDesc.setItemDesc(desc);
            this.itemDescService.updateSelective(itemDesc);
            
    }

}
