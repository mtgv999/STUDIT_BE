package com.studit.backend.domain.point.dto;
import com.studit.backend.domain.point.PointLogType;
import com.studit.backend.domain.point.entity.PointLog;
import lombok.Getter;

@Getter
public class PointLogResponse {
    Long studyId;
    Long paymentId;

    Long changePoint;//변동 포인트
    Long totalWithdrawPoint;//총 출금 포인트
    Long totalRewardPoint;//총 보상 포인트

    Long totalDeductPoint;//총 차감 포인트
    Long totalPoint;//총 포인트
    PointLogType pointLogType;//포인트 로그 종류

    public PointLogResponse(PointLog pointLog){
        this.studyId =pointLog.getStudyId();
        this.paymentId=pointLog.getPaymentId();

        this.changePoint=pointLog.getChangePoint();
        this.totalWithdrawPoint=pointLog.getTotalWithdrawPoint();
        this.totalRewardPoint=pointLog.getTotalRewardPoint();

        this.totalDeductPoint=pointLog.getTotalDeductPoint();
        this.totalPoint=pointLog.getTotalPoint();
        this.pointLogType=pointLog.getPointLogType();}}