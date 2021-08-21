package summer.project.util;

import org.apache.commons.mail.HtmlEmail;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

@Component
public class EmailUtil {


    static Random random = new Random();

    //邮箱验证码
    public static boolean sendEmail(String emailAddress, String code) {
        try {
            HtmlEmail email = new HtmlEmail();//不用更改
            email.setHostName("smtp.qq.com");//需要修改，126邮箱为smtp.126.com,163邮箱为163.smtp.com，QQ为smtp.qq.com
            email.setCharset("UTF-8");
            email.addTo(emailAddress);// 收件地址

            email.setFrom("2320092610@qq.com", random.nextInt()+"");//此处填邮箱地址和用户名,用户名可以任意填写

            email.setAuthentication("2320092610@qq.com", "ltsjprirnqwlecgd");// 此处填写邮箱地址和客户端授权码

            email.setSubject("【问卷星球】注册登录验证"+ random.nextInt()); //此处填写邮件名，邮件名可任意填写
            email.setMsg("尊敬的用户:\n\n您好!\n\n您本次的邮箱验证码是:" + code+",如果不是您本人的操作，请忽略这封邮件,谢谢配合。\n\n祝您生活愉快!\n\n问卷星球团队\n\n"
                    + LocalDate.now()); //此处填写邮件内容
            email.send();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
