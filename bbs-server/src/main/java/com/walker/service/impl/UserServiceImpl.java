package com.walker.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.walker.config.security.JwtTokenUtil;
import com.walker.mapper.SaOrgMapper;
import com.walker.mapper.UserMapper;
import com.walker.pojo.SaOrg;
import com.walker.pojo.User;
import com.walker.utils.ConstantUtil;
import com.walker.vo.MapCityVO;
import com.walker.vo.ResultBean;
import com.walker.service.UserService;
import com.walker.vo.UserMonthVO;
import com.walker.vo.param.UserInfoUpdateParam;
import com.walker.vo.param.UserModOrgNoParam;
import com.walker.vo.param.UserModPwdParam;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author walker
 * @since 2022/04/16 20:18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private SaOrgMapper saOrgMapper;


    /**
     * 登录之后返回token
     * @param username
     * @param password
     * @param channel 登录渠道
     * @param request
     * @return
     * JWT
     * Json Web Token
     */
    @Override
    public ResultBean login(String username, String password, String channel, HttpServletRequest request) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails != null && passwordEncoder.matches(password,userDetails.getPassword())){
            User user = (User) userDetails;
            if (ConstantUtil.MANA_ZERO_ONE.equals(channel) || ConstantUtil.MANA_ZERO_TWO.equals(channel)) {
                if (ConstantUtil.MANA_ZERO_TWO.equals(channel) && ConstantUtil.MANA_ONE.equals(user.getUserType())) {
                    return ResultBean.error("用户名或密码不正确！");
                }
            } else {
                return ResultBean.error("非法渠道登录！");
            }
            //更新security 登录用户对象
            UsernamePasswordAuthenticationToken authenticationToken = new
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 生成token
            String token = jwtTokenUtil.generateToken(userDetails);
            HashMap<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("token",token);
            tokenMap.put("tokenHead",tokenHead);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(user);
            jsonObject.put("password",null);
            tokenMap.put("user", jsonObject);

            return ResultBean.success("登录成功！",tokenMap);
        }
        return ResultBean.error("用户名或密码不正确！");
    }

    /**
     * 更据用户名获取用户信息
     * @param username
     * @return
     */
    @Override
    public User getUserByUsername(String username) {
        User user = null;
        if (StringUtils.isNoneBlank(username)){
            user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getUsername, username)
            );
        }
        return user;
        // return userMapper.selectOne(new QueryWrapper<User>().eq("username",username));
    }

    /**
     * 用户注册
     * @param username
     * @param password
     * @param phone
     * @param orgNo
     * @param nickname
     * @param request
     * @return
     */
    @Override
    public ResultBean register(String username, String password, String phone, String orgNo, String nickname, HttpServletRequest request) {
            if (StringUtils.isNoneBlank(username) && StringUtils.isNoneBlank(password)
                    && StringUtils.isNoneBlank(phone) && StringUtils.isNoneBlank(orgNo)
                    && StringUtils.isNoneBlank(nickname)){
                // 判断用户是否存在
                User user = userMapper.selectOne(
                        new LambdaQueryWrapper<User>()
                                .eq(User::getUsername, username)
                );
                if (user == null){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = format.format(new Date());

                    User newUser = new User();
                    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                    newUser.setUsername(username)
                            .setPassword(bCryptPasswordEncoder.encode(password))
                            .setFans(0)
                            .setAttention(0)
                            .setGender(ConstantUtil.MANA_ZERO)
                            .setGood(0)
                            .setIsAlive(0)
                            .setCreateTime(date)
                            .setPhone(phone)
                            .setOrgNo(orgNo)
                            .setNickname(nickname)
                            .setUserType(ConstantUtil.MANA_ONE);

                    userMapper.insert(newUser);
                    return ResultBean.success("注册成功！");
                }
                return ResultBean.error("用户名已经存在！");
            }
            return ResultBean.error("参数不全！");
    }

    /**
     * 用户上传头像
     * @param id
     * @param pathDB
     * @return
     */
    @Override
    public ResultBean updatePortrait(Integer id, String pathDB) {

        this.update(Wrappers.lambdaUpdate(User.class).set(User::getPortrait,pathDB).eq(User::getId,id));

        // this.update(new UpdateWrapper<User>().set("portrait",pathDB).eq("id",id));

        return ResultBean.success("头像上传成功！");

    }

    /**
     * 用户修改信息（除头像）
     * @param userInfoUpdateParam
     */
    @Override
    public ResultBean updateUserinfo(UserInfoUpdateParam userInfoUpdateParam) {
        User user = new User();
        user.setId(userInfoUpdateParam.getId());
        user.setPhone(userInfoUpdateParam.getPhone());
        this.updateById(user);

        return ResultBean.success("保存成功！");
    }

    /**
     * 通过用户Id获取用户信息
     * @param id
     * @return
     */
    @Override
    public User queryUserinfoById(Integer id) {

        return this.getById(id);
    }

    @Override
    public User getUserInfoByUserId(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public PageInfo<User> getAllUserByPageAndSearch(int pageIndex, int pageSize, String searchInfo) {
        PageHelper.startPage(pageIndex,pageSize);
        // List<User> users = userMapper.selectList(null);
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .like(User::getUsername,searchInfo)
                        .or()
                        .like(User::getNickname,searchInfo)
        );

        return new PageInfo<>(users);
    }

    @Override
    public ResultBean deleteUserByUserId(Integer id) {
        userMapper.deleteById(id);
        return ResultBean.success("删除成功！");
    }

    @Override
    public ResultBean updateUserAliveByUserId(Integer id) {
        User user = userMapper.selectById(id);

        User updateUser = new User();
        updateUser.setId(id);
        if (user.getIsAlive() == 0){
            updateUser.setIsAlive(1);
        }else {
            updateUser.setIsAlive(0);
        }
        userMapper.updateById(updateUser);
        return ResultBean.success("成功修改用户状态！");
    }

    @Override
    public ResultBean batchDeleteUsersByUserIds(List<Integer> userIds) {
        userMapper.deleteBatchIds(userIds);
        return ResultBean.success("批量删除用户成功！");
    }

    @Override
    public ResultBean getUserCount() {
        Long count = userMapper.selectCount(null);
        return ResultBean.success("获取成功！",count);
    }

    @Override
    public ResultBean getAllCity() {
        // 用户数量、占比

        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .isNotNull(User::getCity)
        );
        // 用户总数
        double userCount = users.size();

        List<MapCityVO> list = new ArrayList<>();

        users.stream()
                .map(user -> {
                    String substring = user.getCity().substring(0, user.getCity().indexOf("-"));// 只获取“-”前面的省份
                    if (substring.contains("省")){
                        substring = substring.replace("省", ""); // 去除省字
                    }
                    return substring;
                })
                .collect(Collectors.groupingBy(String::valueOf))
                .forEach((cityName, cityNameList) -> {
                    // 计算占比
                    double cityUserCount = cityNameList.size();
                    NumberFormat numberFormat = NumberFormat.getPercentInstance();
                    numberFormat.setMinimumFractionDigits(2);
                    String perf = numberFormat.format(cityUserCount / userCount);

                    MapCityVO cityVO = new MapCityVO();
                    cityVO.setName(cityName)
                            .setValue(cityNameList.size())
                            .setPerf(perf);
                    list.add(cityVO);
                });
        // System.out.println("list = " + list.toString());

        return ResultBean.success("获取成功!",list);
    }

    @Override
    public ResultBean getUserDataWithMonth() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 获取当前的时间
        String currentDate = format.format(calendar.getTime());

        // 一年前的时间
        calendar.add(Calendar.YEAR, -1);
        String lastDate = format.format(calendar.getTime());
        // System.out.println("lastDate = " + lastDate);

        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, lastDate, currentDate)
                        .orderByAsc(User::getCreateTime)
        );
        // System.out.println("users = " + users);

        List<UserMonthVO> list = new ArrayList<>();
        users.stream()
                .map(user -> user.getCreateTime().substring(0,7))   // 截取年月
                .collect(Collectors.groupingBy(String::valueOf))
                .forEach((date, dateList) -> {
                    UserMonthVO userMonthVO = new UserMonthVO();
                    userMonthVO.setTime(date)
                                    .setCount(dateList.size());
                    list.add(userMonthVO);
                });
        // System.out.println("list = " + list);
        return ResultBean.success("获取成功！",list);
    }

    /**
     * 修改用户密码：先根据用户id查询用户是否存在，校验原密码后加密新密码更新
     * @param userModPwdParam 用户id、原密码、新密码
     * @return
     */
    @Override
    public ResultBean modPwd(UserModPwdParam userModPwdParam) {
        Integer userId = userModPwdParam.getId();
        User user = this.getById(userId);
        if (user == null) {
            return ResultBean.error("用户不存在");
        }
        String rawPassword = userModPwdParam.getPassword();
        String dbPassword = user.getPassword();
        if (!passwordEncoder.matches(rawPassword, dbPassword)) {
            return ResultBean.error("密码不正确");
        }
        String encodedNewPassword = passwordEncoder.encode(userModPwdParam.getNewPassword());
        this.update(Wrappers.lambdaUpdate(User.class)
                .set(User::getPassword, encodedNewPassword)
                .eq(User::getId, userId));
        return ResultBean.success("修改成功");
    }

    @Override
    public ResultBean updateUserRole(Integer userId, String roleType) {
        if (ConstantUtil.MANA_ZERO_ONE.equals(roleType)) {
            roleType = ConstantUtil.MANA_ONE;
        } else if (ConstantUtil.MANA_ZERO_TWO.equals(roleType)) {
            roleType = ConstantUtil.MANA_TWO;
        } else {
            return ResultBean.error("权限越界");
        }

        int result = userMapper.updateUserRole(userId, roleType);
        if (result > 0) {
            return ResultBean.success("操作成功");
        }
        return ResultBean.error("操作失败");
    }

    @Override
    public ResultBean modOrgNo(UserModOrgNoParam userModOrgNoParam) {
        // 先查询用户信息
        List<User> userList = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getId, userModOrgNoParam.getId())
        );
        if (CollectionUtils.isEmpty(userList)) {
            return ResultBean.error("用户不存在");
        }
        // 对比单位编号是否和之前一样
        User user = userList.get(0);
        if (userModOrgNoParam.getOrgNo().equals(user.getOrgNo())) {
            return ResultBean.error("用户单位不能与原单位相同");
        }
        List <SaOrg> saOrgList = saOrgMapper.selectList(new LambdaQueryWrapper<SaOrg>().eq(SaOrg::getOrgNo, userModOrgNoParam.getOrgNo()));
        if (CollectionUtils.isEmpty(saOrgList)) {
            return ResultBean.error("单位编号不正确");
        }
        // 更新用户单位编号和昵称
        String [] nicknameArr = user.getNickname().split("-");
        if (nicknameArr.length > 1) {
            user.setNickname(saOrgList.get(0).getOrgName()+user.getNickname().substring(user.getNickname().indexOf("-")));
        }
        user.setOrgNo(userModOrgNoParam.getOrgNo());
        int result = userMapper.update(user, new LambdaQueryWrapper<User>()
                .eq(User::getId, user.getId()));
        if (result > 0) {
            return ResultBean.success("操作成功");
        }
        return ResultBean.error("操作失败");
    }

}
