package com.taotao.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.manage.pojo.ItemParam;
import com.taotao.manage.service.ItemParamService;


@Service
@RequestMapping("item/param")
public class ItemParamController {

    @Autowired
    private ItemParamService itemParamService;

    @RequestMapping(method = RequestMethod.GET, value = "{itemCatId}")
    public ResponseEntity<ItemParam> queryByItemCatId(@PathVariable("itemCatId") Long itemCatId) {

        try {
            if (null == itemCatId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            ItemParam record = new ItemParam();
            record.setItemCatId(itemCatId);
            ItemParam itemParam = this.itemParamService.queryOne(record);

            if (null == itemParam) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.status(HttpStatus.OK).body(itemParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "{itemCatId}")
    public ResponseEntity<Void> saveItemParam(@RequestParam("paramData") String paramData,
            @PathVariable("itemCatId") Long itemCatId) {
        try {
            ItemParam record = new ItemParam();
            record.setParamData(paramData);
            record.setItemCatId(itemCatId);
            this.itemParamService.saveSelective(record );
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
