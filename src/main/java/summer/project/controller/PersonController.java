package summer.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import summer.project.common.dto.PersonListDto;
import summer.project.common.lang.Result;
import summer.project.entity.Answer;
import summer.project.entity.AnswerList;
import summer.project.entity.Person;
import summer.project.entity.Question;
import summer.project.service.AnswerListService;
import summer.project.service.AnswerService;
import summer.project.service.PersonService;
import summer.project.service.QuestionService;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-28
 */
@Api(tags = {"导出/导入名单"})
@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @Autowired
    AnswerListService answerListService;

    @Autowired
    QuestionService questionService;

    @Autowired
    AnswerService answerService;

    @PostMapping("/checkout_not_punch")
    @ApiOperation(value = "导出没打卡的名单, formdata:" +
            "questionnaireId->问卷id")
    public Result checkoutNotPunch(@ApiParam(value = "questionnaireId->问卷id") Long questionnaireId) {
        List<Person> personList = personService.list(new QueryWrapper<Person>().eq("questionnaire", questionnaireId));
        List<AnswerList> answerListList = answerListService.list(new QueryWrapper<AnswerList>().eq("questionnaire", questionnaireId));
        for (AnswerList answerList : answerListList) {
            Question nameQuestion = questionService.getOne(new QueryWrapper<Question>().eq("questionnaire", questionnaireId).eq("number", 1L));
            Question stuIdQuestion = questionService.getOne(new QueryWrapper<Question>().eq("questionnaire", questionnaireId).eq("number", 2L));

            String name = answerService.getOne(new QueryWrapper<Answer>().eq("question_id", nameQuestion.getId()).eq("answer_list_id", answerList.getId())).getContent();
            String stuId = answerService.getOne(new QueryWrapper<Answer>().eq("question_id", stuIdQuestion.getId()).eq("answer_list_id", answerList.getId())).getContent();

            personList.removeIf(person -> person.getName().equals(name) && person.getStuId().equals(stuId));
        }

        return Result.succeed(personList);
    }

    @PostMapping("/lead_in_list")
    @ApiOperation(value = "导入新的人员名单，json")
    public Result leadInList(@ApiParam("校验实体") PersonListDto personListDto) {
        personService.remove(new QueryWrapper<Person>().eq("questionnaire", personListDto.getQuestionnaireId()));

        for (Person person : personListDto.getPersonList()) {
            person.setQuestionnaire(personListDto.getQuestionnaireId());
            personService.save(person);
        }

        return Result.succeed(200, "导入成功", null);
    }

}
