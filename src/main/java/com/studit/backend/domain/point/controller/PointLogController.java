package com.studit.backend.domain.point.controller;
import com.studit.backend.domain.point.dto.PointLogRequest;
import com.studit.backend.domain.point.dto.PointLogResponse;
import com.studit.backend.domain.point.service.PointLogService;
import com.studit.backend.domain.point.PointLogType;
import com.studit.backend.domain.point.entity.PointLog;
import com.studit.backend.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.LogSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pointLog")
@RequiredArgsConstructor
public class PointLogController {//포인트 로그
    private final PointLogService pointLogService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")//임시 포인트 로그 생성
    public ResponseEntity<PointLogResponse> createPointLog
            (@RequestHeader("Authroization") String token, @RequestBody PointLogRequest pointLogRequest) {

        //JWT 에서 userId 추출
        Long userId=jwtTokenProvider.getUserIdFromToken(token);
        PointLogResponse pointLogResponse=pointLogService.createPointLog(userId, pointLogRequest);

        return ResponseEntity.ok().body(pointLogResponse);}

    @GetMapping("/get/{pointLogId}")//포인트 로그 조회
    public ResponseEntity<PointLog>getPointLog(@PathVariable Long pointLogId){
        PointLog pointLog=pointLogService.getPointLog(pointLogId);
        return ResponseEntity.ok(pointLog);}

    @GetMapping//한 번에 여러 개 로그 조회(기본 10개씩)
    public ResponseEntity<List<PointLog>> getPointLogs(@RequestParam Long userId,
                                                       @RequestParam(required = false, defaultValue = "0") Long cursor,
                                                       @RequestParam(defaultValue = "10") int pageSize){
        List<PointLog> pointLogs=pointLogService.getPointLogs
                (userId,cursor,pageSize);return ResponseEntity.ok(pointLogs);}

    @GetMapping("/getAll") public List<PointLog> getAll(){//모든 포인트 로그 조회
        List<PointLog> pointLog=pointLogService.getAll();
        pointLog.forEach(System.out::println);return pointLog;}

    @GetMapping("/getEach") public List<PointLog> getEach(@RequestParam
                                                                  (required = false) PointLogType pointLogType){//기능에 해당되는 해당 포인트 로그들 조회
        List<PointLog> pointLogs=pointLogService.getEach(pointLogType);
        pointLogs.forEach(System.out::println);return pointLogs;}

    @PutMapping("/charge/{pointLogId}")//충전
    public ResponseEntity<PointLogResponse> charge (@PathVariable Long pointLogId,
                                                    @RequestBody PointLogRequest pointLogRequest) {
        PointLogResponse pointLogResponse=pointLogService.charge(pointLogId,pointLogRequest);
        return ResponseEntity.ok().body(pointLogResponse);}

    @PutMapping("/withdraw/{pointLogId}")//출금
    public ResponseEntity<PointLogResponse> withdraw(@PathVariable Long pointLogId,
                                      @RequestBody PointLogRequest pointLogRequest) {
        PointLogResponse pointLogResponse = pointLogService.withdraw(pointLogId, pointLogRequest);
        return ResponseEntity.ok().body(pointLogResponse);}

    @PutMapping("/refund/{pointLogId}")//환불
    public ResponseEntity<?> refund(@PathVariable Long pointLogId,
                                    @RequestBody PointLogRequest pointLogRequest) {
        PointLogResponse pointLogResponse = pointLogService.refund(pointLogId, pointLogRequest);
        return ResponseEntity.ok().body(pointLogResponse);}

    @PutMapping("/deduct/{pointLogId}")//차감
    public ResponseEntity<?> deduct(@PathVariable Long pointLogId,
                                    @RequestBody PointLogRequest pointLogRequest) {
        PointLogResponse pointLogResponse = pointLogService.deduct(pointLogId, pointLogRequest);
        return ResponseEntity.ok().body(pointLogResponse);}

    @PutMapping("/reward/{pointLogId}")//보상
    public ResponseEntity<?> reward(@PathVariable Long pointLogId,
                                    @RequestHeader("Authorization") String token,
                                    @Validated @RequestBody PointLogRequest pointLogRequest) {
        PointLogResponse pointLogResponse = pointLogService.reward(pointLogId, pointLogRequest);
        return ResponseEntity.ok().body(pointLogResponse);}}

/* @PutMapping("/charge/{pointLogId}")//충전
    public ResponseEntity<?> charge(@PathVariable Long pointLogId,
    @RequestHeader("Authorization") String token,
    @Validated @RequestBody PointLogRequest pointLogRequest) {
        Long userId=jwtTokenProvider.getUserIdFromToken(token);
        PointLog pointLog = pointLogService.charge(pointLogId, pointLogRequest, userId);

        pointLogRequest.setTotalPoint(pointLogRequest.getTotalPoint()+pointLogRequest.getChangePoint());
        pointLogRequest.setTotalWithdrawPoint(pointLogRequest
                .getTotalWithdrawPoint()+pointLogRequest.getChangePoint());
        return ResponseEntity.status(HttpStatus.CREATED).body(pointLog);}
*/