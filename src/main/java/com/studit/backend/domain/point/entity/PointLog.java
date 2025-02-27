package com.studit.backend.domain.point.entity;
import com.studit.backend.domain.point.dto.PointLogRequest;
import com.studit.backend.domain.point.PointLogType;
import com.studit.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class PointLog {
    @Id //포인트 로그
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointLogId; //포인트 로그 ID
    private Long userId; //회원 ID
    private Long studyId; //스터디룸 ID
    private Long paymentId; //결제 ID

    private Long changePoint;//변동 포인트
    private Long totalWithdrawPoint;//총 출금 포인트
    private Long totalRewardPoint;//총 보상 포인트

    private Long totalDeductPoint;//총 차감 포인트
    private Long totalPoint;//최종 포인트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)//Enum을 문자열로 저장
    private PointLogType pointLogType;//포인트 로그 종류

    public void changePointForm(PointLogRequest pointLogRequest) {//포인트 로그 수정 형식
        this.userId = pointLogRequest.getUserId();
        this.studyId = pointLogRequest.getStudyId();
        this.paymentId = pointLogRequest.getPaymentId();
        this.changePoint = pointLogRequest.getChangePoint();
        this.pointLogType = pointLogRequest.getPointLogType();}}