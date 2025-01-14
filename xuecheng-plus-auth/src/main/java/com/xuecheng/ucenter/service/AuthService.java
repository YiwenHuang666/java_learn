package com.xuecheng.ucenter.service;


import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

//同一认证接口
public interface AuthService {

    XcUserExt execute(AuthParamsDto authParamsDto);


}
