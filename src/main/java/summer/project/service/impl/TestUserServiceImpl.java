package summer.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import summer.project.entity.TestUser;
import summer.project.mapper.TestUserMapper;
import summer.project.service.TestUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-19
 */
@Service
public class TestUserServiceImpl extends ServiceImpl<TestUserMapper, TestUser> implements TestUserService {

    @Autowired
    TestUserMapper userMapper;

    @Override
    public TestUser queryUserByName(String name) {
        return userMapper.queryUserByName(name);
    }
}
