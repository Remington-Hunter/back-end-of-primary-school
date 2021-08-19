package summer.project.service;

import summer.project.entity.TestUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-19
 */
public interface TestUserService extends IService<TestUser> {
    TestUser queryUserByName(String name);
}
