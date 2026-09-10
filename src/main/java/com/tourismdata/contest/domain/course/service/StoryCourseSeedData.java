package com.tourismdata.contest.domain.course.service;

import java.util.List;

import com.tourismdata.contest.domain.course.entity.Difficulty;

// 기획 스토리라인(병자호란 47일 항전, 도깨비 기억 회복 서사) 기준으로 확정된 5개 코스의
// 실제 체크포인트 데이터. 좌표는 OpenStreetMap(Nominatim/Overpass) 조회 및 구글맵 Plus Code
// 디코딩으로 확보한 실좌표이며, 지어낸 값이 아니다 (6번 암문=서암문, 제3남옹성은 팀 확인 후
// 구글맵 Plus Code를 직접 디코딩해서 확보).
//
// guideContent는 일반 모드에서 노출되는 장소별 짧은 역사 정보 카드용 텍스트다.
// (도깨비 서사 등 스토리 모드 콘텐츠는 Story 도메인의 story_events.content가 따로 담당)
//
// imageUrl은 TourAPI 원본 이미지가 실제 체크포인트와 매칭되지 않아 비워둠 - 콘텐츠/디자인
// 담당자가 실사진으로 채워야 한다.
final class StoryCourseSeedData {

    private StoryCourseSeedData() {
    }

    record CheckpointSeed(String name, double latitude, double longitude, String guideContent) {
    }

    record CourseSeed(String title, String description, Difficulty difficulty, List<CheckpointSeed> checkpoints) {
    }

    // 코스끼리 겹치는 물리적 장소(수어장대/서문/남장대터/지수당)는 좌표·안내문을 그대로 재사용하되,
    // 코스별로 별도의 Checkpoint row로 중복 생성한다 (사용자 확정 사항).
    private static final String GUIDE_NAMMUN =
            "남한산성의 정문이자 4대문 중 하나로, 병자호란 당시 인조가 이 문을 통해 산성으로 들어왔다고 전해진다.";
    private static final String GUIDE_SEOAMMUN =
            "성벽 곳곳에 뚫린 16개의 암문 가운데 하나로, 적의 눈을 피해 병사와 물자가 드나들던 통로였다.";
    private static final String GUIDE_SUEOJANGDAE =
            "남한산성에 남아있는 유일한 장대(지휘소)로, 병자호란 당시 이곳에서 군사를 지휘했다.";
    private static final String GUIDE_SEOMUN =
            "남한산성 4대문 중 하나로, 성의 서쪽을 지키던 관문.";
    private static final String GUIDE_BUKMUN =
            "남한산성 4대문 중 하나로, 성의 북쪽을 지키던 관문.";
    private static final String GUIDE_HAENGGUNG =
            "병자호란 당시 인조가 47일간 머물며 항전을 지휘했던 임시 궁궐.";
    private static final String GUIDE_GUKCHEONGSA =
            "병자호란 당시 승군(의승군)이 머물며 산성을 지켰던 사찰.";
    private static final String GUIDE_SUNGNYEOLJEON =
            "백제의 시조 온조왕을 모신 사당으로, 병자호란 당시 활약한 인물이 함께 배향되어 있다.";
    private static final String GUIDE_JANGGYEONGSA =
            "남한산성 축성 당시부터 승군이 주둔했던 사찰 중 하나.";
    private static final String GUIDE_DONGJANGDAE =
            "산성의 동쪽을 지키던 장대(지휘소)가 있던 자리.";
    private static final String GUIDE_BEOLBONG =
            "산성 동쪽 바깥의 봉우리로, 병자호란 당시 청군이 이 봉우리에 대포를 배치해 성 안을 공격했다고 전해진다.";
    private static final String GUIDE_NAMJANGDAE =
            "산성의 남쪽을 지키던 장대(지휘소)가 있던 자리.";
    private static final String GUIDE_JE3NAMONGSEONG =
            "남장대 부근에 축조된 방어 시설(옹성) 중 하나로, 성벽 바깥으로 돌출시켜 적을 여러 방향에서 공격할 수 있게 만들었다.";
    private static final String GUIDE_JISUDANG =
            "조선시대에 조성된 연못과 정자.";
    private static final String GUIDE_DONGMUN =
            "남한산성 4대문 중 하나로, 성의 동쪽을 지키던 관문.";

    static final List<CourseSeed> COURSES = List.of(
            new CourseSeed(
                    "1코스 · 수장의 길",
                    "남한산성을 지키던 수장의 발자취를 따라 남문에서 북문까지 성곽 요충지를 걷는 코스.",
                    Difficulty.HARD,
                    List.of(
                            new CheckpointSeed("남문(지화문)", 37.4733372, 127.1811677, GUIDE_NAMMUN),
                            new CheckpointSeed("6번 암문(서암문)", 37.474563, 127.179938, GUIDE_SEOAMMUN),
                            new CheckpointSeed("수어장대", 37.4798450, 127.1766014, GUIDE_SUEOJANGDAE),
                            new CheckpointSeed("서문(우익문)", 37.4846475, 127.1760407, GUIDE_SEOMUN),
                            new CheckpointSeed("북문(전승문)", 37.4815816, 127.1847914, GUIDE_BUKMUN)
                    )
            ),
            new CourseSeed(
                    "2코스 · 승려의 길",
                    "병자호란 당시 성을 지켰던 승군의 흔적을 따라 행궁에서 숭렬전까지 걷는 코스.",
                    Difficulty.EASY,
                    List.of(
                            new CheckpointSeed("행궁", 37.4778445, 127.1828485, GUIDE_HAENGGUNG),
                            new CheckpointSeed("국청사", 37.4839626, 127.1770903, GUIDE_GUKCHEONGSA),
                            new CheckpointSeed("숭렬전", 37.4813313, 127.1802837, GUIDE_SUNGNYEOLJEON)
                    )
            ),
            new CourseSeed(
                    "3코스 · 승병장의 길",
                    "장경사에서 벌봉까지, 산성 동쪽을 지키던 승병장의 흔적을 따라 걷는 코스.",
                    Difficulty.HARD,
                    List.of(
                            new CheckpointSeed("장경사", 37.4753157, 127.1976126, GUIDE_JANGGYEONGSA),
                            new CheckpointSeed("동장대터", 37.4796437, 127.1984781, GUIDE_DONGJANGDAE),
                            new CheckpointSeed("벌봉", 37.4828565, 127.2026412, GUIDE_BEOLBONG)
                    )
            ),
            new CourseSeed(
                    "4코스 · 무관의 길",
                    "남장대에서 지수당까지, 산성을 지키던 무관(장수)의 발자취를 따라 걷는 코스.",
                    Difficulty.NORMAL,
                    List.of(
                            new CheckpointSeed("남장대터", 37.4718123, 127.1851225, GUIDE_NAMJANGDAE),
                            new CheckpointSeed("제3남옹성", 37.471313, 127.189438, GUIDE_JE3NAMONGSEONG),
                            new CheckpointSeed("지수당", 37.4758905, 127.1897516, GUIDE_JISUDANG)
                    )
            ),
            new CourseSeed(
                    "5코스 · 인조의 길",
                    "지수당에서 동문까지, 47일간 성을 지키려 했던 왕 인조의 발자취를 따라 걷는 코스.",
                    Difficulty.HARD,
                    List.of(
                            new CheckpointSeed("지수당", 37.4758905, 127.1897516, GUIDE_JISUDANG),
                            new CheckpointSeed("남장대터", 37.4718123, 127.1851225, GUIDE_NAMJANGDAE),
                            new CheckpointSeed("수어장대", 37.4798450, 127.1766014, GUIDE_SUEOJANGDAE),
                            new CheckpointSeed("서문(우익문)", 37.4846475, 127.1760407, GUIDE_SEOMUN),
                            new CheckpointSeed("동문(좌익문)", 37.4742674, 127.1941035, GUIDE_DONGMUN)
                    )
            )
    );
}
