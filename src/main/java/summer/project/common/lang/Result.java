package summer.project.common.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {
    private int code;
    private String message;
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
