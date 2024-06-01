package com.bit.springboot.controller.admin;

import com.bit.springboot.common.BaseResponse;
import com.bit.springboot.common.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LeLe
 * @date 2024/5/22 13:48
 * @Description:
 */

@RestController
@RequestMapping("/admin")
@Api("用户登录")
public class AdminLoginController {


    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public BaseResponse<String> login(){
        return ResultUtils.success("");
    }

    @GetMapping("/loginout")
    @ApiOperation("管理员退出")
    public BaseResponse<String> loginOut(){
        return ResultUtils.success("");
    }

}
