package monitor.plugin.asm_inject

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * Created by ZhouKeWen on 17/3/17.
 */
class AsmHandler {

    public static byte[] handleClass(File file) {
        def optClass = new File(file.getParent(), file.name + ".opt")

        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(optClass)

        def bytes = injectMonitorCode(inputStream);
        outputStream.write(bytes)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        optClass.renameTo(file)
        return bytes
    }

    private static byte[] injectMonitorCode(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new ActivityClassVisitor(cw)
//        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
//            @Override
//            public MethodVisitor visitMethod(int access, String name, String desc,
//                    String signature, String[] exceptions) {
//
//                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//                mv = new MethodVisitor(Opcodes.ASM4, mv) {
//                    @Override
//                    void visitInsn(int opcode) {
//                        if ("<init>".equals(name) && opcode == Opcodes.RETURN) {
//                            super.visitLdcInsn(Type.getType("Lcn/jiajixin/nuwa/Hack;"));
//                        }
//                        super.visitInsn(opcode);
//                    }
//                }
//                return mv;
//            }
//
//        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

}
