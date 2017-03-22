package monitor.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by ZhouKeWen on 17/3/17.
 */
public class PreDexTransform extends Transform {

    Project project
    // 添加构造，为了方便从plugin中拿到project对象，待会有用
    public PreDexTransform(Project project) {
        this.project = project
    }

    // Transfrom在Task列表中的名字
    // TransfromClassesWithPreDexForXXXX
    @Override
    String getName() {
        return "preDex"
    }

    // 指定input的类型
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transfrom的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        project.logger.error "================自定义插件成功！=========="
        // inputs就是输入文件的集合
        // outputProvider可以获取outputs的路径
//        inputs.each { TransformInput input ->
//            input.directoryInputs.each { DirectoryInput directoryInput ->
//                //TODO 这里可以对input的文件做处理，比如代码注入！
//                // 获取output目录
//                def dest = outputProvider.getContentLocation(directoryInput.name,
//                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
//                // 将input的目录复制到output指定目录
//                FileUtils.copyDirectory(directoryInput.file, dest)
//            }
//
//            input.jarInputs.each { JarInput jarInput ->
//                //TODO 这里可以对input的文件做处理，比如代码注入！
//                // 重命名输出文件（同目录copyFile会冲突）
//                def jarName = jarInput.name
//                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
//                if (jarName.endsWith(".jar")) {
//                    jarName = jarName.substring(0, jarName.length() - 4)
//                }
//                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
//                FileUtils.copyFile(jarInput.file, dest)
//            }
//
//        }

        //TODO 获取配置参数

        /**
         * 遍历输入文件
         */
        inputs.each { TransformInput input ->
            // 遍历目录
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //获得产物的目录
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
                //TODO 这里进行我们的处理
//                project.logger.error "Copying ${directoryInput.name} to ${dest.absolutePath}"
                Injector.injectDir(project, directoryInput.file.absolutePath)
                // 处理完后拷到目标文件
                FileUtils.copyDirectory(directoryInput.file, dest);
            }

            //遍历jar
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.name;
                // 重名名输出文件,因为可能同名,会覆盖
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath);
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4);
                }
                // 获得输出文件
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR);

                //TODO 处理jar进行字节码注入处理

                FileUtils.copyFile(jarInput.file, dest);
//                project.logger.error "Copying ${jarInput.file.absolutePath} to ${dest.absolutePath}"
            }
        }
    }

    //TODO 代码注入：
    //TODO 1.可配置代码筛选规则。2.可配置代码注入方式。3.可配置代码注入内容。4.日志
}