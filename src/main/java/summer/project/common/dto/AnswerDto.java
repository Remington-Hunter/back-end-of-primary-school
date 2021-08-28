package summer.project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author jerryzhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    @NotNull(message = "对应的问题id不能为空")
    Long questionId;

    String content;

    String number;
}
