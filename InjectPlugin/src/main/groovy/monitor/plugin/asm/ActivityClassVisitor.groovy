package monitor.plugin.asm

import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Created by ZhouKeWen on 17/3/17.
 */
class ActivityClassVisitor extends ClassVisitor {

    private boolean isActivitySubClass = false;

    public ActivityClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM4, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        printLog("visit version: ${version}, name: ${name}, superName: ${superName}")
        isActivitySubClass = "android/support/v7/app/AppCompatActivity".equals(superName)
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        if (mv != null) {
            printLog("visitMethod name: ${name}, desc: ${desc}, signature: ${signature}")
            mv = new CpuInjectVisitor(mv, name)
        }
        return mv
    }

    @Override
    void visitEnd() {
        super.visitEnd()
    }

    private void printLog(String content) {
        System.out.println(content)
    }
}
