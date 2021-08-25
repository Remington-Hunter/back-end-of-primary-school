package summer.project;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import summer.project.util.VerifyCode;
import summer.project.util.message.SMSParameter;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;

// 导入 SMS 模块的 client
import com.tencentcloudapi.sms.v20190711.SmsClient;

// 导入要请求接口对应的 request response 类
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import summer.project.util.message.Sms;

@RunWith(SpringRunner.class)
@SpringBootTest
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
    public String a;

    @Test
    public void p() {
        System.out.println(a);
    }


    @Test
    public void testSendMessage() {
        System.out.println(smsParameter);
        try {
            //短信模板中的参数列表
            String[] params = {VerifyCode.createRandom(true, 4)};
            System.out.println(params.length + params[0]);
            SmsSingleSender sender = new SmsSingleSender(smsParameter.getAppId(), smsParameter.getAppKey());
            SmsSingleSenderResult result = sender.sendWithParam("86", smsParameter.getPhoneNumbers()[0],
                    smsParameter.getTemplateId(), params, "", "", "");
            System.out.println(result);
        } catch (HTTPException | com.github.qcloudsms.httpclient.HTTPException | JSONException | IOException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        } // JSON 解析错误
        // 网络 IO 错误

    }

    @Value("${tencentcloud.sms.secretId}")
    private String secretId;
    @Value("${tencentcloud.sms.secretKey}")
    private String secretKey;
//    @Value("${tencentcloud.sms.appId}")
//    private String appId;

    @Test
    public void sendPhoneTextMsg() {
        Sms sms = new Sms(secretId, secretKey,new String[]{"15295791959", "15166680628"}, new String[]{"123"});
        try {
            /* 必要步骤：
             * 实例化一个认证对象，入参需要传入腾讯云账户密钥对 secretId 和 secretKey
             * CAM 密钥查询：https://console.cloud.tencent.com/cam/capi*/
            Credential cred = new Credential(secretId, secretKey);

            /* 实例化 SMS 的 client 对象
             * 第二个参数是地域信息，可以直接填写字符串 ap-guangzhou，或者引用预设的常量 */
            SmsClient client = new SmsClient(cred, "");
            /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 您可以直接查询 SDK 源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构 */
            SendSmsRequest req = new SendSmsRequest();

            /* 短信应用 ID: 在 [短信控制台] 添加应用后生成的实际 SDKAppID，例如1400006666 */
            String appid = appId+"";
            req.setSmsSdkAppid(appid);

            /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，可登录 [短信控制台] 查看签名信息 */
            String sign = sms.getSign();
            req.setSign(sign);

            /* 模板 ID: 必须填写已审核通过的模板 ID，可登录 [短信控制台] 查看模板 ID */
            String templateID = sms.getTemplateId();
            req.setTemplateID(templateID);

            /* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
             * 例如+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*/
            req.setPhoneNumberSet(sms.getMobile());

            /* 模板参数: 若无模板参数，则设置为空*/
            req.setTemplateParamSet(sms.getParams());

            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse res = client.SendSms(req);

//            // 输出 JSON 格式的字符串回包
            System.out.println((SendSmsResponse.toJsonString(res)));
//            // 可以取出单个值，您可以通过官网接口文档或跳转到 response 对象的定义处查看返回字段的定义
//            log.info(res.getRequestId());

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }
}
