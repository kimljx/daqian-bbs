package com.walker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walker.mapper.SensitiveWordMapper;
import com.walker.pojo.SaOrg;
import com.walker.pojo.SensitiveWord;
import com.walker.service.SensitiveWordService;
import com.walker.utils.SensitiveWordUtil;
import com.walker.vo.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author chengQing
 * @Date 2026/4/8 17:54
 * @PackageName:com.walker.service.impl
 * @ClassName: SensitiveWordServiceImpl
 * @Description: 敏感词接口层实现类
 */
@Service
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Override
    public List<SensitiveWord> getList() {
        return sensitiveWordMapper.selectList(new LambdaQueryWrapper<SensitiveWord>()
                .orderByAsc(SensitiveWord::getId)
        );
    }

    @Override
    public ResultBean addSensitiveWord(String keyword) {
        // 先查询敏感词是否已存在
        Long count = sensitiveWordMapper.selectCount(
                new LambdaQueryWrapper<SensitiveWord>()
                        .eq(SensitiveWord::getKeyword, keyword)
        );
        if (count > 0) {
            ResultBean.error("添加失败！敏感词已存在！");
        }
        // 组装数据并添加到数据库
        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setKeyword(keyword);
        int result = sensitiveWordMapper.insert(sensitiveWord);
        if (result > 0) {
            // 刷新敏感词列表
            refresh();
            return ResultBean.success("添加成功！");
        }
        return ResultBean.error("添加失败！");
    }

    @Override
    public ResultBean delSensitiveWord(Integer id) {
        int result = sensitiveWordMapper.deleteById(id);
        if (result > 0) {
            // 刷新敏感词列表
            refresh();
            return ResultBean.success("删除成功！");
        }
        return ResultBean.error("删除失败！");
    }

    /**
     * PostConstruct 启动加载
     */
    @PostConstruct
    @Override
    public void loadSensitiveWords() {
        List<SensitiveWord> list = this.list();

        List<String> words = list.stream()
                .map(SensitiveWord::getKeyword)
                .collect(Collectors.toList());
        // 存入工具类（静态变量）
        SensitiveWordUtil.updateSensitiveWords(words);
    }

    /**
     * 方法描述 重新加载敏感词
     * @author chengQing
     * @date 2026/4/9 15:05
     */
    public void refresh() {
        // 重新加载敏感词并更新工具类
        loadSensitiveWords();
    }
}
