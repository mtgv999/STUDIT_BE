package com.studit.backend.domain.point.service;
import com.studit.backend.domain.point.PointLogType;
import com.studit.backend.domain.point.dto.PointLogRequest;
import com.studit.backend.domain.point.dto.PointLogResponse;
import com.studit.backend.domain.point.entity.PointLog;
import com.studit.backend.domain.point.repository.PointLogRepository;
import com.studit.backend.domain.room.entity.StudyRoom;
import com.studit.backend.domain.room.repository.StudyRoomRepository;
import com.studit.backend.domain.user.entity.User;
import com.studit.backend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PointLogService {//포인트 로그
    private final PointLogRepository pointLogRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;

    /* public PointLog createPointLog(PointLogRequest pointLogRequest) {//임시 포인트 로그 생성
        return pointLogRepository.save(PointLogRequest.pointLogForm(pointLogRequest));}*/

    public PointLogResponse createPointLog(Long userId, PointLogRequest pointLogRequest) {//임시 포인트 로그 생성
        User user=userRepository.findById(userId).orElse(null);
        if(user==null){return null;}
        PointLog pointLog=pointLogRepository.save(pointLogRequest.pointLogForm(user));
        return new PointLogResponse(pointLog);}

    public PointLog getPointLog(Long pointLogId) {
        return pointLogRepository.findById(pointLogId).orElse(null);}

    public List<PointLog> getPointLogs(Long userId, Long cursor, int pageSize){
        Pageable pageable= PageRequest.of(0, pageSize);//최대 조회 개수 제한
        return pointLogRepository.findNextPointLogs(userId, cursor, pageable);}

    public List<PointLog> getAll() {return pointLogRepository.findAll();}

    public List<PointLog> getEach(PointLogType pointLogType) {
        if(pointLogType!=null){return pointLogRepository.findByPointLogType(pointLogType);}
        return pointLogRepository.findAll();}//없을 시 모든 결과 조회

    public PointLogResponse charge(Long pointLogId,PointLogRequest pointLogRequest) {//충전
        PointLog saved=pointLogRepository.findById(pointLogId)
                .orElseThrow(()->new RuntimeException("포인트 로그 없음"));

        long charge=pointLogRequest.getChangePoint();
        saved.setTotalPoint(saved.getTotalPoint()+charge);
        saved.changePointForm(pointLogRequest);
        return new PointLogResponse(pointLogRepository.save(saved));}

    public PointLogResponse withdraw(Long pointLogId, PointLogRequest pointLogRequest) {//출금
        //포인트 로그 찾기
        PointLog saved=pointLogRepository.findById(pointLogId)
                .orElseThrow(()->new RuntimeException("포인트 로그 없음"));
        //출금값 가져오기
        long withdraw=pointLogRequest.getChangePoint();
        //출금 처리
        saved.setTotalPoint(saved.getTotalPoint()-withdraw);
        saved.setTotalWithdrawPoint(saved.getTotalWithdrawPoint()+withdraw);
        saved.changePointForm(pointLogRequest);
        //포인트 로그 저장
        return new PointLogResponse(pointLogRepository.save(saved));}

    public PointLogResponse refund(Long pointLogId, PointLogRequest pointLogRequest) {//환불
        PointLog saved=pointLogRepository.findById(pointLogId)
                .orElseThrow(()->new RuntimeException("포인트 로그 없음"));

        long refund=pointLogRequest.getChangePoint();
        saved.setTotalPoint(saved.getTotalPoint()+refund);
        saved.changePointForm(pointLogRequest);
        return new PointLogResponse(pointLogRepository.save(saved));}

    public PointLogResponse deduct(Long pointLogId,PointLogRequest pointLogRequest) {//차감
        PointLog saved=pointLogRepository.findById(pointLogId)
                .orElseThrow(()->new RuntimeException("포인트 로그 없음"));

        long deduct=pointLogRequest.getChangePoint();
        saved.setTotalPoint(saved.getTotalPoint()-deduct);
        saved.setTotalDeductPoint(saved.getTotalDeductPoint()+deduct);
        saved.changePointForm(pointLogRequest);
        return new PointLogResponse(pointLogRepository.save(saved));}

    public PointLogResponse reward(Long pointLogId, PointLogRequest pointLogRequest) {
        PointLog saved=pointLogRepository.findById(pointLogId)
                .orElseThrow(()->new RuntimeException("포인트 로그 없음"));

        long reward=pointLogRequest.getChangePoint();
        saved.setTotalPoint(saved.getTotalPoint()+reward);
        saved.setTotalRewardPoint(saved.getTotalRewardPoint()+reward);
        saved.changePointForm(pointLogRequest);
        return new PointLogResponse(pointLogRepository.save(saved));}}

/* public PointLogResponse charge(PointLogRequest pointLogRequest, Long userId) {//충전
        PointLog saved=pointLogRepository.findById(pointLogId)
                .orElseThrow(()->new RuntimeException("포인트 로그 없음"));

        long charge=pointLogRequest.getChangePoint();
        saved.setTotalPoint(saved.getTotalPoint()+charge);
        saved.changePointForm(pointLogRequest);
        return pointLogRepository.save(saved);}

    public PointLog withdraw(Long pointLogId,PointLogRequest pointLogRequest, Long userId) {//출금
        //포인트 로그 찾기
        PointLog saved=pointLogRepository.findById(pointLogId)
                .orElseThrow(()->new RuntimeException("포인트 로그 없음"));

        //출금값 가져오기
        long withdraw=pointLogRequest.getChangePoint();

        //출금 처리
        saved.setTotalPoint(saved.getTotalPoint()-withdraw);
        saved.setTotalWithdrawPoint(saved.getTotalWithdrawPoint()+withdraw);
        saved.changePointForm(pointLogRequest);

        //포인트 로그 저장
        return pointLogRepository.save(saved);}*/