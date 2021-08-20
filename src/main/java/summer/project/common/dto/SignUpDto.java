package summer.project.common.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class SignUpDto implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 15, message = "用户名最大15位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(message = "密码为6-18位", min = 6, max = 18)
    private String password;

    @NotBlank(message = "验证密码不能为空")
    @Size(message = "密码为6-18位", min = 6, max = 18)
    private String checkPassword;

//    @NotNull(message = "是否记住")
//    private Long remember;
}

