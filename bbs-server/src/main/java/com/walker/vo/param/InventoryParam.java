package com.walker.vo.param;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation(value = "InventoryParam对象")
public class InventoryParam {

    private String area;
    private String time;
    private String type;
    private String category;
    private String keywords;

}
