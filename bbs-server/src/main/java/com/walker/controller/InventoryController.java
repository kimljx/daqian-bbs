package com.walker.controller;


import com.walker.pojo.Inventory;
import com.walker.service.InventoryService;
import com.walker.vo.param.InventoryParam;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "InventoryController")
@RestController
@RequestMapping("/common")
public class InventoryController {


    @Autowired
    private InventoryService    inventoryService;

    @PostMapping("/inventory/getAllInventory")
    public List<Inventory> getAllInventoryByCondition(@RequestBody InventoryParam inventoryparam){
        // System.out.println(inventoryparam);
        return  inventoryService.getAllInventory(inventoryparam);
    }


    @GetMapping("/inventory/getInventoryById")
    public Inventory getInventoryById(@RequestParam("id") Integer id){
        return inventoryService.getInventoryById(id);
    }

}
