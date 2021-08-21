package summer.project.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

import javax.annotation.Resource;
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
    public Result saveNewQuestionnaire(@ApiParam(value = "保存一个问卷", required = true) @Validated @RequestBody QuestionnaireDto questionnaireDto) {

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);

        // 新问卷
        if (questionnaireDto.getId() == null) {
            try {
                Questionnaire questionnaire = new Questionnaire(questionnaireDto.getUserId(),
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
                            questionnaire.getId(),
                            questionDto.getContent(),
                            questionDto.getAnswer(),
                            questionDto.getPoint(),
                            questionDto.getType(),
                            questionDto.getNumber()
                    );
                    questionService.save(question);
                    for (OptionDto optionDto : questionDto.getOptionList()) {
                        Option option = new Option(
                                question.getId(),
                                optionDto.getContent(),
                                optionDto.getLimit(),
                                optionDto.getNumber()
                        );
                        optionService.save(option);
                    }
                }
                transactionManager.commit(status);
            }catch (Exception e) {

                transactionManager.rollback(status);
                return Result.fail("保存失败！");
            }
        } else {
            // 旧问卷
            Questionnaire questionnaire = questionnaireService.getById(questionnaireDto.getId());
            if (questionnaire == null) {
                return Result.fail("不存在该问卷。");
            }
            if (!questionnaire.getUserId().equals(ShiroUtil.getProfile().getId())) {
                return Result.fail("无权限修改他人问卷。");
            }
            try {
                questionnaire.setId(questionnaireDto.getId());
                questionnaire.setCreateTime(questionnaireDto.getEndTime());
                questionnaire.setTitle(questionnaireDto.getTitle());
                questionnaire.setStartTime(questionnaireDto.getStartTime());
                questionnaire.setEndTime(questionnaireDto.getEndTime());
                questionnaire.setNeedNum(questionnaireDto.getNeedNum());
                questionnaire.setLimit(questionnaire.getLimit());
                questionnaireService.updateById(questionnaire);
            } catch (Exception e) {
                transactionManager.rollback(status);
                return Result.fail("保存失败！");
            }
        }



        return Result.succeed(200, "问卷保存成功!", null);
    }

    @RequiresAuthentication
    @PostMapping("/submit_questionnaire")
    @ApiOperation(value = "保存新建的问卷", notes = "发送用户ID（userId），和一个问题的列表，每个问题包含答案（如果必要），具体看下面的描述，" +
            "如果这个问卷是已经修改过的，那就带着id，如果是新的问卷，id就不用填")
    public Result submitQuestionnaire (@ApiParam(value = "发布一个问卷", required = true) @Validated @RequestBody QuestionnaireDto questionnaireDto) {


        return Result.succeed(200, "问卷发布成功!", null);
    }
}
