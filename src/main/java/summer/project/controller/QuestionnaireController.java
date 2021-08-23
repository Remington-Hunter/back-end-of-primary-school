package summer.project.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.crypto.hash.Hash;
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
import summer.project.entity.Answer;
import summer.project.entity.Option;
import summer.project.entity.Question;
import summer.project.entity.Questionnaire;
import summer.project.service.AnswerService;
import summer.project.service.OptionService;
import summer.project.service.QuestionService;
import summer.project.service.QuestionnaireService;
import summer.project.util.CopyUtil;
import summer.project.util.ShiroUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-20
 */
@Api(tags = "问卷相关的接口")
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

    @Autowired
    AnswerService answerService;

    //    @Transactional
    @RequiresAuthentication
    @PostMapping("/save_questionnaire")
    @ApiOperation(value = "保存问卷", notes = "发送用户ID（userId），和一个问题的列表，每个问题包含答案（如果必要），具体看下面的描述，" +
            "如果这个问卷是已经修改过的，那就带着id，如果是新的问卷，id就不用填")
    public Result saveQuestionnaire(@ApiParam(value = "要提交问卷", required = true) @Validated @RequestBody QuestionnaireDto questionnaireDto) {

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
        Long id;
        try {
            // 新问卷
            Questionnaire questionnaire;
            if (questionnaireDto.getId() == null) {

                questionnaire = new Questionnaire(
                        questionnaireDto.getUserId(),
                        questionnaireDto.getTitle(),
                        questionnaireDto.getDescription(),
                        LocalDateTime.now(),
                        questionnaireDto.getStartTime(),
                        questionnaireDto.getEndTime(),
                        questionnaireDto.getNeedNum(),
                        questionnaireDto.getLimit(),
                        questionnaireDto.getType()
                );

                questionnaireService.save(questionnaire);


            } else {
                // 旧问卷
                questionnaire = questionnaireService.getById(questionnaireDto.getId());
                Assert.notNull(questionnaire, "不存在该问卷。");
                Assert.isTrue(questionnaire.getUserId().equals(ShiroUtil.getProfile().getId()), "无权限修改他人问卷。");
                questionnaire.setId(questionnaireDto.getId());
                questionnaire.setCreateTime(LocalDateTime.now());
                questionnaire.setTitle(questionnaireDto.getTitle());
                questionnaire.setStartTime(questionnaireDto.getStartTime());
                questionnaire.setEndTime(questionnaireDto.getEndTime());
                questionnaire.setNeedNum(questionnaireDto.getNeedNum());
                questionnaire.setLimit(questionnaireDto.getLimit());
                questionnaire.setType(questionnaireDto.getType());
                questionnaireService.updateById(questionnaire);
//                List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));
                questionService.remove(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));
            }
            for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
                Question question = new Question(
                        questionnaire.getId(),
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
                            question.getId(),
                            optionDto.getContent(),
                            optionDto.getLimit(),
                            optionDto.getNumber()
                    );
                    optionService.save(option);
                }
            }
            //                for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
//                    questionList.removeIf(question -> question.getId().equals(questionDto.getId()));
//                }
//                questionService.removeByIds(questionList);
//                for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
//
//                    if (questionDto.getId() == null) {
//                        // 新问题
//                        Question question = new Question(
//                                questionDto.getContent(),
//                                questionDto.getAnswer(),
//                                questionDto.getPoint(),
//                                questionDto.getType(),
//                                questionDto.getNumber(),
//                                questionDto.getRequired(),
//                                questionDto.getComment()
//                        );
//                        questionService.save(question);
//                        for (OptionDto optionDto : questionDto.getOptionList()) {
//                            Option option = new Option(
//                                    optionDto.getContent(),
//                                    optionDto.getLimit(),
//                                    optionDto.getNumber()
//                            );
//                            optionService.save(option);
//                        }
//                    } else {
//                        // 旧问题
//                        Question question = questionService.getById(questionDto.getId());
//                        Assert.notNull(question, "题目不存在");
//                        question.setAnswer(questionDto.getAnswer());
//                        question.setComment(question.getComment());
//                        question.setType(question.getType());
//                        question.setContent(question.getContent());
//                        question.setNumber(question.getNumber());
//                        question.setPoint(question.getPoint());
//                        question.setRequired(question.getRequired());
//
//                        questionService.save(question);
//
//                        List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
//                        for (OptionDto optionDto : questionDto.getOptionList()) {
//                            optionList.removeIf(option -> option.getId().equals(optionDto.getId()));
//                        }
//                        optionService.removeByIds(optionList);
//
//                        for (OptionDto optionDto : questionDto.getOptionList()) {
//
//                            if (optionDto.getId() == null) {
//                                // 新选项
//                                Option option = new Option(
//                                        optionDto.getContent(),
//                                        optionDto.getLimit(),
//                                        optionDto.getNumber()
//                                );
//                                optionService.save(option);
//                            } else {
//                                // 旧选项
//                                Option option = optionService.getById(optionDto.getId());
//                                Assert.notNull(option, "选项不存在");
//                                option.setContent(optionDto.getContent());
//                                option.setLimit(optionDto.getLimit());
//                                option.setNumber(optionDto.getNumber());
//                                optionService.updateById(option);
//                            }
//                        }
//                    }
//
//                }
            id = questionnaire.getId();
            transactionManager.commit(status);
        } catch (Exception e) {

            transactionManager.rollback(status);
            return Result.fail("保存失败！");
        }


        return Result.succeed(200, "问卷保存成功!", id);
    }


    @RequiresAuthentication
    @PostMapping("/throw_questionnaire")
    @ApiOperation(value = "投放问卷，form data传参数 questionnaireId:问卷ID")
    public Result throwQuestionnaire(@ApiParam(value = "要投放的问卷id", required = true) Long questionnaireId) {
        Questionnaire questionnaire = questionnaireService.getById(questionnaireId);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(ShiroUtil.getProfile().getId().equals(questionnaire.getUserId()), "您无权操作此问卷！");

        String md5;
        if (questionnaire.getUrl() == null || questionnaire.getUrl().equals("")) {
            md5 = questionnaire.getId() + "_" + SecureUtil.md5(LocalDateTime.now() + "").substring(0, 4);
            questionnaire.setUrl(md5);
        }
        questionnaire.setPreparing(1);
        questionnaire.setStopping(0);
        questionnaire.setDeleted(0);
        questionnaire.setUsing(0);
        questionnaireService.updateById(questionnaire);
        md5 = questionnaire.getUrl();
        return Result.succeed(200, "问卷发布成功!", md5);
    }


    @RequiresAuthentication
    @PostMapping("/publish_questionnaire")
    @ApiOperation(value = "发布问卷，form data传参数 questionnaireId:问卷ID")
    public Result submitQuestionnaire(@ApiParam(value = "要发布的问卷id", required = true) Long questionnaireId) {
        Questionnaire questionnaire = questionnaireService.getById(questionnaireId);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(ShiroUtil.getProfile().getId().equals(questionnaire.getUserId()), "您无权操作此问卷！");
        questionnaire.setPreparing(0);
        questionnaire.setDeleted(0);
        questionnaire.setUsing(1);
        questionnaire.setStopping(0);

        questionnaireService.updateById(questionnaire);
        return Result.succeed(200, "问卷发布成功!", null);
    }

    @RequiresAuthentication
    @PostMapping("/stop_questionnaire")
    @ApiOperation(value = "停止问卷，form data传参数 questionnaireId:问卷ID")
    public Result stopQuestionnaire(@ApiParam(value = "要停止的问卷id", required = true) Long questionnaireId) {
        Questionnaire questionnaire = questionnaireService.getById(questionnaireId);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(ShiroUtil.getProfile().getId().equals(questionnaire.getUserId()), "您无权操作此问卷！");
        questionnaire.setPreparing(0);
        questionnaire.setDeleted(0);
        questionnaire.setUsing(0);
        questionnaire.setStopping(1);

        questionnaireService.updateById(questionnaire);
        return Result.succeed(200, "问卷停止成功!", null);
    }

    @RequiresAuthentication
    @PostMapping("/prepare_questionnaire")
    @ApiOperation(value = "修改问卷状态为准备中，form data传参数 questionnaireId:问卷ID")
    public Result prepareQuestionnaire(@ApiParam(value = "要修改状态的问卷id", required = true) Long questionnaireId) {
        Questionnaire questionnaire = questionnaireService.getById(questionnaireId);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(ShiroUtil.getProfile().getId().equals(questionnaire.getUserId()), "您无权操作此问卷！");
        questionnaire.setPreparing(1);
        questionnaire.setDeleted(0);
        questionnaire.setUsing(0);
        questionnaire.setStopping(0);

        questionnaireService.updateById(questionnaire);
        return Result.succeed(200, "问卷在准备中!", null);
    }

    @RequiresAuthentication
    @PostMapping("/get_link")
    @ApiOperation(value = "获得问卷的链接后缀，相当于投放问卷", notes = "直接发送问卷的id，发form data")
    public Result getLink(@ApiParam(value = "要得到链接的问卷id", required = true) Long id) {
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "不存在该问卷");
        Assert.isTrue(questionnaire.getUserId().equals(ShiroUtil.getProfile().getId()), "您无权访问此问卷");
        String md5;
        if (questionnaire.getUrl() == null || questionnaire.getUrl().equals("")) {
            md5 = questionnaire.getId() + "_" + SecureUtil.md5(LocalDateTime.now() + "").substring(0, 4);
            questionnaire.setUrl(md5);
            questionnaireService.updateById(questionnaire);
        }
        md5 = questionnaire.getUrl();
        return Result.succeed(md5);
    }

    @RequiresAuthentication
    @PostMapping("/get_new_link")
    @ApiOperation(value = "获得新的链接，之前的链接失效", notes = "直接发送问卷的id，发form data")
    public Result getNewLink(@ApiParam(value = "要得到链接的问卷id", required = true) Long id) {
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

    @RequiresAuthentication
    @PostMapping("/get_questionnaire_by_id")
    @ApiOperation(value = "获得所有的问卷的基本信息", notes = "带着Authorization请求头，需要id")
    @ApiResponses(
            @ApiResponse(code = 200, message = "你的data长这个样")
    )
    public Result getQuestionnaireById(@ApiParam(value = "问卷Id", required = true) Long id) {
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "不存在该问卷。");
        List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", id));

        List<HashMap<String, Object>> questions = new ArrayList<>();
        for (Question question : questionList) {
            HashMap<String, Object> qMap = new HashMap<>();
            qMap.put("question", question);
            qMap.put("optionList", optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId())));
            questions.add(qMap);
        }


        HashMap<String, Object> result = new HashMap<>();
        result.put("questionnaire", questionnaire);
        result.put("questionList", questions);

        return Result.succeed(result);
    }

    @RequiresAuthentication
    @PostMapping("/throw_to_trashcan")
    @ApiOperation(value = "将问卷放到回收站", notes = "直接发送问卷的id，发form data")
    public Result throwToTrash(@ApiParam(value = "要放入回收站的问卷id", required = true) Long id) {
        Long userId = ShiroUtil.getProfile().getId();
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(userId.equals(questionnaire.getUserId()), "你无权操作此问卷！");
        questionnaire.setUsing(0);
        questionnaire.setDeleted(1);
        questionnaire.setPreparing(0);
        questionnaire.setStopping(0);
        questionnaire.setUrl("");
        questionnaireService.updateById(questionnaire);

        return Result.succeed("该问卷已放入回收站，之前发布的链接已失效。");
    }

    @RequiresAuthentication
    @PostMapping("/delete_questionnaire")
    @ApiOperation(value = "将回收站的问卷直接删除", notes = "直接发送问卷的id，发form data")
    public Result deleteQuestionnaire(@ApiParam(value = "要彻底删除的问卷id", required = true) Long id) {
        Long userId = ShiroUtil.getProfile().getId();
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(userId.equals(questionnaire.getUserId()), "你无权操作此问卷！");
        Assert.isTrue(questionnaire.getDeleted().equals(1), "请先将问卷放入回收站。");
        questionnaireService.removeById(id);

        return Result.succeed("该问卷已删除。");
    }

    @RequiresAuthentication
    @PostMapping("/take_out_from_trashcan")
    @ApiOperation(value = "将回收站的问卷取出来恢复", notes = "直接发送问卷的id，发form data")
    public Result takeOutFromTrashcan(@ApiParam(value = "要彻底删除的问卷id", required = true) Long id) {
        Long userId = ShiroUtil.getProfile().getId();
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(userId.equals(questionnaire.getUserId()), "你无权操作此问卷！");
        Assert.isTrue(questionnaire.getDeleted().equals(1), "请先将问卷放入回收站。");
        questionnaire.setPreparing(0);
        questionnaire.setStopping(1);
        questionnaire.setDeleted(0);
        questionnaire.setUsing(0);
        questionnaireService.updateById(questionnaire);

        return Result.succeed("该问卷已删除。");
    }

    @RequiresAuthentication
    @PostMapping("/copy_questionnaire")
    @ApiOperation(value = "复制问卷", notes = "直接发送问卷的id，发form data")
    public Result CopyQuestionnaire(@ApiParam(value = "要复制的问卷的id", required = true) Long id) {
        Long userId = ShiroUtil.getProfile().getId();
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(userId.equals(questionnaire.getUserId()), "你无权操作此问卷！");
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
        Questionnaire newQuestionnaire = null;
        try {
            newQuestionnaire = (Questionnaire) CopyUtil.deepCopy(questionnaire);
            newQuestionnaire.setAnswerNum(0L);
            newQuestionnaire.setPreparing(1);
            newQuestionnaire.setUsing(0);
            newQuestionnaire.setDeleted(0);
            newQuestionnaire.setStopping(0);
            newQuestionnaire.setCreateTime(LocalDateTime.now());
            newQuestionnaire.setUrl("");
            questionnaireService.save(newQuestionnaire);

            List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));

            for (Question question : questionList) {
                List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
                question.setQuestionnaire(newQuestionnaire.getId());
                questionService.save(question);
                for (Option option : optionList) {
                    option.setQuestionId(question.getId());
                    option.setAnswerNum(0L);
                }
                optionService.saveBatch(optionList);
            }
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }

        assert newQuestionnaire != null;
        return Result.succeed(201, "复制成功", newQuestionnaire.getId());
    }

    @PostMapping("/get_questionnaire")
    @ApiOperation(value = "得到问卷", notes = "把链接/vj/后面的那一串码抠出来发过来")
    public Result getQuestionnaire(@ApiParam(value = "xx_xxxxx的码", required = true) String md5) {
        Questionnaire questionnaire = questionnaireService.getOne(new QueryWrapper<Questionnaire>().eq("url", md5));
        Assert.notNull(questionnaire, "链接已失效");
        List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));

        List<HashMap<String, Object>> questions = new ArrayList<>();
        for (Question question : questionList) {
            HashMap<String, Object> qMap = new HashMap<>();
            qMap.put("question", question);
            qMap.put("optionList", optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId())));
            questions.add(qMap);
        }


        HashMap<String, Object> result = new HashMap<>();
        result.put("questionnaire", questionnaire);
        result.put("questionList", questions);

        return Result.succeed(result);
    }

    @RequiresAuthentication
    @PostMapping("/edit_questionnaire")
    @ApiOperation(value = "保存问卷", notes = "发送用户ID（userId），和一个问题的列表，每个问题包含答案（如果必要），具体看下面的描述，" +
            "如果这个问卷是已经修改过的，那就带着id，如果是新的问卷，id就不用填")
    public Result editQuestionnaire(@ApiParam(value = "问卷的信息", required = true) @Validated @RequestBody QuestionnaireDto questionnaireDto) {

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
        Long id;
        try {
            // 新问卷
            Questionnaire questionnaire;
            if (questionnaireDto.getId() == null) {

                questionnaire = new Questionnaire(
                        questionnaireDto.getUserId(),
                        questionnaireDto.getTitle(),
                        questionnaireDto.getDescription(),
                        LocalDateTime.now(),
                        questionnaireDto.getStartTime(),
                        questionnaireDto.getEndTime(),
                        questionnaireDto.getNeedNum(),
                        questionnaireDto.getLimit(),
                        questionnaireDto.getType()
                );

                questionnaireService.save(questionnaire);


            } else {
                // 旧问卷
                questionnaire = questionnaireService.getById(questionnaireDto.getId());
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
//                List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));
                questionService.remove(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));
            }
            for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
                Question question = new Question(
                        questionnaire.getId(),
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
                            question.getId(),
                            optionDto.getContent(),
                            optionDto.getLimit(),
                            optionDto.getNumber()
                    );
                    optionService.save(option);
                }
            }
            //                for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
//                    questionList.removeIf(question -> question.getId().equals(questionDto.getId()));
//                }
//                questionService.removeByIds(questionList);
//                for (QuestionDto questionDto : questionnaireDto.getQuestionList()) {
//
//                    if (questionDto.getId() == null) {
//                        // 新问题
//                        Question question = new Question(
//                                questionDto.getContent(),
//                                questionDto.getAnswer(),
//                                questionDto.getPoint(),
//                                questionDto.getType(),
//                                questionDto.getNumber(),
//                                questionDto.getRequired(),
//                                questionDto.getComment()
//                        );
//                        questionService.save(question);
//                        for (OptionDto optionDto : questionDto.getOptionList()) {
//                            Option option = new Option(
//                                    optionDto.getContent(),
//                                    optionDto.getLimit(),
//                                    optionDto.getNumber()
//                            );
//                            optionService.save(option);
//                        }
//                    } else {
//                        // 旧问题
//                        Question question = questionService.getById(questionDto.getId());
//                        Assert.notNull(question, "题目不存在");
//                        question.setAnswer(questionDto.getAnswer());
//                        question.setComment(question.getComment());
//                        question.setType(question.getType());
//                        question.setContent(question.getContent());
//                        question.setNumber(question.getNumber());
//                        question.setPoint(question.getPoint());
//                        question.setRequired(question.getRequired());
//
//                        questionService.save(question);
//
//                        List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
//                        for (OptionDto optionDto : questionDto.getOptionList()) {
//                            optionList.removeIf(option -> option.getId().equals(optionDto.getId()));
//                        }
//                        optionService.removeByIds(optionList);
//
//                        for (OptionDto optionDto : questionDto.getOptionList()) {
//
//                            if (optionDto.getId() == null) {
//                                // 新选项
//                                Option option = new Option(
//                                        optionDto.getContent(),
//                                        optionDto.getLimit(),
//                                        optionDto.getNumber()
//                                );
//                                optionService.save(option);
//                            } else {
//                                // 旧选项
//                                Option option = optionService.getById(optionDto.getId());
//                                Assert.notNull(option, "选项不存在");
//                                option.setContent(optionDto.getContent());
//                                option.setLimit(optionDto.getLimit());
//                                option.setNumber(optionDto.getNumber());
//                                optionService.updateById(option);
//                            }
//                        }
//                    }
//
//                }
            id = questionnaire.getId();
            transactionManager.commit(status);
        } catch (Exception e) {

            transactionManager.rollback(status);
            return Result.fail("保存失败！");
        }


        return Result.succeed(200, "问卷保存成功!", id);
    }

    @RequiresAuthentication
    @PostMapping("/delete_and_get_questionnaire_by_id")
    @ApiOperation(value = "删除问卷所有调查结果并获得问卷的题目信息", notes = "发form data，要删除的 id")
    public Result deleteAndGetQuestionnaireById(@ApiParam(value = "要操作的问卷的id", required = true) Long id) {
        Long userId = ShiroUtil.getProfile().getId();
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(userId.equals(questionnaire.getUserId()), "你无权操作此问卷！");

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);

        try {
            questionnaire.setCreateTime(LocalDateTime.now());
            questionnaireService.updateById(questionnaire);
            List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", id));
            for (Question question : questionList) {
                answerService.remove(new QueryWrapper<Answer>().eq("question_id", question.getId()));
                List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
                for (Option option : optionList) {
                    option.setAnswerNum(0L);
                }
                optionService.updateBatchById(optionList);
            }

            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }


        return getQuestionnaireById(id);
    }


    @RequiresAuthentication
    @PostMapping("/throw_and_get_new_questionnaire")
    @ApiOperation(value = "原有问卷放到回收站，复制一个新的问卷", notes = "发form data，问卷的 id")
    public Result throwAndGetNewQuestionnaire(@ApiParam(value = "要操作的问卷的id", required = true) Long id) {
        throwToTrash(id);
        Long userId = ShiroUtil.getProfile().getId();
        Questionnaire questionnaire = questionnaireService.getById(id);
        Assert.notNull(questionnaire, "问卷不存在");
        Assert.isTrue(userId.equals(questionnaire.getUserId()), "你无权操作此问卷！");
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
        Questionnaire newQuestionnaire = null;
        try {
            newQuestionnaire = (Questionnaire) CopyUtil.deepCopy(questionnaire);
            newQuestionnaire.setAnswerNum(0L);
            newQuestionnaire.setPreparing(1);
            newQuestionnaire.setUsing(0);
            newQuestionnaire.setDeleted(0);
            newQuestionnaire.setStopping(0);
            newQuestionnaire.setCreateTime(LocalDateTime.now());
            newQuestionnaire.setUrl("");
            questionnaireService.save(newQuestionnaire);

            List<Question> questionList = questionService.list(new QueryWrapper<Question>().eq("questionnaire", questionnaire.getId()));

            for (Question question : questionList) {
                List<Option> optionList = optionService.list(new QueryWrapper<Option>().eq("question_id", question.getId()));
                question.setQuestionnaire(newQuestionnaire.getId());
                questionService.save(question);
                for (Option option : optionList) {
                    option.setQuestionId(question.getId());
                    option.setAnswerNum(0L);
                }
                optionService.saveBatch(optionList);
            }
            questionnaire.setDeleted(1);
            questionnaire.setPreparing(0);
            questionnaire.setUsing(0);
            questionnaire.setStopping(0);
            questionnaireService.updateById(questionnaire);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }

        assert newQuestionnaire != null;
        return getQuestionnaireById(newQuestionnaire.getId());
    }
}
