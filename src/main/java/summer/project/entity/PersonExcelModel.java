package summer.project.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author jerryzhao
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PersonExcelModel  extends BaseRowModel implements Serializable {

    @ExcelProperty(value = "姓名", index = 0)
    private String name;

    @ExcelProperty(value = "学号", index = 1)
    private String stuId;
}

