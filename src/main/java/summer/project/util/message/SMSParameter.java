package summer.project.util.message;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 *  封装发送短信所需的参数
 */
@Data
public class SMSParameter {

//    @Value("${tencentcloud.sms.secretId}")
//    private String secretId;
//    @Value("${tencentcloud.sms.secretKey}")

    // 短信应用 SDK AppID，SDK AppID 以1400开头
    @Value("${tencentcloud.sms.appId}")
    private int appId;
    // 短信应用SDK AppKey
    @Value("${tencentcloud.sms.appKey}")
    private String appKey;
    // 需要发送短信的手机号码，可以定义多个手机号码
    private String[] phoneNumbers = {"15166680628","15295791959"};
    // 短信模板ID，需要在短信控制台中申请，我们查看自己的短信模板ID即可
    @Value("${tencentcloud.sms.templateId}")
    private int templateId;
    // 签名，签名参数使用的是`签名内容`，而不是`签名ID`，真实的签名需要在短信控制台申请，这里按自己的来修改就好
//    @Value("${tencentcloud.sms.smsSign}")
    private String smsSign = "赵宸个人学生作业";
}
