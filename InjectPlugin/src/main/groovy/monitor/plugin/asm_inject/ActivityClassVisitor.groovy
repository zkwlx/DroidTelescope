package monitor.plugin.asm_inject

import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
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
        isActivitySubClass = "Activity".equals(superName)
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        printLog("visitMethod name: ${name}, desc: ${desc}")
        if (isActivitySubClass) {

        }
        return super.visitMethod(access, name, desc, signature, exceptions)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
    }

    private void printLog(String content) {
        System.out.println(content)
    }
}
