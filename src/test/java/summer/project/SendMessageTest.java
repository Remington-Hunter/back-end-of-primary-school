package summer.project;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.json.JSONException;
import org.junit.Test;
import summer.project.util.VerifyCode;
import summer.project.util.message.SMSParameter;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;

public class SendMessageTest {

    private final SMSParameter smsParameter = new SMSParameter();

    @Test
    public void testM() {
        SMSParameter msParameter = new SMSParameter();
    }

    @Test
    public void testSendMessage() {
        System.out.println(smsParameter);
        try {
            //短信模板中的参数列表
            String[] params = {VerifyCode.createRandom(true, 4), "5"};
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
