package summer.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import summer.project.util.EmailUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    }

    @Test
    void testShuffle() {
        List<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        Collections.shuffle(a, new Random(1));
        System.out.println(a);
    }

}
