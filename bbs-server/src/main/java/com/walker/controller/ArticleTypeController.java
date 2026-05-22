package com.walker.controller;


import com.walker.pojo.ArticleType;
import com.walker.service.ArticleTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author walker
 * @since 2022/05/24 11:10
 */
@Api(tags = "ArticleTypeController")
@RestController
//@RequestMapping("/article-type")
public class ArticleTypeController {

    @Autowired
    private ArticleTypeService articleTypeService;

    @ApiOperation(value = "获取文章的类型")
    @GetMapping("/getArticleType")
    public List<ArticleType> getArticleType(){

        List<ArticleType> articleTypes = articleTypeService.queryAllArticleType();
        return articleTypes;

    }

}
