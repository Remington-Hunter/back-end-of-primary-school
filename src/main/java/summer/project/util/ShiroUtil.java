package summer.project.util;

import org.apache.shiro.SecurityUtils;
import summer.project.shiro.AccountProfile;

public class ShiroUtil {
    public static AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

}
