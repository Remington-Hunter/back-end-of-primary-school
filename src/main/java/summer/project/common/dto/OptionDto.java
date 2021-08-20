package summer.project.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import summer.project.entity.Option;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class OptionDto implements Serializable {

    @NotBlank(message = "题目不能为空")
    @Size(max = 254, message = "题目不超过250个字")
    String content;

    private Long id;

    private Long limit;

    private String number;

}