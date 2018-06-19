package monitor.plugin.javassist.inject

import javassist.CtBehavior
import javassist.CtClass
import javassist.Modifier
import monitor.plugin.utils.Logger

/**
 * 卡顿监控模块的代码注入器
 * <p>Created by ZhouKeWen on 17-4-3.</p>
 */
class CpuCodeInject {

    static void insertCpuSampleCode(CtClass clazz, CtBehavior ctBehavior) {
        Logger.d("inject Cpu sample code:::>>>> ${clazz.name}.${ctBehavior.name}")
        if (ctBehavior.isEmpty() || Modifier.isNative(ctBehavior.getModifiers())) {
            return;
        }

        ctBehavior.addLocalVariable("__cpu_switch", CtClass.booleanType);
        ctBehavior.insertBefore(
                """
                  __cpu_switch = andr.perf.monitor.injected.TimeConsumingSample.shouldMonitor();
                  if(__cpu_switch) {
                      andr.perf.monitor.injected.TimeConsumingSample.methodEnter("${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                  }
                """)
        //只在正常return之前插入代码，如果是异常退出，不会回调methodExit
        ctBehavior.insertAfter(
                """
                   if(__cpu_switch) {
                       andr.perf.monitor.injected.TimeConsumingSample.methodExit("${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                   }
                """)
        //方法异常退出会导致方法堆栈记录混乱，被迫注入finally代码
        ctBehavior.insertAfter(
                """
                   andr.perf.monitor.injected.TimeConsumingSample.methodExitFinally("${
                    clazz.name
                }", "${
                    ctBehavior.name
                }", "${generateParamTypes(ctBehavior.parameterTypes)}");
                """, true)
    }

    private static String generateParamTypes(CtClass[] paramTypes) {
        StringBuilder argTypesBuilder = new StringBuilder();
        if (paramTypes.length > 0) {
            CtClass firstParam = paramTypes[0]
            argTypesBuilder.append(firstParam.name);
            for (int i = 1; i < paramTypes.length; i++) {
                argTypesBuilder.append(",")
                CtClass paramCls = paramTypes[i]
                argTypesBuilder.append(paramCls.name)
            }
        }
        return argTypesBuilder.toString()
    }

}
