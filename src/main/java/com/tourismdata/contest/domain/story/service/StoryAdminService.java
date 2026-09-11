package com.tourismdata.contest.domain.story.service;

import com.tourismdata.contest.domain.course.entity.Checkpoint;
import com.tourismdata.contest.domain.course.entity.Course;
import com.tourismdata.contest.domain.course.repository.CheckpointRepository;
import com.tourismdata.contest.domain.course.repository.CourseRepository;
import com.tourismdata.contest.domain.story.dto.StoryEventResponse;
import com.tourismdata.contest.domain.story.entity.Ingredient;
import com.tourismdata.contest.domain.story.entity.StoryEvent;
import com.tourismdata.contest.domain.story.repository.IngredientRepository;
import com.tourismdata.contest.domain.story.repository.StoryEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 확정된 스토리라인 문서 기준으로 story_events + 재료를 심는 운영 도구.
 * A의 seed-story-courses가 코스/체크포인트를 먼저 심어둔 뒤에 호출해야 함
 * (courseTitle로 코스를 찾고, 체크포인트 순서(order_no)와 콘텐츠 순서를 1:1 매칭).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StoryAdminService {

    private final CourseRepository courseRepository;
    private final CheckpointRepository checkpointRepository;
    private final StoryEventRepository storyEventRepository;
    private final IngredientRepository ingredientRepository;

    public List<StoryEventResponse> seedVerifiedCourseContent() {
        List<StoryEventResponse> result = new ArrayList<>();

        for (StoryContentSeedData.CourseContentSeed courseSeed : StoryContentSeedData.COURSES) {
            Course course = courseRepository.findByTitle(courseSeed.courseTitle())
                .orElseThrow(() -> new IllegalStateException(
                    "코스를 찾을 수 없습니다 - 먼저 /admin/courses/seed-story-courses 실행 필요: "
                        + courseSeed.courseTitle()));

            List<Checkpoint> checkpoints = checkpointRepository
                .findByCourse_CourseIdOrderByOrderNoAsc(course.getCourseId());

            if (checkpoints.size() != courseSeed.checkpoints().size()) {
                throw new IllegalStateException(
                    "체크포인트 개수가 스토리 콘텐츠와 안 맞습니다. course=" + courseSeed.courseTitle()
                        + " DB=" + checkpoints.size() + " 콘텐츠=" + courseSeed.checkpoints().size());
            }

            for (int i = 0; i < checkpoints.size(); i++) {
                Checkpoint checkpoint = checkpoints.get(i);
                StoryContentSeedData.CheckpointContentSeed seed = courseSeed.checkpoints().get(i);

                List<Ingredient> ingredients = seed.ingredientNames().stream()
                    .map(this::findOrCreateIngredient)
                    .toList();

                StoryEvent event = StoryEvent.builder()
                    .checkpoint(checkpoint)
                    .content(seed.content())
                    .ingredients(ingredients)
                    .build();

                result.add(StoryEventResponse.from(storyEventRepository.save(event)));
            }
        }

        return result;
    }

    private Ingredient findOrCreateIngredient(String name) {
        return ingredientRepository.findByName(name)
            .orElseGet(() -> ingredientRepository.save(
                Ingredient.builder().name(name).imageUrl(null).build()
            ));
    }
}
