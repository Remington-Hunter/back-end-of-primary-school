package summer.project.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import summer.project.common.dto.OptionDto;
import summer.project.common.dto.QuestionDto;
import summer.project.common.dto.QuestionnaireDto;
import summer.project.common.lang.Result;
import summer.project.entity.Option;
import summer.project.entity.Question;
import summer.project.entity.Questionnaire;
import summer.project.service.OptionService;
import summer.project.service.QuestionService;
import summer.project.service.QuestionnaireService;
import summer.project.util.ShiroUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-20
 */
@Api(tags = "提交问卷等")
@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionService questionService;

    @Autowired
    OptionService optionService;

    @Autowired
    PlatformTransactionManager transactionManager;

    //    @Transactional
    @RequiresAuthentication
    @PostMapping("/save_questionnaire")
    @ApiOperation(value = "保存新建的问卷", notes = "发送用户ID（userId），和一个问题的列表，每个问题包含答案（如果必要），具体看下面的描述，" +
            "如果这个问卷是已经修改过的，那就带着id，如果是新的问卷，id就不用填")
    public Result saveQuestionnaire(@ApiParam(value = "要提交问卷", required = true) @Validated @RequestBody QuestionnaireDto questionnaireDto) {

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
        try {
            // 新问卷
            if (questionnaireDto.getId() == null) {

                Questionnaire questionnaire = new Questionnaire(
                        questionnaireDto.getUserId(),
                        questionnaireDto.getTitle(),
                        questionnaireDto.getDescription(),
                        LocalDateTime.now(),
                        questionnaireDto.getStartTime(),
                        questionnaireDto.getEndTime(),
                        questionnaireDto.getNeedNum(),
                        questionnaireDto.getLimit()
                );

                questionnaireService.save(questionnaire);

                for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
                    Question question = new Question(
                            questionDto.getContent(),
                            questionDto.getAnswer(),
                            questionDto.getPoint(),
                            questionDto.getType(),
                            questionDto.getNumber(),
                            questionDto.getRequired(),
                            questionDto.getComment()
                    );
                    questionService.save(question);
                    for (OptionDto optionDto : questionDto.getOptionList()) {
                        Option option = new Option(
                                optionDto.getContent(),
                                optionDto.getLimit(),
                                optionDto.getNumber()
                        );
                        optionService.save(option);
                    }
                }
            } else {
                // 旧问卷
                Questionnaire questionnaire = questionnaireService.getById(questionnaireDto.getId());
                Assert.notNull(questionnaire, "不存在该问卷。");
                Assert.isTrue(questionnaire.getUserId().equals(ShiroUtil.getProfile().getId()), "无权限修改他人问卷。");
                questionnaire.setId(questionnaireDto.getId());
                questionnaire.setCreateTime(LocalDateTime.now());
                questionnaire.setTitle(questionnaireDto.getTitle());
                questionnaire.setStartTime(questionnaireDto.getStartTime());
                questionnaire.setEndTime(questionnaireDto.getEndTime());
                questionnaire.setNeedNum(questionnaireDto.getNeedNum());
                questionnaire.setLimit(questionnaire.getLimit());
                questionnaireService.updateById(questionnaire);

                for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
                    if (questionDto.getId() == null) {
                        // 新问题
                        Question question = new Question(
                                questionDto.getContent(),
                                questionDto.getAnswer(),
                                questionDto.getPoint(),
                                questionDto.getType(),
                                questionDto.getNumber(),
                                questionDto.getRequired(),
                                questionDto.getComment()
                        );
                        questionService.save(question);
                        for (OptionDto optionDto : questionDto.getOptionList()) {
                            Option option = new Option(
                                    optionDto.getContent(),
                                    optionDto.getLimit(),
                                    optionDto.getNumber()
                            );
                            optionService.save(option);
                        }
                    } else {
                        // 旧问题
                        Question question = questionService.getById(questionDto.getId());
                        Assert.notNull(question, "题目不存在");
                        question.setAnswer(questionDto.getAnswer());
                        question.setComment(question.getComment());
                        question.setType(question.getType());
                        question.setContent(question.getContent());
                        question.setNumber(question.getNumber());
                        question.setPoint(question.getPoint());
                        question.setRequired(question.getRequired());

                        questionService.save(question);
                        for (OptionDto optionDto : questionDto.getOptionList()) {

                            if (optionDto.getId() == null) {
                                // 新选项
                                Option option = new Option(
                                        optionDto.getContent(),
                                        optionDto.getLimit(),
                                        optionDto.getNumber()
                                );
                                optionService.save(option);
                            } else {
                                // 旧选项
                                Option option = optionService.getById(optionDto.getId());
                                Assert.notNull(option, "选项不存在");
                                option.setContent(optionDto.getContent());
                                option.setLimit(optionDto.getLimit());
                                option.setNumber(optionDto.getNumber());
                                optionService.updateById(option);
                            }
                        }
                    }

                }
            }
            transactionManager.commit(status);
        } catch (Exception e) {

            transactionManager.rollback(status);
            return Result.fail("保存失败！");
        }


        return Result.succeed(200, "问卷保存成功!", null);
    }


    @RequiresAuthentication
    @PostMapping("/publish_questionnaire")
    @ApiOperation(value = "发布新建的问卷，并返回链接的后缀", notes = "发送用户ID（userId），和一个问题的列表，每个问题包含答案（如果必要），具体看下面的描述，" +
            "如果这个问卷是已经修改过的，那就带着id，如果是新的问卷，id就不用填")
    public Result submitQuestionnaire
            (@ApiParam(value = "要提交的问卷", required = true) @Validated @RequestBody QuestionnaireDto questionnaireDto) {
        Result result = saveQuestionnaire(questionnaireDto);
        if (result.getCode() != 200) {
            return Result.fail("发布失败！");
        }
        Questionnaire questionnaire = questionnaireService.getById(questionnaireDto.getId());
        questionnaire.setPreparing(0);
        questionnaire.setDeleted(0);
        questionnaire.setUsing(1);
        String md5 = questionnaire.getId() + "_" + SecureUtil.md5(LocalDateTime.now() + "").substring(0, 4);
        questionnaire.setUrl(md5);
        questionnaireService.updateById(questionnaire);
        return Result.succeed(200, "问卷发布成功!", md5);
    }

    @RequiresAuthentication
    @PostMapping("/get_link")
    @ApiOperation(value = "获得问卷的链接后缀", notes = "直接发送问卷的id，发form data")
    public Result getLink(@ApiParam(value = "要得到链接的问卷id", required = true) Long id) {
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "不存在该问卷");
        Assert.isTrue(questionnaire.getUserId().equals(ShiroUtil.getProfile().getId()), "您无权访问此问卷");
        String md5 = questionnaire.getId() + "_" + SecureUtil.md5(LocalDateTime.now() + "").substring(0, 4);
        questionnaire.setUrl(md5);
        questionnaireService.updateById(questionnaire);
        return Result.succeed(md5);
    }

    @RequiresAuthentication
    @PostMapping("/get_questionnaire_list")
    @ApiOperation(value = "获得所有的问卷的基本信息", notes = "带着Authorization请求头，不需要参数")
    @ApiResponses(
            @ApiResponse(code = 200, message = "你的data长这个样")
    )
    public Result getQuestionnaireList() {
        Long userId = ShiroUtil.getProfile().getId();
        List<Questionnaire> questionnaireList = questionnaireService.list(new QueryWrapper<Questionnaire>().eq("user_id", userId));
        return Result.succeed(questionnaireList);
    }

}
