package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.manage.pojo.ItemDesc;
import com.taotao.web.bean.Item;
import com.taotao.web.service.ItemService;

@Controller
@RequestMapping("item")
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ModelAndView showDetail(@PathVariable("itemId")Long itemId) {
		ModelAndView modelAndView = new ModelAndView("item");
		
		Item item = this.itemService.queryByItemId(itemId);
		modelAndView.addObject("item", item );
		
		ItemDesc itemDesc = this.itemService.queryItemDescByItemId(itemId);
		modelAndView.addObject("itemDesc", itemDesc);
		
		//规格参数数据
        String itemParam = this.itemService.queryItemParamByItemId(itemId);
        modelAndView.addObject("itemParam", itemParam);
		return modelAndView;
	}
}
