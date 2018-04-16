package org.smart4j.framework;

import org.smart4j.framework.helper.*;
import org.smart4j.framework.util.ClassUtil;
import top.smart4j.framework.helper.*;

/**
 * 加载相应的 Helper 类
 *
 * @author wang yi zhe
 * @since 1.0.0
 */

public final class HelpLoader {
    public static void init(){
        Class<?>[] classList ={
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class,
                UploadHelper.class,
                ServletHelper.class,
                RequestHelper.class,
                ConfigHelper.class,
                DatabaseHelper.class
        };
        for (Class<?> cls : classList){
            ClassUtil.loadClass(cls.getName(),false);
        }
    }
}
