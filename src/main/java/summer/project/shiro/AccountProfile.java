package summer.project.shiro;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccountProfile implements Serializable {
    private Long id;

    private String username;

    private String email;

    private String phone;

    private Integer isWriter;

    private Integer isVip;

    private Integer isEdit;

    private Integer isReviewer;

    private Integer status;

    private String description;

    private LocalDate birth;

    private Integer gender;

    private String avatar;

    private LocalDateTime lastLogin;

}
