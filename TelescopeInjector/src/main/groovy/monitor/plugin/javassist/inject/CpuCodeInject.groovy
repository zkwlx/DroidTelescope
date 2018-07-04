package monitor.plugin.javassist.inject

import javassist.CannotCompileException
import javassist.CtBehavior
import javassist.CtClass
import javassist.Modifier
import monitor.plugin.utils.JavassistUtils
import monitor.plugin.utils.Logger

/**
 * 卡顿监控模块的代码注入器
 * <p>Created by ZhouKeWen on 17-4-3.</p>
 */
class CpuCodeInject {

    static void insertCpuSampleCode(CtClass clazz, CtBehavior ctBehavior) {
        Logger.d("inject Cpu sample code:::>>>> ${clazz.name}.${ctBehavior.name}")
        if (ctBehavior.isEmpty() || Modifier.isNative(ctBehavior.getModifiers())) {
            return
        }
        CtClass[] types = JavassistUtils.getBehaviorParameterTypes(ctBehavior)
        if (types == null) {
            return
        }
        String paramSignature = generateParamSignature(types)
        try {
            ctBehavior.insertBefore(
                    """
                  andr.perf.monitor.injected.TimeConsumingSample.methodEnter("${
                        clazz.name
                    }", "${
                        ctBehavior.name
                    }", "${paramSignature}");
                """)
        } catch (CannotCompileException e) {
            Logger.i("Cpu code inject error:${e.getMessage()}, method:${clazz.name}.${ctBehavior.name}")
            return
            //insertBefore 出错说明方法字节码有问题（比如返回值类型 NotFound），直接退出
            //如果 insertBefore 没出错而后面出错了，则抛出异常，避免出现前后不一致的情况
        }
        //只在正常return之前插入代码，如果是异常退出，不会回调methodExit
        ctBehavior.insertAfter(
                """
                   andr.perf.monitor.injected.TimeConsumingSample.methodExit("${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${paramSignature}");
                """)
        //方法异常退出会导致方法堆栈记录混乱，被迫注入finally代码
        ctBehavior.insertAfter(
                """
                   andr.perf.monitor.injected.TimeConsumingSample.methodExitFinally("${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${paramSignature}");
                """, true)
    }

    private static String generateParamSignature(CtClass[] paramTypes) {
        StringBuilder argTypesBuilder = new StringBuilder()
        if (paramTypes.length > 0) {
            CtClass firstParam = paramTypes[0]
            argTypesBuilder.append(firstParam.name)
            for (int i = 1; i < paramTypes.length; i++) {
                argTypesBuilder.append(",")
                CtClass paramCls = paramTypes[i]
                argTypesBuilder.append(paramCls.name)
            }
        }
        return argTypesBuilder.toString()
    }

}
