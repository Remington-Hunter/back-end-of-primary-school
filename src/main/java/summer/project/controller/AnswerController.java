package summer.project.controller;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import summer.project.common.dto.AnswerDto;
import summer.project.common.dto.AnswerListDto;
import summer.project.common.lang.Result;
import summer.project.entity.Answer;
import summer.project.entity.Option;
import summer.project.entity.Question;
import summer.project.entity.Questionnaire;
import summer.project.service.AnswerService;
import summer.project.service.OptionService;
import summer.project.service.QuestionService;
import summer.project.service.QuestionnaireService;

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
@Api("提交答卷")
@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionService questionService;

    @Autowired
    AnswerService answerService;

    @Autowired
    OptionService optionService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @ApiOperation(value = "查看统计结果", notes = "直接发formdata的问卷id，名字就叫id")
    @PostMapping("get_result")
    public Result getResult(@ApiParam(value = "问卷id", required = true) Long id) {
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "不存在该问卷");
    }


    @ApiOperation(value = "提交答案", notes = "json格式")
    @PostMapping("/submit_answer")
    public Result submitAnswer(@ApiParam(value = "问卷id和答案清单", required = true) @Validated @RequestBody AnswerListDto answerListDto) {
        Long questionnaireId = answerListDto.getQuestionnaireId();

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);


        try {
            Questionnaire questionnaire = questionnaireService.getById(questionnaireId);
            Assert.notNull(questionnaire, "不存在该问卷");

            LocalDateTime now = LocalDateTime.now();
            if (questionnaire.getEndTime() != null && now.isAfter(questionnaire.getEndTime())) {
                return Result.fail(400, "问卷提交已截止。", null);
            }
            if (questionnaire.getStartTime() != null && now.isBefore(questionnaire.getStartTime())) {
                return Result.fail(400, "问卷未开始。", null);
            }

            if (questionnaire.getLimit() >= 0 && questionnaire.getLimit() < questionnaire.getAnswerNum()) {
                return Result.fail(400, "问卷填报人数已满。", null);
            }


            for (AnswerDto answerDto : answerListDto.getAnswerDtoList()) {
                Long questionId = answerDto.getQuestionId();
                Question question = questionService.getById(questionId);
                Assert.notNull(question, "问题不存在");
                Answer answer = new Answer();
                answer.setContent(answerDto.getContent());
                answer.setNumber(answerDto.getNumber());
                answer.setQuestionId(answerDto.getQuestionId());

                List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
                for (Option option : optionList) {
                    if (option.getNumber().equals(answerDto.getNumber()) && option.getLimit() <= option.getAnswerNum()) {
                        return Result.fail(400, "抱歉，您的第"+answerDto.getQuestionId()+"题的选择人数已满。", null);
                    }
                }

                answerService.save(answer);
            }
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }


        return Result.succeed(200, "提交问卷成功", null);
    }

}
