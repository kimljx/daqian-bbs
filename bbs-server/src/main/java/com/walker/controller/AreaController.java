package com.walker.controller;


import com.walker.pojo.Area;
import com.walker.service.AreaService;
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
@Api(tags = "AreaController")
@RestController
//@RequestMapping("/area")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @ApiOperation(value = "获取三个推荐区的图片和文字")
    @GetMapping("/common/getArea")
    public List<Area> getArea(){

        return areaService.queryArea();
    }

}
