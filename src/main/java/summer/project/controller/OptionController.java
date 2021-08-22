package summer.project.controller;


import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import summer.project.common.lang.Result;
import summer.project.entity.Option;
import summer.project.service.OptionService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-20
 */

@Api(tags = {"跟选项相关","比如查看当前选项还剩下多少人"})
@RestController
@RequestMapping("/option")
public class OptionController {

    @Autowired
    OptionService optionService;

    @ApiOperation(value = "查看选项还有多少剩余", notes = "传formdata， optionId:选项的id")
    @PostMapping("/checkRest")
    public Result checkRest(@ApiParam(value = "选项的id") Long optionId) {
        Option option = optionService.getById(optionId);
        Assert.notNull(option, "选项不存在");
        return Result.succeed((option.getLimit() - option.getAnswerNum()));
    }
}
