package ync.likelion.moguri_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrowMoguriResponse {
    private String message; // 응답 메시지
    private String targetDifference; // 목표 몸무게와의 차이
    private boolean isEvolved; // 진화 여부
    private MoguriGrowDetailDto moguri; // 모구리 DTO

    // 필요한 경우, 생성자 및 기타 메서드 추가 가능
}
