package com.tourismdata.contest.domain.story.dto;

// title: 방문 중인 코스 제목(어느 코스인지 안내용).
// content: 스토리라인 문서 기준 모든 코스 공통 프롤로그(빛에 휩싸여 조선시대로 떨어짐 ->
// 기억 잃은 도깨비 등장 -> 노인이 "전설의 백숙을 완성하라" 지시). 코스마다 달라지지 않음
// - StoryService.COMMON_PROLOGUE 참고.
public record StoryIntroResponse(
    Long visitId,
    String title,
    String content
) {
}
