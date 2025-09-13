package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.entity.*;
import com.finalproject.springbackend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/db")
@RequiredArgsConstructor
public class GetRecordController {

    private final SystemLevelFalseService slfService;
    private final Certified2TimeService c2tService;
    private final CertifiedNotMoveService cnmService;
    private final ResourceLevelFalseService rlfService;
    private final ResourceLevelFalse2Service rlf2Service;

    /**
     * 테이블 레코드 전체를 가져오는 엔드포인트 ( /<레코드>/all )
     */
    //system-level-false 테이블의 레코드 전체를 가져오는 클래스
//    @GetMapping("/system_level_false/all")
//    public ResponseEntity<List<SystemLevelFalse>> getAllSLF() {
//        List<SystemLevelFalse> slf = slfService.getAll();
//        return ResponseEntity.ok(slf);
//    }

    //certified-2-time 테이블의 레코드 전체를 가져오는 클래스
    @GetMapping("/certified_2_time/all")
    public ResponseEntity<List<Certified2Time>> getAllC2T() {
        List<Certified2Time> c2t = c2tService.getAll();
        return ResponseEntity.ok(c2t);
    }

    //certified-not-move 테이블의 레코드 전체를 가져오는 클래스
    @GetMapping("/certified_not_move/all")
    public ResponseEntity<List<CertifiedNotMove>> getAllCNM() {
        List<CertifiedNotMove> cnm = cnmService.getAll();

        return ResponseEntity.ok(cnm);
    }

//    //resource_level_false 테이블 레코드 전체를 가져오는 클래스
//    @GetMapping("/resource_level_false/all")
//    public ResponseEntity<List<ResourceLevelFalse>> getAllRLF() {
//        List<ResourceLevelFalse> rlf = rlfService.getAll();
//
//        return ResponseEntity.ok(rlf);
//    }


    /**
     * 시간 기준 resource-level-false 테이블의 레코드 반환
     * http://localhost:8080/api/db/resource_level_false?start=2025-09-12T00:12:43Z&end=2025-09-12T12:34:56Z로
     * GET 요청을 보내면 start과 end 사이의 레코드 반환
     * start 데이터 형식: yyyy-mm-ddThh:mm:ssZ
     * end 데이터 형식: yyyy-mm-ddThh:mm:ssZ
     *  yyyy-mm-dd 뒤에 T, hh:mm:ss 뒤에 Z를 붙여야 함
     */

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimes(
            @RequestParam OffsetDateTime start,
            @RequestParam OffsetDateTime end
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service
                .getTimeStampWithUnAuthAccess(start, end);

        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "principal"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPrincipal(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPrincipal(
                start, end, principal
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "resourceName"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndResourceName(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String resourceName
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndResourceName(
                start, end, resourceName
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param operation     어떤 권한으로 접근했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "operation"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndOperation(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String operation
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndOperation(
                start, end, operation
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndClientIp(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndClientIp(
                start, end, clientIp
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "principal", "resourceName"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPR(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal, @RequestParam String resourceName
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPR(
                start, end, principal, resourceName
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @param operation     어떤 권한으로 접근했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "principal", "operation"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPO(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal, @RequestParam String operation
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPO(
                start, end, principal, operation
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "principal", "clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPC(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal, @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPC(
                start, end, principal, clientIp
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @param operation     어떤 권한으로 접근했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "resourceName", "operation"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndRO(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String resourceName, @RequestParam String operation
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndRO(
                start, end, resourceName, operation
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "resourceName", "clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndRC(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String resourceName, @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndRC(
                start, end, resourceName, clientIp
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param operation     어떤 권한으로 접근했는지
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "operation", "clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndOC(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String operation, @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndOC(
                start, end, operation, clientIp
        );
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @param operation     어떤 권한으로 접근했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false", params = {"start", "end", "principal", "resourceName", "operation"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPRO(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal,
            @RequestParam String resourceName,
            @RequestParam String operation
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPRO(
                start, end, principal, resourceName, operation);
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false",
            params = {"start", "end", "principal", "resourceName", "clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPRC(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal,
            @RequestParam String resourceName,
            @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPRC(
                start, end, principal, resourceName, clientIp);
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @param operation     어떤 권한으로 접근했는지
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false",
                params = {"start", "end", "principal", "operation", "clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPOC(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal,
            @RequestParam String operation,
            @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPOC(
                start, end, principal, operation, clientIp);
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @param operation     어떤 권한으로 접근했는지
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false",
                params = {"start", "end", "resourceName", "operation", "clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndROC(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String resourceName,
            @RequestParam String operation,
            @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndROC(
                start, end, resourceName, operation, clientIp);
        return ResponseEntity.ok(rlf);
    }

    /**
     * @param start         시작 시각
     * @param end           종료 시각 (null 가능: 서비스에서 현재 시간으로 보정해줌)
     * @param principal     principal(검색 할 유저)값
     * @param resourceName  resourceName(비인가 접근이 생긴 리소스 이름)
     * @param operation     어떤 권한으로 접근했는지
     * @param clientIp      어떤 IP로 비인가 접근을 했는지
     * @return ResponseEntity<List<ResourceLevelFalse2>>
     *                      200 OK - 결과 리스트(없으면 빈 리스트),
     *                      400 Bad Request - 파라미터 검증 실패
     */
    @GetMapping(value = "/resource_level_false",
                    params = {"start", "end",
                            "principal", "resourceName","operation","clientIp"})
    public ResponseEntity<List<ResourceLevelFalse2>> getTimeAndPROC(
            @RequestParam OffsetDateTime start, @RequestParam OffsetDateTime end,
            @RequestParam String principal,
            @RequestParam String resourceName,
            @RequestParam String operation,
            @RequestParam String clientIp
    ){
        List<ResourceLevelFalse2> rlf = rlf2Service.getTimeAndPROC(
                start, end, principal, resourceName, operation, clientIp);
        return ResponseEntity.ok(rlf);
    }



}
