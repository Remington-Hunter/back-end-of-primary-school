package summer.project.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;
import summer.project.entity.TestUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-19
 */
@Repository
public interface TestUserMapper extends BaseMapper<TestUser> {
    public TestUser queryUserByName(String name);
}
