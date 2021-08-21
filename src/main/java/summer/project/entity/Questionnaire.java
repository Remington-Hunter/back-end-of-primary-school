package summer.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
public class Questionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer using;

    private Integer preparing;

    private String url;

    private Integer stopping;

    @TableField("`limit`")
    private Long limit;

    private Long answerNum;

    private Long needNum;


    public Questionnaire(Long userId,
                         String title,
                         String description,
                         LocalDateTime now,
                         LocalDateTime startTime,
                         LocalDateTime endTime,
                         Long answerNum,
                         Long needNum,
                         Long limit) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.createTime = now;
        this.startTime = startTime;
        this.endTime = endTime;
        this.answerNum = answerNum;
        this.needNum = needNum;
        this.limit = limit;
    }
}
