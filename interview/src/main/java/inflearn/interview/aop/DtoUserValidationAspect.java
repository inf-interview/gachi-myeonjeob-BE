package inflearn.interview.aop;

import inflearn.interview.domain.User;
import inflearn.interview.domain.dto.BaseDTO;
import inflearn.interview.exception.UserValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class DtoUserValidationAspect {

    @Pointcut("@annotation(ValidateUser) && (args(dto, ..) || args(.., dto))")
    public void validateUserPointcut(BaseDTO dto) {}

    @Before(value = "validateUserPointcut(dto)", argNames = "joinPoint,dto")
    public void validateUserBefore(JoinPoint joinPoint, BaseDTO dto) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (dto.getUserId() == null || !user.getUserId().equals(dto.getUserId())) {
            log.info("DTO 유저 검증 실패, User={}, Path={}", user.getName(), request.getRequestURI());
            throw new UserValidationException("유저 검증에 실패하였습니다.", request.getRequestURI());
        }


    }

}
