package com.tourismdata.contest.domain.story.service;

import java.util.List;

// 확정된 스토리라인 문서 기준 체크포인트별 스토리 콘텐츠.
// ⚠️ 1코스만 A의 시딩 데이터(StoryCourseSeedData)와 체크포인트 구성이 일치함이 확인됨.
// 2~5코스는 체크포인트 개수/구성이 스토리라인 문서와 달라서 기획 확인 전까지 비워둠.
final class StoryContentSeedData {

    private StoryContentSeedData() {
    }

    record CheckpointContentSeed(String checkpointName, String content, List<String> ingredientNames) {
    }

    record CourseContentSeed(String courseTitle, List<CheckpointContentSeed> checkpoints) {
    }

    static final List<CourseContentSeed> COURSES = List.of(
        new CourseContentSeed(
            "1코스 · 수장의 길",
            List.of(
                new CheckpointContentSeed(
                    "남문(지화문)",
                    """
                    병사: "적군이다! 성문을 닫아라!"
                    도깨비: "모두 위치를 지켜라! 우리는 이 성을 지켜야 한다!"
                    도깨비: "응?… 내가 방금 뭐라고 한 거지?"
                    도깨비: (머리를 감싸며) "으윽… 무언가 떠오를 것 같아"
                    사용자: "… 그냥 노는 도깨비는 아니었나봐"
                    """,
                    List.of()
                ),
                new CheckpointContentSeed(
                    "6번 암문(서암문)",
                    """
                    사용자: "응? 저게 뭐지?"
                    마늘 한 줌을 획득했습니다!
                    """,
                    List.of("마늘")
                ),
                new CheckpointContentSeed(
                    "수어장대",
                    """
                    사용자: "여기 닭이 있어! 여긴 뭐 하는 곳이지?"
                    닭을 획득했습니다!
                    도깨비: "군사를 지휘하고 성 안팎을 살피는 곳이야."
                    사용자: "너 그걸 어떻게 알아?"
                    도깨비: "… 그러게?"
                    """,
                    List.of("닭")
                ),
                new CheckpointContentSeed(
                    "서문(우익문)",
                    """
                    도깨비: "이상하다… 여기 서 있으니깐 마음이 편해."
                    사용자: "와본 적 있는 거 아냐?"
                    도깨비: "글쎄… 근데 이 문을 지나가는 사람들을 바라봤던 기억이 나는 것 같아."
                    사용자: "앗 저기 대파랑 양파가 있다!!"
                    대파와 양파를 획득했습니다!
                    """,
                    List.of("대파", "양파")
                ),
                new CheckpointContentSeed(
                    "북문(전승문)",
                    """
                    도깨비: "잠깐… 또 머리가…"
                    병사1: "수장님! 명을 내려주십시오!"
                    병사2: "적군이 가까워지고 있습니다!"
                    도깨비: "수장?… 그래 이제 기억났어. 나는 이 성을 지키던 사람들을 지휘하고 있었어."
                    사용자: "그래서 남문에서도 병사들에게 명령을 내렸던 거구나."
                    도깨비: "응, 수어장대도, 성문도… 내가 알고 있던 곳이었어. 잊고 있었지만, 난 이 성을 지키고 있었어."
                    """,
                    List.of()
                )
            )
        )
    );
}
