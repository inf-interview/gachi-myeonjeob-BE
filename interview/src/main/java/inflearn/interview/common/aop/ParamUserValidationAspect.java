package inflearn.interview.common.aop;

import inflearn.interview.user.infrastructure.UserEntity;
import inflearn.interview.common.exception.UserValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
public class ParamUserValidationAspect {

    @Pointcut(value = "@annotation(inflearn.interview.common.aop.ValidateUserParam) && (args(.., userId) || args(userId, ..))")
    public void validateUserPointcut(Long userId) {
    }

    @Before(value = "validateUserPointcut(userId)", argNames = "userId")
    public void validate(Long userId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity getUserEntity = (UserEntity) authentication.getPrincipal();

        if (userId == null || !userId.equals(getUserEntity.getUserId())) {
            log.info("Param 유저 검증 실패, User={}, Path={}", getUserEntity.getName(), request.getRequestURI());
            throw new UserValidationException("유저 검증에 실패하였습니다.", request.getRequestURI());
        }

    }
}