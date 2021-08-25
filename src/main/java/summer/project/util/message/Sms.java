package summer.project.util.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sms {

    /**
     * 签名
     */
    private String sign ;
    /**
     * 模板
     */
    private String templateId;
    /**
     * 手机号
     */
    private String[] mobile = {"15295791959", "15166680628"};
    /**
     * 模板参数 {}
     */
    private String[] params;
}
