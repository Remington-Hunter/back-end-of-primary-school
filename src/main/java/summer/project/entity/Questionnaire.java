package summer.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
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
@ApiModel
public class Questionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    @TableField("`description`")
    private String description;

    private LocalDateTime createTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @TableField("`using`")
    private Integer using;

    private Integer preparing;

    private String url;

    private Integer stopping;

    private Integer deleted;

    private Integer canSee;

    @TableField("`limit`")
    private Long limit;

    private Long answerNum;

    private Long needNum;

    @TableField("`type`")
    private Integer type;

    @Version // 这个注解是关键
    private Integer version;

    private Integer disorder;


    public Questionnaire(Long userId,
                         String title,
                         String description,
                         LocalDateTime now,
                         LocalDateTime startTime,
                         LocalDateTime endTime,
                         Long needNum,
                         Long limit,
                         Integer type,
                         Integer canSee,
                         Integer disorder) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.createTime = now;
        this.startTime = startTime;
        this.endTime = endTime;
        this.needNum = needNum;
        this.limit = limit;
        this.type = type;
        this.canSee = canSee;
        this.disorder = disorder;
    }
}
