package summer.project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import summer.project.entity.Person;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jerryzhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonListDto {
    @NotNull(message = "对应的问题id不能为空")
    Long questionnaireId;

    List<Person> personList;
}
