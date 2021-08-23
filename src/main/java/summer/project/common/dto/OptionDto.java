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

    @Size(max = 254, message = "选项不超过250个字")
    String content;

    private Long id;

    private Long limit;

    private String number;

    private String comment;

}
