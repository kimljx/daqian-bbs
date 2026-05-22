package com.walker.controller;

import com.walker.service.SaOrgService;
import com.walker.vo.ResultBean;
import com.walker.vo.SaOrgTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author chengQing
 * @Date 2026/3/3 14:45
 * @PackageName:com.walker.controller
 * @ClassName: SaOrgController
 * @Description: 单位控制层
 */
@Api(tags = "SaOrgController")
@RestController
public class SaOrgController {

    @Autowired
    private SaOrgService saOrgService;
    /**
     * 查询单位树形结构
     */
    @ApiOperation("查询单位树形结构")
    @GetMapping("/common/saOrgTree")
    public ResultBean getSaOrgTree() {
        return ResultBean.success("成功获取！",saOrgService.getOrgTree());
    }

    /**
     * 方法描述 删除单位
     * @author chengQing
     * @date 2026/4/8 17:02
     * @param orgNo 单位编号
     * @return ResultBean 返回封装结果
     */
    @ApiOperation("删除单位")
    @GetMapping("/saOrg/deleteSaOrgByOrgNo")
    public ResultBean deleteSaOrgByOrgNo(@RequestParam("orgNo") String orgNo) {
        return saOrgService.deleteSaOrgByOrgNo(orgNo);
    }

    /**
     * 方法描述 添加单位
     * @author chengQing
     * @date 2026/4/8 17:04
     * @param pOrgNo 父级单位
     * @param orgName 单位名称
     * @return ResultBean 返回封装结果
     */
    @ApiOperation("添加单位")
    @GetMapping("/saOrg/addSaOrg")
    public ResultBean addSaOrg(@RequestParam("pOrgNo") String pOrgNo, @RequestParam("orgName") String orgName) {
        return saOrgService.addSaOrg(pOrgNo, orgName);
    }

}
