package monitor.plugin.asm_inject;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by ZhouKeWen on 17/3/20.
 */
public class CpuInjectAdapter extends AdviceAdapter {

    private String methodName

    private String tempM = "gogo"

    protected CpuInjectAdapter(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(Opcodes.ASM4, methodVisitor, access, name, desc);
        methodName = name;
    }

    @Override
    protected void onMethodEnter() {
        if (tempM == methodName) {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            //            super.visitInsn(Opcodes.POP2)
            super.visitVarInsn(Opcodes.LSTORE, 1);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        printLog("MethodVisitor>>>>> opcode: ${opcode}")
        // (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW ||
        if (tempM == methodName && (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode ==
                Opcodes.ATHROW) {
            super.visitVarInsn(Opcodes.LLOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "andr/perf/monitor/CpuMonitor", "startMethodMonitor",
                                  "(J)V", false);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (tempM == methodName) {
            super.visitMaxs(maxStack, maxLocals + 2);
        } else {
            super.visitMaxs(maxStack, maxLocals);
        }
    }

    private void printLog(String content) {
        System.out.println(content)
    }

}
