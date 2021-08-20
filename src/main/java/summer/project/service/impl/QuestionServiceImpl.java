package summer.project.service.impl;

import summer.project.entity.Question;
import summer.project.mapper.QuestionMapper;
import summer.project.service.QuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-20
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

}
