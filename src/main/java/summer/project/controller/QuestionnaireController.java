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
    @PostMapping("/save_new_questionnaire")
    @ApiOperation(value = "保存新建的问卷", notes = "发送用户ID，和一个问题的列表，每个问题包含答案（如果必要），具体看下面的描述")
    public Result submitQuestionnaire(@ApiParam(value = "发送一个问卷", required = true) @Validated @RequestBody QuestionnaireDto questionnaireDto) {

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);

        try {
            Questionnaire questionnaire = new Questionnaire(questionnaireDto.getUserId(),
                    questionnaireDto.getTitle(),
                    questionnaireDto.getDescription(),
                    LocalDateTime.now(),
                    questionnaireDto.getStartTime(),
                    questionnaireDto.getEndTime(),
                    questionnaireDto.getAnswerNum(),
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
                        questionDto.getType()
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


        return Result.succeed(200, "保存问卷成功!", null);
    }
}
