package com.taotao.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.search.bean.SearchResult;
import com.taotao.search.service.SearchService;


@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public ModelAndView search(@RequestParam("q") String keyWords,
            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        ModelAndView mv = new ModelAndView("search");
        try {
            //解决GET请求中文乱码问题，
            keyWords = new String(keyWords.getBytes("ISO-8859-1"), "UTF-8");
            
            // 搜索商品
            SearchResult searchResult = this.searchService.search(keyWords, page);

            mv.addObject("query", keyWords); // 搜索关键字
            mv.addObject("itemList", searchResult.getData()); // 商品列表
            mv.addObject("page", page); // 页数
            int total = searchResult.getTotal().intValue();
            int pages = total % SearchService.ROWS == 0 ? total / SearchService.ROWS : total
                    / SearchService.ROWS + 1;
            mv.addObject("pages", pages); // 总页数
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mv;
    }

}
