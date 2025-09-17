package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.entity.Certified2Time;
import com.finalproject.springbackend.repository.projection.AlertTypeCount;
import com.finalproject.springbackend.repository.projection.IpCount;
import com.finalproject.springbackend.service.Certified2TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/db/certified_2_time")
@RequiredArgsConstructor
public class GetCertified2TimeController {

    private final Certified2TimeService c2tService;

    @GetMapping
    public ResponseEntity<List<Certified2Time>> getAll(){
        return ResponseEntity.ok(c2tService.getAll());
    }

    @GetMapping(value = "/count")
    public ResponseEntity<Long> getAllCount(){
        return ResponseEntity.ok(c2tService.getAllCount());
    }

    @GetMapping(params = {"client_ip"})
    public ResponseEntity<List<Certified2Time>> getOnlyClientIp(
            @RequestParam(value = "client_ip") String clientIp
    ) {
        List<Certified2Time> c2tList = c2tService.getOnlyClientIp(clientIp);
        return ResponseEntity.ok(c2tList);
    }

    @GetMapping(value = "/count", params = {"client_ip"})
    public ResponseEntity<Long> getOnlyClientIpCount(@RequestParam(value = "client_ip") String clientIp){
        Long count = c2tService.getOnlyClientIpCount(clientIp);
        return ResponseEntity.ok(count);
    }

    @GetMapping(params = {"alert_type"})
    public ResponseEntity<List<Certified2Time>> getOnlyAlertType(@RequestParam(value = "alert_type") String alertType){
        List<Certified2Time> c2tList = c2tService.getOnlyAlertType(alertType);
        return ResponseEntity.ok(c2tList);
    }

    @GetMapping(value = "/count", params = {"alert_type"})
    public ResponseEntity<Long> getOnlyAlertTypeCount(@RequestParam(value = "alert_type") String alertType){
        Long cnt = c2tService.getOnlyAlertTypeCount(alertType);
        return ResponseEntity.ok(cnt);
    }

    @GetMapping(params = {"start"})
    public ResponseEntity<List<Certified2Time>> getTimeOnly(
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end
    ){
        List<Certified2Time> c2tList = c2tService.getTimeOnly(start, end);
        return ResponseEntity.ok(c2tList);
    }

    @GetMapping(value = "/count", params = {"start"})
    public ResponseEntity<Long> getTimeOnlyCount(
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end
    ){
        Long cnt = c2tService.getTimeOnlyCount(start, end);
        return ResponseEntity.ok(cnt);
    }

    @GetMapping(params = {"start", "client_ip"})
    public ResponseEntity<List<Certified2Time>> getC(
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end,
            @RequestParam(value = "client_ip") String clientIp
    ){
        List<Certified2Time> c2tList = c2tService.getC(start, end, clientIp);
        return ResponseEntity.ok(c2tList);
    }

    @GetMapping(value = "/count", params = {"start", "client_ip"})
    public ResponseEntity<Long> getCCount(
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end,
            @RequestParam(value = "client_ip") String clientIp
    ){
        Long cnt = c2tService.getCCount(start, end,clientIp);
        return ResponseEntity.ok(cnt);
    }

    @GetMapping(params = {"start", "alert_type"})
    public ResponseEntity<List<Certified2Time>> getA(
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end,
            @RequestParam(value = "alert_type") String alertType
    ){
        List<Certified2Time> c2tList = c2tService.getA(start, end, alertType);
        return ResponseEntity.ok(c2tList);
    }

    @GetMapping(value = "/count", params = {"start", "alert_type"})
    public ResponseEntity<Long> getACount(
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end,
            @RequestParam(value = "alert_type") String alertType
    ){
        Long cnt = c2tService.getACount(start, end, alertType);
        return ResponseEntity.ok(cnt);
    }

    @GetMapping(value = "/count/group/client_ip", params = {"start"})
    public ResponseEntity<List<IpCount>> getIpCount(
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end
    ){
        List<IpCount> c2tIpCountList = c2tService.getIpCount(start, end);
        return ResponseEntity.ok(c2tIpCountList);
    }

    @GetMapping(value = "/count/group/client_ip")
    public ResponseEntity<List<IpCount>> getIpCountAll(){
        List<IpCount> c2tIpCountList = c2tService.getIpCountAll();
        return ResponseEntity.ok(c2tIpCountList);
    }

    @GetMapping(value = "/count/group/alert_type", params = {"start"})
    public ResponseEntity<List<AlertTypeCount>> getAlertTypeCount (
            @RequestParam(value = "start") OffsetDateTime start,
            @RequestParam(value = "end", required = false) OffsetDateTime end
    ) {
        List<AlertTypeCount> c2tATCountList = c2tService.getAlertTypeCount(start, end);
        return ResponseEntity.ok(c2tATCountList);
    }

    @GetMapping(value="/count/group/alert_type")
    public ResponseEntity<List<AlertTypeCount>> getAlterTypeCountAll(){
        List<AlertTypeCount> c2tAlterTypeCountList = c2tService.getAlertTypeCountAll();
        return ResponseEntity.ok(c2tAlterTypeCountList);
    }

}
