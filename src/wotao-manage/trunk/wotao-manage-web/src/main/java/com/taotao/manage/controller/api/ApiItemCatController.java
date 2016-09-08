package com.taotao.manage.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.bean.ItemCatResult;
import com.taotao.manage.service.ItemCatService;

@RequestMapping("api/item/cat")
@Controller
public class ApiItemCatController {
    
    @Autowired 
    private ItemCatService itemCatService;
    
//    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<ItemCatResult> queryItemCat() {
        
        try {
            ItemCatResult itemCatResult =this.itemCatService.queryAllToTree();
            if(null==itemCatResult){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(itemCatResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
    }
    
//    @RequestMapping(method=RequestMethod.GET)
//    public ResponseEntity<String> queryItemCat(@RequestParam("callback")String callback) {
//        
//        try {
//            ItemCatResult itemCatResult =this.itemCatService.queryAllToTree();
//          
//            if(null!=itemCatResult){
//                String json = MAPPER.writeValueAsString(itemCatResult);
//                if(StringUtils.isEmpty(callback)){
//                    return ResponseEntity.status(HttpStatus.OK).body(json);
//                }else{
//                    return ResponseEntity.status(HttpStatus.OK).body(callback+"("+json+")");
//                }
//            }
//            
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        
//    }
}
