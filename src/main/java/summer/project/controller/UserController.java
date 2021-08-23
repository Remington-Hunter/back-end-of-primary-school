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


}
