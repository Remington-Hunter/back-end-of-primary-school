package summer.project.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import summer.project.common.dto.LoginDto;
import summer.project.common.lang.Result;
import summer.project.entity.TestUser;
import summer.project.service.TestUserService;
import summer.project.util.JwtUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api(tags = {"登录，认证，退出等操作"})
@RestController
public class AccountController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TestUserService userService;

    @PostMapping("/login")
    public Result test(@ApiParam(value = "用户信息校验实体", required = true) @Validated LoginDto loginDto,
                       HttpServletResponse response) {

//        UsernamePasswordToken token = new UsernamePasswordToken( username, password );
//        token.setRememberMe(r == 1);
////        Subject currentUser = SecurityUtils.getSubject();
////        currentUser.login(token);
//        Subject subject = SecurityUtils.getSubject();
//        try {
//            subject.login(token);
//        } catch (UnknownAccountException e) {
//            return Result.fail("用户名不存在");
//        } catch (IncorrectCredentialsException e) {
//            return Result.fail("密码错误");
//        }

        TestUser user = userService.getOne(new QueryWrapper<TestUser>().eq("username", loginDto.getUsername()));
        Assert.notNull(user, "用户不存在");

//        if (!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
        if (!user.getPassword().equals(loginDto.getPassword())) {

            System.out.println(SecureUtil.md5(loginDto.getPassword()));
            return Result.fail("密码不正确");
        }
        String jwt = jwtUtils.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        Map<Object, Object> map = MapUtil.builder()
                .put("username", user.getUsername())
                .put("user_id", user.getId())
                .map();
        return Result.succeed(200, "登录成功", map);
    }

    @PostMapping("/t1")
    @RequiresPermissions("user:add")
    public Result test1() {

        return Result.succeed(200, "测试成功", null);
    }

    @PostMapping("/t2")
    @RequiresPermissions("user:update")
    public Result test2() {

        return Result.succeed(200, "测试成功", null);
    }

//    @PostMapping


//    @PostMapping("/login")
//    public Result login()

}
