package summer.project.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel
public class QuestionnaireDto {
    private Long id;

    @NotNull(message = "用户名ID不能为空")
    private Long userId;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long limit;

    private Long needNum;

    List<QuestionDto> questionList;

    private Integer canSee;

    @NotNull(message = "问卷类型不能为空")
    private Integer type;

    private Integer disorder;
}
