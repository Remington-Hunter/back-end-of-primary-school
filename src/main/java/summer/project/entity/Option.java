package summer.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author JerryZhao
 * @since 2021-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`option`")
@NoArgsConstructor
@AllArgsConstructor
public class Option implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String content;

    private Long questionId;

    @TableField("`limit`")
    private Long limit;

    @TableField("`number`")
    private String number;

    private Long answerNum;

    private String comment;


    public Option(Long questionId, String content, Long limit, String number, String comment) {
        this.questionId = questionId;
        this.content = content;
        this.limit = limit;
        this.number = number;
    }

    public Option(String content, Long limit, String number, String comment) {
        this.content = content;
        this.limit = limit;
        this.number = number;
    }
}
