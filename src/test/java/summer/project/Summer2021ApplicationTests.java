package summer.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import summer.project.util.EmailUtil;

@SpringBootTest
class Summer2021ApplicationTests {


    @Test
    void contextLoads() {
    }

    @Test
    void SendEmailTest() {
        EmailUtil.sendEmail("2320092610@qq.com", "4312");
    }

    @Test
    void testLong() {
        Long a = null;
        if (a == 1) {
            System.out.println(1);
        }
    }

}
