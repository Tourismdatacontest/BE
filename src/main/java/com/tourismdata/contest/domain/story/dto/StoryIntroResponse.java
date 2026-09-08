package com.tourismdata.contest.domain.story.dto;

// ⚠️ ERD에 별도 "스토리 인트로" 테이블이 없어, 코스(Course)의 title/description을
// 인트로 제목/내용으로 대신 사용함. 병자호란 서사 전용 인트로 콘텐츠가 필요하면
// 나중에 전용 테이블/필드로 분리해야 함.
public record StoryIntroResponse(
    Long visitId,
    String title,
    String content
) {
}
