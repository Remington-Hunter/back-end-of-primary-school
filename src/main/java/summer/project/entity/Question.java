package summer.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.Version;
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
@NoArgsConstructor
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long questionnaire;

    private String content;

    /**
     * 0-普通单选
     * 1-普通多选
     * 2-普通填空
     * 3-评分单选
     * 4-评分多选
     * 5-评分填空
     * 6-报名单选
     * 7-报名多选
     * 8-下拉题
     * 9-评分题
     * 10-投票单选
     * 11-投票多选
     */
    private Integer type;

    private Long point;

    private String answer;

    private Long number;

    private String comment;

    private Integer required;

    @Version // 这个注解是关键
    private Integer version;

    public Question(String content, String answer, Long point, Integer type, Long number, Integer required, String comment) {
        this.content = content;
        this.answer = answer;
        this.point = point;
        this.type = type;
        this.number = number;
        this.required = required;
        this.comment = comment;
    }

    public Question(Long questionnaireId, String content, String answer, Long point, Integer type, Long number, Integer required, String comment) {
        this.questionnaire = questionnaireId;
        this.content = content;
        this.answer = answer;
        this.point = point;
        this.type = type;
        this.number = number;
        this.required = required;
        this.comment = comment;
    }
}
