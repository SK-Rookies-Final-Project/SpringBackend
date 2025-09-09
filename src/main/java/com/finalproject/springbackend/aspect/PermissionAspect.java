package com.finalproject.springbackend.aspect;

import com.finalproject.springbackend.annotation.RequirePermission;
import com.finalproject.springbackend.dto.Permission;
import com.finalproject.springbackend.service.AuthService;
import com.finalproject.springbackend.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;
    private final AuthService authService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증되지 않은 사용자의 API 접근 시도");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"인증이 필요합니다\"}");
        }

        String username = authentication.getName();
        Permission[] requiredPermissions = requirePermission.value();

        // 사용자 권한 확인
        boolean hasPermission = permissionService.hasAnyPermission(username, requiredPermissions);
        
        if (!hasPermission) {
            log.warn("사용자 {}가 필요한 권한 없이 API 접근 시도: {}", username, requiredPermissions);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"접근 권한이 없습니다\", \"required_permissions\": " + 
                          java.util.Arrays.toString(requiredPermissions) + "}");
        }

        log.info("사용자 {}가 권한 확인 후 API 접근: {}", username, requiredPermissions);
        return joinPoint.proceed();
    }
}
