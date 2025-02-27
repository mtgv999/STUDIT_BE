package com.studit.backend.domain.point.dto;
import com.studit.backend.domain.point.PointLogType;
import com.studit.backend.domain.point.entity.PointLog;
import com.studit.backend.domain.user.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PointLogRequest {//포인트 로그
    private Long userId;
    private Long studyId;
    private Long paymentId;

    private Long changePoint;//변동 포인트
    private Long totalWithdrawPoint;//총 출금 포인트
    private Long totalRewardPoint;//총 보상 포인트

    private Long totalDeductPoint;//총 차감 포인트
    private Long totalPoint;//총 포인트
    private PointLogType pointLogType;//포인트 로그 종류

    public PointLog pointLogForm(User user){
        return PointLog.builder()
                .userId(this.userId)
                .studyId(this.studyId)
                .paymentId(this.paymentId)

                .changePoint(this.changePoint)
                .totalWithdrawPoint(this.totalWithdrawPoint)
                .totalRewardPoint(this.totalRewardPoint)

                .totalDeductPoint(this.totalDeductPoint)
                .totalPoint(this.totalPoint)
                .user(user)
                .pointLogType(this.pointLogType)
                .build();}}