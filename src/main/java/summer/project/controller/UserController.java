package summer.project.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import summer.project.common.dto.EmailDto;
import summer.project.common.lang.Result;
import summer.project.entity.User;
import summer.project.service.QuestionnaireService;
import summer.project.service.UserService;
import summer.project.util.ShiroUtil;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-20
 */
@Api(tags = {"个人信息相关"})
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequiresAuthentication
    @PostMapping("/get_info")
    @ApiOperation(value = "获取个人信息")
    private Result getInfo() {
        System.out.println(ShiroUtil.getProfile().getId());
        return Result.succeed(userService.getById(ShiroUtil.getProfile().getId()));
    }

    @RequiresAuthentication
    @PostMapping("/set_phone")
    @ApiOperation(value = "设置")
    private Result setPhone(@ApiParam(value = "手机号（字符串）") String phone) {
        User user = userService.getById(ShiroUtil.getProfile().getId());
        user.setPhone(phone);
        return Result.succeed(200, "手机号设置成功", user);
    }

    @RequiresAuthentication
    @PostMapping("/set_email")
    @ApiOperation(value = "设置")
    private Result setEmail(@ApiParam(value = "邮箱（字符串）") @Validated EmailDto emailDto) {
        User user = userService.getById(ShiroUtil.getProfile().getId());
        user.setEmail(emailDto.getEmail());
        return Result.succeed(200, "邮箱设置成功", user);
    }

    @RequiresAuthentication
    @PostMapping("/set_wechat")
    @ApiOperation(value = "设置")
    private Result setWechat(@ApiParam(value = "微信号（字符串）") String wechat) {
        User user = userService.getById(ShiroUtil.getProfile().getId());
        user.setWechatId(wechat);
        return Result.succeed(200, "微信设置成功", user);
    }
}
