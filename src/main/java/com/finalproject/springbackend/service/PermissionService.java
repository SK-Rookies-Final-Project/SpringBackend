package com.finalproject.springbackend.service;

import com.finalproject.springbackend.dto.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    // 사용자별 권한 매핑
    private final Map<String, Set<Permission>> userPermissions = new HashMap<>();

    @PostConstruct
    public void initializeUserPermissions() {
        // 하드코딩된 사용자 권한 매핑 제거
        // 이제 모든 권한은 Kafka SCRAM 인증을 통과한 사용자에게 동적으로 부여됩니다.
        log.info("PermissionService 초기화 완료 - 하드코딩된 사용자 권한 제거됨");
    }

    public boolean hasPermission(String username, Permission permission) {
        // Kafka SCRAM 인증을 통과한 사용자는 기본적으로 모든 권한을 가집니다.
        // 실제 환경에서는 사용자별 세부 권한을 DB나 외부 시스템에서 조회해야 합니다.
        log.debug("권한 체크: {} - {} (Kafka SCRAM 인증 통과 사용자에게 모든 권한 허용)", username, permission);
        return true;
    }

    public boolean hasAnyPermission(String username, Permission... permissions) {
        for (Permission permission : permissions) {
            if (hasPermission(username, permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllPermissions(String username, Permission... permissions) {
        for (Permission permission : permissions) {
            if (!hasPermission(username, permission)) {
                return false;
            }
        }
        return true;
    }

    public Set<Permission> getUserPermissions(String username) {
        // Kafka SCRAM 인증을 통과한 사용자에게는 모든 권한을 부여
        return new HashSet<>(Arrays.asList(Permission.values()));
    }

    public List<String> getPermissionCodes(String username) {
        return getUserPermissions(username).stream()
                .map(Permission::getCode)
                .toList();
    }

    public boolean canAccessRegion(String username, String region) {
        // Kafka SCRAM 인증을 통과한 사용자는 모든 지역에 접근 가능
        log.debug("지역 접근 체크: {} - {} (Kafka SCRAM 인증 통과 사용자에게 모든 지역 접근 허용)", username, region);
        return true;
    }
}
