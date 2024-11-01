package icu.nanshuo.model.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.service.UserService;
import icu.nanshuo.utils.SpringBeanContextUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 通用字段自动处理程序（创建人和更新人）
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Component
public class CommonFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        UserService userService = SpringBeanContextUtils.getBeanByClass(UserService.class);
        User user = userService.getLoginUser(request);
        this.setFieldValByName("createBy", user.getId(), metaObject);
        this.setFieldValByName("updateBy", user.getId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        UserService userService = SpringBeanContextUtils.getBeanByClass(UserService.class);
        User user = userService.getLoginUser(request);
        this.setFieldValByName("updateBy", user.getId(), metaObject);
    }
}
