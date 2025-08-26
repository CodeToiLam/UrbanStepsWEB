package vn.urbansteps.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import vn.urbansteps.common.AdminAuditable;
import vn.urbansteps.service.AdminActionLogService;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminAuditAspect {
    private final AdminActionLogService logService;
    private final HttpServletRequest req;

    @Around("within(vn.urbansteps.controller.admin..*) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(vn.urbansteps.common.AdminAuditable))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object out = pjp.proceed();
        try {
            MethodSignature ms = (MethodSignature) pjp.getSignature();
            AdminAuditable ann = ms.getMethod().getAnnotation(AdminAuditable.class);
            String action = ann != null && !ann.value().isBlank()
                    ? ann.value()
                    : ms.getDeclaringType().getSimpleName() + "." + ms.getMethod().getName() + "()";

            String details = "method=" + req.getMethod() +
                    ", path=" + req.getRequestURI() +
                    (req.getQueryString() != null ? "?" + req.getQueryString() : "") +
                    ", ip=" + req.getRemoteAddr();

//            logService.logActionCurrent(action, details);
        } catch (Exception ignored) {}
        return out;
    }
}
