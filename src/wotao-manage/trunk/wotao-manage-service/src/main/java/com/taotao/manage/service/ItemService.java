package com.taotao.manage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.mapper.ItemMapper;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;

@Service
public class ItemService extends BaseService<Item>{

    @Autowired
    private ItemDescService itemDescService;
    
    @Autowired
    private ItemParamItemService itemParamItemService;
    
    @Autowired
    private ItemMapper itemMapper;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public void save(Item item, String desc, String itemParams) {

        // 设置初始数据
        item.setStatus(1);
        item.setId(null);// 强制设置id为null
        
        this.save(item);
        
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        // 保存描述数据
        this.itemDescService.save(itemDesc);
        
        ItemParamItem itemParamItem = new ItemParamItem();
        itemParamItem.setId(null);
        itemParamItem.setItemId(item.getId());
        itemParamItem.setParamData(itemParams);
        
        this.itemParamItemService.save(itemParamItem);
        
     // 发送消息
        sendMsg(item.getId(), "insert");
    }

    public PageInfo<Item> queryItemList(Integer page, Integer rows) {
        Example example = new Example(Item.class);
        example.setOrderByClause("updated DESC");
        
        PageHelper.startPage(page, rows);
        
        List<Item> list = this.itemMapper.selectByExample(example);
        
        return new PageInfo<Item>(list);
    }

        public void updateItem(Item item, String desc, String itemParams) {
            
            item.setCreated(null);
            item.setStatus(null);
            this.updateSelective(item);
            
            ItemDesc itemDesc = new ItemDesc();
            itemDesc.setItemId(item.getId());
            itemDesc.setItemDesc(desc);
            this.itemDescService.updateSelective(itemDesc);
            
            this.itemParamItemService.updateItemParamItem(item.getId(),itemParams);
            
            // 发送消息
            sendMsg(item.getId(), "update");
    }

        private void sendMsg(Long itemId, String type) {
            try {
                // 发送MQ消息通知其他系统
                Map<String, Object> msg = new HashMap<String, Object>();
                msg.put("itemId", itemId);
                msg.put("type", type);
                msg.put("date", System.currentTimeMillis());
                this.rabbitTemplate.convertAndSend("item." + type, MAPPER.writeValueAsString(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
