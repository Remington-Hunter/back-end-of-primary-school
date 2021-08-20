package summer.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
     * 6-报名多选
     * 7-下拉题
     */
    private Integer type;

    private Long point;


}
