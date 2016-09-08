package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


	
@Controller
public class IndexController {

//	@Autowired
//	private IndexService indexService;
	
        @RequestMapping(value="index",method=RequestMethod.GET)
        public ModelAndView index() {
            
            ModelAndView mv = new ModelAndView("index");
//            String indexAd1 = this.indexService.queryAD1();
//            mv.addObject("indexAD1", indexAd1);
//            
//            String indexAd2 = this.indexService.queryAD2();
//            mv.addObject("indexAD2", indexAd2);
            
            return mv;
            
        }
}
