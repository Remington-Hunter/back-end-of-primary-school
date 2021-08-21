package summer.project.common.lang;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class Result implements Serializable {
    @ApiModelProperty("状态码")
    private int code;
    @ApiModelProperty("提示信息")
    private String message;
    @ApiModelProperty("返回数据")
    private Object data;

    public static Result succeed(int code, String message, Object data) {
        return new Result(code, message, data);
    }

    public static Result succeed(Object data) {
        return succeed(201, "操作成功", data);
    }

    public static Result fail(int code, String message, Object data) {
        return new Result(code, message, data);
    }

    public static Result fail(String message, Object data) {
        return fail(400, message, data);
    }

    public static Result fail(String message) {
        return fail(400, message, null);
    }
}
