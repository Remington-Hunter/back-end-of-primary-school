package summer.project.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import summer.project.entity.Option;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class QuestionDto implements Serializable {

    Long id;

    @NotBlank(message = "题目不能为空")
    @Size(max = 254, message = "题目不超过250个字")
    String content;

    Long point;

    @NotNull(message = "请提供题目类型")
    Integer type;

    List<OptionDto> optionList;

    private String answer;

    @NotNull(message = "请提供题号")
    private Long number;

    @NotNull(message = "题目是否必填")
    private Integer required;
}
