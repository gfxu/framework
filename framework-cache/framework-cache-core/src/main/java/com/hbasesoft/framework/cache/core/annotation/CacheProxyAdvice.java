package com.hbasesoft.framework.cache.core.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 *
 * @author hejiawen<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017/2/10 <br>
 * @see com.hbasesoft.framework.cache.core.annotation <br>
 * @since V1.0<br>
 */
@Component
public class CacheProxyAdvice implements BeanPostProcessor, ApplicationContextAware {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Method[] methods = bean.getClass().getMethods();

        for (Method method : methods) {
            String name = method.getName();
            if (name.length() > 3 && name.startsWith("set") && method.getParameterTypes().length == 1
                && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                try {
                    CacheProxy cacheProxy = method.getAnnotation(CacheProxy.class);
                    if (cacheProxy != null) {
                        Object value = CacheHelper.proxy(method.getParameterTypes()[0], cacheProxy);
                        if (value != null) {
                            method.invoke(bean, value);
                        }
                    }
                }
                catch (Throwable e) {
                    LoggerUtil.error("Failed to init proxy cache at method " + name + " in class "
                        + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
                }
            }
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                CacheProxy cacheProxy = field.getAnnotation(CacheProxy.class);

                if (cacheProxy != null) {

                    Object value = CacheHelper.proxy(field.getType(), cacheProxy);
                    if (value != null) {
                        field.set(bean, value);
                    }
                }
            }
            catch (Throwable e) {
                LoggerUtil.error("Failed to init proxy cache at filed " + field.getName() + " in class "
                    + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param applicationContext
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextHolder.setContext(applicationContext);
    }
}
