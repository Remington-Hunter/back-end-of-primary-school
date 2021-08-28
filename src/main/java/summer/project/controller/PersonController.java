package summer.project.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import summer.project.common.dto.PersonListDto;
import summer.project.common.lang.Result;
import summer.project.entity.*;
import summer.project.service.AnswerListService;
import summer.project.service.AnswerService;
import summer.project.service.PersonService;
import summer.project.service.QuestionService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-28
 */
@Slf4j
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
        System.out.println(answerListList.get(0).getSubmitTime().toLocalDate());
        System.out.println(LocalDateTime.now().toLocalDate());
        answerListList.removeIf(answerList -> !answerList.getSubmitTime().toLocalDate().equals(LocalDateTime.now().toLocalDate()));
        for (AnswerList answerList : answerListList) {
            Question stuIdQuestion = questionService.getOne(new QueryWrapper<Question>().eq("questionnaire", questionnaireId).eq("number", 2));

            String stuId = answerService.getOne(new QueryWrapper<Answer>().eq("question_id", stuIdQuestion.getId()).eq("answer_list_id", answerList.getId())).getContent();

            personList.removeIf(person -> person.getStuId().equals(stuId));
        }

        return Result.succeed(personList);
    }

    @PostMapping("/lead_in_list")
    @ApiOperation(value = "导入新的人员名单，json")
    public Result leadInList(@ApiParam("校验实体") @RequestBody PersonListDto personListDto) {
        personService.remove(new QueryWrapper<Person>().eq("questionnaire", personListDto.getQuestionnaireId()));


        HashMap<String, String> hashMap = new HashMap<>(128);

        Set<String> set = new HashSet<>(128);

        for (Person person : personListDto.getPersonList()) {
            if (person.getName().equals(hashMap.get(person.getStuId()))) {
                set.add(person.getStuId());
            } else {
                hashMap.put(person.getStuId(), person.getName());
            }
        }

        if (set.size() != 0) {
            return Result.fail("存在冲突的数据。", set);
        }


        for (Person person : personListDto.getPersonList()) {
            person.setQuestionnaire(personListDto.getQuestionnaireId());
            personService.save(person);
        }

        return Result.succeed(200, "导入成功", null);
    }

    @PostMapping("/lead_in_list_by_excel")
    @ApiOperation(value = "导入新的人员名单，json")
    public Result leadInList(@ApiParam(value = "questionnaireId") @RequestParam("questionnaireId") Long questionnaireId, @ApiParam(value = "file") @RequestParam("file") MultipartFile file) {
        personService.remove(new QueryWrapper<Person>().eq("questionnaire", questionnaireId));

        List<PersonExcelModel> list;

        try {
            list = EasyExcel.read(file.getInputStream(), PersonExcelModel.class, new AnalysisEventListener() {
                private List<Object> datas = new ArrayList<>();
                /**
                 * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
                 */
                @Override
                public void invoke(Object data, AnalysisContext context) {
                    //数据存储到list，供批量处理，或后续自己业务逻辑处理。
                    log.info("读取到数据{}",data);
                    datas.add(data);
                    //根据业务自行处理，可以写入数据库等等
                }

                //所以的数据解析完了调用
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("所有数据解析完成");
                }
            }).sheet().doReadSync();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("数据解析失败");
        }

        HashMap<String, String> hashMap = new HashMap<>(128);

        Set<String> set = new HashSet<>(128);

        for (PersonExcelModel person : list) {
            if (person.getName().equals(hashMap.get(person.getStuId()))) {
                set.add(person.getStuId());
            } else {
                hashMap.put(person.getStuId(), person.getName());
            }
        }

        if (set.size() != 0) {
            return Result.fail("存在冲突的数据。", set);
        }


        for (PersonExcelModel person : list) {
            personService.save(new Person(person, questionnaireId));
        }

        return Result.succeed(200, "导入成功", list);
    }


    @PostMapping("/get_all_list")
    @ApiOperation(value = "导出所有名单")
    public Result leadInList(@ApiParam("问卷id") Long questionnaireId) {


        return Result.succeed(200, "导出成功", personService.list(new QueryWrapper<Person>().eq("questionnaire", questionnaireId)));
    }
}
