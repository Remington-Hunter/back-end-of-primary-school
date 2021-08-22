package summer.project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerListDto {

    @NotNull(message = "问卷id不能为空")
    Long questionnaireId;

    List<AnswerDto> answerDtoList;


}
