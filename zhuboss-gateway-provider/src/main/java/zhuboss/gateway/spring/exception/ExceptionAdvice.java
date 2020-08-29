package zhuboss.gateway.spring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import zhuboss.framework.exception.BussinessException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    public static boolean isAjaxRequest(WebRequest webRequest) {
        String requestedWith = webRequest.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
    }

    /**
     * 可应用于Controller或Service的参数Validate
     * @param e
     * @param request
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map validExceptionHandler(MethodArgumentNotValidException e, WebRequest request, HttpServletResponse response) throws IOException {
        log.error(e.getMessage());
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        StringBuilder sb = new StringBuilder("Validate fail! ");
        for (ObjectError objectError : allErrors) {
            if(objectError instanceof FieldError){
                sb.append(((FieldError)objectError).getField());
            }
            sb.append(objectError.getDefaultMessage() + ";");
        }
        log.error(sb.toString());
        Map map = new HashMap();
        map.put("result", false);
        map.put("msg", sb.toString());
        return map;
    }

    @ResponseBody
    @ExceptionHandler(BussinessException.class)
    public Map errorHandler(BussinessException ex) {
        log.error(ex.getMessage(),ex);
        Map map = new HashMap();
        map.put("result", false);
        map.put("msg", ex.getMessage());
        return map;
    }

    /**
     * 业务异常
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Map errorHandler(Exception ex) {
        log.error(ex.getMessage(),ex);
        Map map = new HashMap();
        map.put("result", false);
        map.put("msg", ex.getClass().getSimpleName()+":"+ex.getMessage());
        return map;
    }
}
