package com.walker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walker.pojo.Inventory;
import com.walker.vo.param.InventoryParam;

import java.util.List;

public interface InventoryService extends IService<Inventory> {

    /**
     * 获取所有的
     * @return
     */
    List<Inventory> getAllInventory(InventoryParam inventoryParam);

    /**
     * 通过id获取
     * @param id
     * @return
     */
    Inventory getInventoryById(Integer id);
}
