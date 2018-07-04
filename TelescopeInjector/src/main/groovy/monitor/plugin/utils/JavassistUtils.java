package monitor.plugin.utils;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * @author zhoukewen
 * @since 2018/7/4
 */
public class JavassistUtils {

    /**
     * 获取 ctClass 的超类，如果没有超类或直接继承{@link java.lang.Object}则返回 {@link CtClass.voidType}.
     * 如果超类无法解析或抛出 {@link javassist.NotFoundException}，则返回 null
     *
     * @param ctClass
     * @return 如果没有超类或直接继承{@link java.lang.Object}则返回 {@link CtClass.voidType}.
     * 如果超类无法解析或抛出 {@link javassist.NotFoundException}，则返回 null
     */
    public static CtClass getSuperclass(CtClass ctClass) {
        try {
            CtClass superClazz = ctClass.getSuperclass();
            if (superClazz == null) {
                Logger.i("Super class is java.lang.Object!!! class:" + ctClass.getName());
                //说明父类是 java.lang.Object，用 CtClass.voidType 替代
                return CtClass.voidType;
            } else {
                return superClazz;
            }
        } catch (NotFoundException e) {
//            e.printStackTrace();
            //不要崩溃直接跳过
            Logger.i("Super class not found!!!! Exception msg:" + e.getMessage() + ", class:" + ctClass.getName());
        }
        return null;
    }

    public static CtClass[] getBehaviorParameterTypes(CtBehavior ctBehavior) {
        CtClass[] types = null;
        try {
            types = ctBehavior.getParameterTypes();
        } catch (NotFoundException e) {
            String exMessage = e.getMessage();
            String className = ctBehavior.getDeclaringClass().getName();
            String methodName = ctBehavior.getName();
            Logger.i("Parameter class not found!!!! Exception msg:" + exMessage
                    + ", Method signature:" + className
                    + "." + methodName);
        }
        return types;
    }

}
