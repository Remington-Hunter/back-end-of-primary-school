package summer.project.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import summer.project.common.lang.Result;

@Slf4j // 日志输出
@RestControllerAdvice // 异步全局异常处理
public class GlobalExceptionHandler {

//    java.lang.IllegalArgumentException
//    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 无权限
//    @ExceptionHandler(value = ShiroException.class)
//    public Result handler(ShiroException e) {
//        log.error("无权限异常：----------------{}", e);
//        return Result.fail(401, e.getMessage(), null);
//    }

    @ResponseStatus(HttpStatus.ACCEPTED) // 无权限
    @ExceptionHandler(value = ShiroException.class)
    public Result handler(ShiroException e) {
        log.error("无权限异常：----------------{}", e);
        return Result.fail(401, e.getMessage(), null);
    }


    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e) {
        log.error("运行时异常：----------------{}", e);
        return Result.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        log.error("实体校验异常：----------------{}", e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();

        return Result.fail(objectError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler(value = BindException.class)
    public Result handler(BindException e ) {
        log.error("实体校验2异常：----------------{}", e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();

        return Result.fail(objectError.getDefaultMessage());
    }



    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler(value = UnauthorizedException.class)
    public Result handler(UnauthorizedException e ) {
        log.error("未授权异常：----------------{}", e);

        return Result.fail("您无权限进行此操作，原因是未授权。");
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler(value = UnauthenticatedException.class)
    public Result handler(UnauthenticatedException e ) {
        log.error("未认证异常：----------------{}", e);

        return Result.fail("您无权限进行此操作，原因是未认证。");
    }





}
