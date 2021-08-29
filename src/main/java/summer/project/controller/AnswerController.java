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
import java.util.*;

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
    PersonService personService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @RequiresAuthentication
    @ApiOperation(value = "查看统计结果，答卷为单位", notes = "直接发form data的问卷id，名字就叫id")
    @PostMapping("/get_result_by_questionnaire")
    public Result getResultByQuestionnaire(@ApiParam(value = "问卷id", required = true) Long id, @ApiParam(value = "对于考试问卷，如果是1，就是成绩排序") Long byGrade) {
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "不存在该问卷");

        Assert.isTrue(questionnaire.getUserId().equals(ShiroUtil.getProfile().getId()), "您无权查看此问卷！");
        HashMap<String, Object> result = new HashMap<>(128);
        List<AnswerList> answerListList = answerListService.list(new QueryWrapper<AnswerList>().eq("questionnaire", questionnaire.getId()));
        List<HashMap<String, Object>> answerInfo = new ArrayList<>();
        for (AnswerList answerList : answerListList) {
            HashMap<String, Object> an = new HashMap<>(128);
            an.put("info", answerList);
            List<Answer> answers = answerService.list(new QueryWrapper<Answer>().eq("answer_list_id", answerList.getId()));
            answers.sort((o1, o2) -> (int) (o1.getQuestionId() - o2.getQuestionId()));
            an.put("answerList", answers);
            answerInfo.add(an);
        }

        if (byGrade != null && byGrade == 1) {
            answerInfo.sort((o1, o2) -> ((AnswerList) o2.get("info")).getPoint() - ((AnswerList) o1.get("info")).getPoint());
        }
        result.put("answerInfo", answerInfo);

        List<Question> questions = questionService.list(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));
        List<Object> questionInfo = new ArrayList<>();
        for (Question question : questions) {
            HashMap<String, Object> an = new HashMap<>(128);
            an.put("info", question);
            an.put("optionList", optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId())));
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

            Integer type = question.getType();
            switch (type) {
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
                case 12:
                case 13:
                    List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
                    q.put("optionList", optionList);
                    break;

                default:
                    List<Answer> answerList = answerService.list(new QueryWrapper<Answer>().eq("question_id", question.getId()));
                    q.put("answerList", answerList);
                    break;

            }

            if (type == 12 || type == 13) {
                List<Answer> answerList = answerService.list(new QueryWrapper<Answer>().eq("question_id", question.getId()));
                int count = 0;
                for (Answer answer : answerList) {
                    if (question.getAnswer().equals(answer.getNumber())) {
                        count++;
                    }
                }
                question.setRate(1.0 * count / answerList.size());
            } else if (type == 14) {
                List<Answer> answerList = answerService.list(new QueryWrapper<Answer>().eq("question_id", question.getId()));
                int count = 0;
                for (Answer answer : answerList) {
                    if (question.getAnswer().equals(answer.getContent())) {
                        count++;
                    }
                }
                question.setRate(1.0 * count / answerList.size());
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


        Questionnaire questionnaire = questionnaireService.getById(questionnaireId);
        Assert.notNull(questionnaire, "不存在该问卷");

        if (questionnaire.getLimit() >= 0 && questionnaire.getLimit() < questionnaire.getAnswerNum()) {
            return Result.fail(400, "问卷填报人数已满。", null);
        }

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




        Integer punchType = 4;

        if (questionnaire.getType().equals(punchType)) {


            Question stuIdQuestion = questionService.getOne(new QueryWrapper<Question>().eq("number", 2).eq("questionnaire", questionnaireId));
            Person person = personService.getOne(new QueryWrapper<Person>().eq("questionnaire", questionnaireId).eq("stu_id", answerListDto.getAnswerDtoList().get(1).getContent()));
            if (person == null) {
                return Result.fail("您不在打卡名单内。");
            }

            List<Answer> list = answerService.list(new QueryWrapper<Answer>().eq("question_id", stuIdQuestion.getId()).eq("content", answerListDto.getAnswerDtoList().get(1).getContent()));
            for (Answer answer : list) {
                if (answerListService.getById(answer.getAnswerListId()).getSubmitTime().toLocalDate().equals(LocalDateTime.now().toLocalDate())){
                    return Result.fail(400, "您今日已经打卡。", null);
                }
            }
        }

        Integer signNum = 2;
        if (questionnaire.getType().equals(signNum)) {
            for (AnswerDto answerDto : answerListDto.getAnswerDtoList()) {
                Long questionId = answerDto.getQuestionId();
                Question question = questionService.getById(questionId);
                Assert.notNull(question, "问题不存在");

                List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
                for (Character ch : answerDto.getNumber().toCharArray()) {
                    for (Option option : optionList) {
                        if (Character.valueOf(option.getNumber().charAt(0)).equals(ch) && option.getLimit() <= option.getAnswerNum()) {
                            return Result.fail(400, "抱歉，第" + questionService.getById(answerDto.getQuestionId()).getNumber() + "题的选择人数已满。", null);
                        }
                    }
                }

            }

        }

        AnswerList answerList = new AnswerList();
        answerList.setQuestionnaire(questionnaire.getId());
        answerList.setPoint(answerListDto.getPoint());
        answerListService.save(answerList);

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
                case 0:
                case 1:
                case 3:
                case 4:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    for (Character ch : answerDto.getNumber().toCharArray()) {
                        for (Option option : optionList) {
                            if (option.getNumber().charAt(0) == ch) {
                                option.setAnswerNum(option.getAnswerNum() + 1);
                                optionService.updateById(option);
                            }
                        }
                    }
                    break;
                default:

            }

            answerService.save(answer);

        }

        questionnaire.setAnswerNum(questionnaire.getAnswerNum() + 1);
        questionnaireService.updateById(questionnaire);


        return Result.succeed(200, "提交问卷成功", null);
    }

}
