package monitor.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.api.ApplicationVariantImpl
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
        project.logger.error "================开始转换================"

        Set<File> classPath = new HashSet<>()
        Set<File> carePath  = new HashSet<>()
        //TODO 获取配置参数
        /**
         * 遍历输入文件，用于初始化相应参数
         */
        inputs.each { TransformInput input ->
            // 遍历目录
            input.directoryInputs.each { DirectoryInput directoryInput ->
                classPath.add(directoryInput.file)
                carePath.add(directoryInput.file)
            }
            //遍历jar
            input.jarInputs.each { JarInput jarInput ->
                classPath.add(jarInput.file)
            }
        }
        ApplicationVariantImpl applicationVariant = project.android.applicationVariants.getAt(0);
        classPath.addAll(applicationVariant.androidBuilder.computeFullBootClasspath())
        //进行代码注入
        Injector.setClassPathForJavassist(classPath)
        carePath.each {File file ->
            Injector.injectDir(project, file)
        }

        /**
         * 再次遍历输入文件，进行input到output的拷贝，包括修改后的代码
         */
        inputs.each { TransformInput input ->
            // 遍历目录
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //获得产物的目标目录
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
                // 将input文件拷到目标文件
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
                //将input文件拷到目标文件
                FileUtils.copyFile(jarInput.file, dest);
            }
        }

    }

    //TODO 代码注入：
    //TODO 1.可配置代码筛选规则。2.可配置代码注入方式。3.可配置代码注入内容。4.日志
}