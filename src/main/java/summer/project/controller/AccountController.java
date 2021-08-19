package summer.project.controller;

import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import summer.project.common.lang.Result;

@Api(tags = {"登录，认证，退出等操作"})
@RestController
public class AccountController {
    @PostMapping("/test")
    public Result test(@RequestParam("username") String username,
                       @RequestParam("password") String password,
                       @RequestParam("remember_me") Integer r) {

        UsernamePasswordToken token = new UsernamePasswordToken( username, password );
        token.setRememberMe(r == 1);
//        Subject currentUser = SecurityUtils.getSubject();
//        currentUser.login(token);
        return Result.succeed(200, "测试成功", token);
    }

//    @PostMapping


//    @PostMapping("/login")
//    public Result login()

}
