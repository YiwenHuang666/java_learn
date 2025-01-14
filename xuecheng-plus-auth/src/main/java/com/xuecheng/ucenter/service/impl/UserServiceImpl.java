package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    XcMenuMapper xcMenuMapper;

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;

        try {
            authParamsDto = JSON.parseObject(s,AuthParamsDto.class);
        }catch (Exception e){
            log.info("认证请求不符合项目要求:{}",s);
            throw new RuntimeException("认证请求数据格式不对");
        }

        String authType = authParamsDto.getAuthType();

        String beanName = authType + "_authservice";

        AuthService authService = applicationContext.getBean(beanName, AuthService.class);

        XcUserExt execute = authService.execute(authParamsDto);

        UserDetails userDetails = getUserPrincipal(execute);

        return userDetails;
    }

    public UserDetails getUserPrincipal(XcUserExt user){
//用户权限,如果不加报 Cannot pass a null GrantedAuthoritycollection
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());
        String[] authorities = {"p1"};
        if(xcMenus.size()>0){
            List<String> permissions = new ArrayList<>();
            xcMenus.forEach(m->{
                permissions.add(m.getCode());
            });
            authorities = permissions.toArray(new String[0]);
        }

        String password = user.getPassword();
//为了安全在令牌中不放密码
        user.setPassword(null);
//将 user 对象转 json
        String userString = JSON.toJSONString(user);
//创建 UserDetails 对象
        UserDetails userDetails =
                User.withUsername(userString).password(password
                ).authorities(authorities).build();
        return userDetails;
    }
}
