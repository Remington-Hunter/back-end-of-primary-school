package summer.project;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.json.JSONException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import summer.project.util.VerifyCode;
import summer.project.util.message.SMSParameter;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;

public class SendMessageTest {

    @Value("${tencentcloud.sms.appId}")
    public int appId;
    // 短信应用SDK AppKey
    @Value("${tencentcloud.sms.appKey}")
    public String appKey;
    // 需要发送短信的手机号码，可以定义多个手机号码
    public String[] phoneNumbers = {"15166680628","15295791959"};
    // 短信模板ID，需要在短信控制台中申请，我们查看自己的短信模板ID即可
    @Value("${tencentcloud.sms.templateId}")
    public int templateId;
    // 签名，签名参数使用的是`签名内容`，而不是`签名ID`，真实的签名需要在短信控制台申请，这里按自己的来修改就好
//    @Value("${tencentcloud.sms.smsSign}")
    public String smsSign = "赵宸个人学生作业";

    SMSParameter smsParameter = new SMSParameter();

    @Value("${storage.pathname}")
    private String a;

    @Test
    public void p() {
        System.out.println(a);
    }

    @Test
    public void testM() {
        SMSParameter msParameter = new SMSParameter(appId, appKey, phoneNumbers, templateId, smsSign);
    }

    @Test
    public void testSendMessage() {
        System.out.println(smsParameter);
        try {
            //短信模板中的参数列表
            String[] params = {VerifyCode.createRandom(true, 4)};
            SmsSingleSender sender = new SmsSingleSender(smsParameter.getAppId(), smsParameter.getAppKey());
            SmsSingleSenderResult result = sender.sendWithParam("86", smsParameter.getPhoneNumbers()[0],
                    smsParameter.getTemplateId(), params, smsParameter.getSmsSign(), "", "");
            System.out.println(result);
        } catch (HTTPException | com.github.qcloudsms.httpclient.HTTPException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // JSON 解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络 IO 错误
            e.printStackTrace();
        }
    }
}
