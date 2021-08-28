package summer.project.service.impl;

import summer.project.entity.Person;
import summer.project.mapper.PersonMapper;
import summer.project.service.PersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-28
 */
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements PersonService {

}
