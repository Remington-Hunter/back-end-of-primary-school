package summer.project.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import summer.project.common.dto.LoginDto;
import summer.project.common.dto.SignUpDto;
import summer.project.common.lang.Result;
import summer.project.entity.TestUser;
import summer.project.entity.User;
import summer.project.service.TestUserService;
import summer.project.service.UserService;
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
    TestUserService testUserService;

    @Autowired
    UserService userService;

    @ApiOperation(value = "登录(使用用户名和密码)", notes = "普通登录接口，使用用户名和密码登录")
    @PostMapping("/login_username_password")
    public Result login(@ApiParam(value = "用户名密码登录信息校验实体", required = true) @Validated LoginDto loginDto,
                        HttpServletResponse response) {


        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        Assert.notNull(user, "用户不存在");

        if (!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
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

    @ApiOperation(value = "注册(使用用户名和密码)", notes = "普通注册接口，使用用户名和密码注册")
    @PostMapping("/sign_up_username_password")
    public Result SignUp(@ApiParam(value = "用户名密码注册信息校验实体", required = true) @Validated SignUpDto signUpDto) {
        User user = userService.getOne(new QueryWrapper<User>().eq("username", signUpDto.getUsername()));
        if (!signUpDto.getPassword().equals(signUpDto.getCheckPassword())) {
            return Result.fail(400, "两次密码不相同", null);
        }
        if (user != null) {
            return Result.fail(400, "用户名已存在", null);
        }
        userService.save(new User(signUpDto.getUsername(), SecureUtil.md5(signUpDto.getPassword())));

        return Result.succeed(200, "注册成功，请进行登录", null);

    }

    @ApiOperation(value = "测试权限", notes = "必须持有Authorization的头文件才可以访问该接口")
    @PostMapping("/test_right")
    @RequiresAuthentication
    public Result testRight() {
        return Result.succeed(200, "测试成功", null);
    }


    @ApiOperation(value = "测试登录", notes = "如果登录成功，会在Header中返回Authorization，将值添加到add和update接口的请求头中")
    @PostMapping("/test_login")
    public Result test(@ApiParam(value = "用户信息校验实体", required = true) @Validated LoginDto loginDto,
                       HttpServletResponse response) {


        TestUser user = testUserService.getOne(new QueryWrapper<TestUser>().eq("username", loginDto.getUsername()));
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

    @ApiOperation(value = "测试add权限", notes = "root用户应该能成功，其余用户不能成功")
    @PostMapping("/add")
    @RequiresPermissions("user:add")
    public Result test1() {

        return Result.succeed(200, "测试成功", null);
    }


    @ApiOperation(value = "测试update权限", notes = "admin用户应该能成功，其余用户不能成功")
    @PostMapping("/update")
    @RequiresPermissions("user:update")
    public Result test2() {

        return Result.succeed(200, "测试成功", null);
    }

//    @PostMapping


//    @PostMapping("/login")
//    public Result login()

}
