package summer.project.controller;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.tomcat.jni.Time;
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
import summer.project.entity.*;
import summer.project.service.*;
import summer.project.util.ShiroUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-20
 */
@Api(tags = {"提交答卷"})
@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    AnswerListService answerListService;

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

    @RequiresAuthentication
    @ApiOperation(value = "查看统计结果，答卷为单位", notes = "直接发form data的问卷id，名字就叫id")
    @PostMapping("/get_result_by_questionnaire")
    public Result getResultByQuestionnaire(@ApiParam(value = "问卷id", required = true) Long id) {
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "不存在该问卷");

        Assert.isTrue(questionnaire.getUserId().equals(ShiroUtil.getProfile().getId()), "您无权查看此问卷！");
        HashMap<String, Object> result = new HashMap<>();
        List<AnswerList> answerListList = answerListService.list(new QueryWrapper<AnswerList>().eq("questionnaire", questionnaire.getId()));
        List<Object> answerInfo = new ArrayList<>();
        for (AnswerList answerList : answerListList) {
            HashMap<String, Object> an = new HashMap<>();
            an.put("info", answerList);
            an.put("answerList", answerService.list(new QueryWrapper<Answer>().eq("answer_list_id", answerList.getId())));
            answerInfo.add(an);
        }
        result.put("answerInfo", answerInfo);

        List<Question> questions = questionService.list(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));
        List<Object> questionInfo = new ArrayList<>();
        for (Question question : questions) {
            HashMap<String, Object> an = new HashMap<>();
            an.put("info", question);
            an.put("optionList", optionService.list(new QueryWrapper<Option>().eq("question_id",question.getId())));
            questionInfo.add(an);
        }
        result.put("questionInfo", questionInfo);

        return Result.succeed(result);
    }

    @RequiresAuthentication
    @ApiOperation(value = "查看统计结果", notes = "直接发form data的问卷id，名字就叫id")
    @PostMapping("/get_result")
    public Result getResult(@ApiParam(value = "问卷id", required = true) Long id) {

        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "不存在该问卷");

        Assert.isTrue(questionnaire.getUserId().equals(ShiroUtil.getProfile().getId()), "您无权查看此问卷！");

        List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", id));

        List<HashMap<String, Object>> r = new ArrayList<>();

        for (Question question : questionList) {
            HashMap<String, Object> q = new HashMap<>();

            switch (question.getType()) {
                case 0:
                case 1:
                case 3:
                case 4:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
                    q.put("optionList", optionList);
                    break;
                default:
                    List<Answer> answerList = answerService.list(new QueryWrapper<Answer>().eq("question_id", question.getId()));
                    q.put("answerList", answerList);
                    break;

            }


            q.put("question", question);
            r.add(q);
        }

        return Result.succeed(200, "查看成功", r);

    }


    @ApiOperation(value = "提交答案", notes = "json格式")
    @PostMapping("/submit_answer")
    public Result submitAnswer(@ApiParam(value = "问卷id和答案清单", required = true) @Validated @RequestBody AnswerListDto answerListDto) {
        Long questionnaireId = answerListDto.getQuestionnaireId();

//        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
//        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);


//        try {
        Questionnaire questionnaire = questionnaireService.getById(questionnaireId);
        Assert.notNull(questionnaire, "不存在该问卷");

        AnswerList answerList = new AnswerList();
        answerList.setQuestionnaire(questionnaire.getId());
        answerListService.save(answerList);

        LocalDateTime now = LocalDateTime.now();
        if (questionnaire.getEndTime() != null && now.isAfter(questionnaire.getEndTime().plusSeconds(5L))) {
            return Result.fail(400, "问卷提交已截止。", null);
        }

        if (questionnaire.getStartTime() != null && now.isBefore(questionnaire.getStartTime())) {
            return Result.fail(400, "问卷未开始。", null);
        }

        if (questionnaire.getUsing() != 1) {
            return Result.fail(400, "当前问卷已经停止投放。", null);
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
            answer.setAnswerListId(answerList.getId());

            List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));

            switch (question.getType()) {
                case 6:
                case 7:
                    for (Option option : optionList) {
                        if (option.getNumber().equals(answerDto.getNumber()) && option.getLimit() <= option.getAnswerNum()) {
                            return Result.fail(400, "抱歉，第" + questionService.getById(answerDto.getQuestionId()).getNumber() + "题的选择人数已满。", null);
                        }
                    }
                case 0:
                case 1:
                case 3:
                case 4:
                case 8:
                case 9:
                case 10:
                case 11:
                    for (Character ch : answerDto.getNumber().toCharArray()) {
                        for (Option option : optionList) {
                            if (option.getNumber().charAt(0) == ch) {
                                option.setAnswerNum(option.getAnswerNum() + 1);
                                optionService.updateById(option);
                            }
                        }
                    }
                    break;


            }

            answerService.save(answer);

        }
//            transactionManager.commit(status);
//        } catch (Exception e) {
//            transactionManager.rollback(status);
//            throw e;
//        }
        questionnaire.setAnswerNum(questionnaire.getAnswerNum() + 1);
        questionnaireService.updateById(questionnaire);


        return Result.succeed(200, "提交问卷成功", null);
    }

}
