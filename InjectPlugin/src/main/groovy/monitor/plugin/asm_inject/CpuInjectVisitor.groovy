package monitor.plugin.asm_inject;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by ZhouKeWen on 17/3/20.
 */

public class CpuInjectVisitor extends MethodVisitor {

    private String methodName;

    private String tempM = "gogo"

    private boolean isFirstVisitFrame = true;

    public CpuInjectVisitor(MethodVisitor mv, String name) {
        super(Opcodes.ASM4, mv);
        methodName = name
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (tempM == methodName) {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            super.visitVarInsn(Opcodes.LSTORE, 1);
        }
    }

    @Override
    void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        printLog("type:> ${type}, nLocal:${nLocal}, nStack:${nStack}")
        if (tempM == methodName && isFirstVisitFrame) {
            //修改帧数据Frame，增加我们添加的long
            isFirstVisitFrame = false
            Object[] finalArray
            if (local == null) {
                finalArray = [Opcodes.LONG] as Object[]
            } else {
                List list = local.toList()
                list.add(0, Opcodes.LONG)
                finalArray = list as Object[]
            }
            super.visitFrame(Opcodes.F_APPEND, nLocal + 1, finalArray, nStack, stack)
        } else {
            super.visitFrame(type, nLocal, local, nStack, stack)
        }
    }

    @Override
    void visitVarInsn(int opcode, int var) {
        printLog("visitVarInsn=== opcode:${opcode}, operand:${var}")
        //如果opcode是ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE指令，则对参数处理
        //TODO 注意，这里也包括RET指令
        if (tempM == methodName && var > 0) {
            //var>0是因为我们只影响了索引>=1之后的局部变量表
            super.visitVarInsn(opcode, var + 2)
        } else {
            super.visitVarInsn(opcode, var)
        }
    }

    @Override
    public void visitInsn(int opcode) {
        printLog("visitInsn>>> opcode: ${opcode}")
        if (tempM == methodName && (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode ==
                Opcodes.ATHROW) {
            super.visitVarInsn(Opcodes.LLOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "andr/perf/monitor/CpuMonitor", "startMethodMonitor",
                                  "(J)V", false);
        }
        super.visitInsn(opcode);
    }

    private void printLog(String content) {
        System.out.println(content)
    }

}