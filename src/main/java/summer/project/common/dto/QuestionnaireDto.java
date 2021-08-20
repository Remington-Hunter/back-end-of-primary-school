package summer.project.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class QuestionnaireDto {
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long limit;

    private Integer column13;

    private Long answerNum;

    private Long needNum;

    List<QuestionDto> questionList;
}
