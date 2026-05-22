package com.walker.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.InventoryMapper;
import com.walker.pojo.Inventory;
import com.walker.service.InventoryService;
import com.walker.vo.param.InventoryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {


    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public List<Inventory> getAllInventory(InventoryParam inventoryParam) {
        if (inventoryParam.getArea() != null || inventoryParam.getTime() != null ||
                inventoryParam.getCategory() != null || inventoryParam.getType() != null ||
                inventoryParam.getKeywords() != null){
            LambdaQueryWrapper<Inventory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Inventory::getArea, inventoryParam.getArea()).or()
                    .eq(Inventory::getTime,inventoryParam.getTime()).or()
                    .eq(Inventory::getCategory,inventoryParam.getCategory()).or()
                    .eq(Inventory::getType,inventoryParam.getType()).or()
                    .like(Inventory::getContent,inventoryParam.getKeywords());
            return inventoryMapper.selectList(lambdaQueryWrapper);
        }
        return inventoryMapper.selectList(null);

    }

    @Override
    public Inventory getInventoryById(Integer id) {

        return inventoryMapper.selectById(id);
    }
}
