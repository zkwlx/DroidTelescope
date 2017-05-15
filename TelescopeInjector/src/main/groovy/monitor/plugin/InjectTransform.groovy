package monitor.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.pipeline.TransformManager
import monitor.plugin.config.InjectConfig
import monitor.plugin.config.Scope
import monitor.plugin.utils.LogUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.internal.hash.HashUtil

import javax.inject.Inject

/**
 * Created by ZhouKeWen on 17/3/17.
 */
public class InjectTransform extends Transform {

    private static final String NAME = "injectPlugin"

    private Project project
    private InjectConfig config;
    private final Set<QualifiedContent.Scope> careScopes = new HashSet<>();

    private List<String> mExcludePackages = ["andr.perf.monitor"];
    private List<String> mExcludeClasses = [];
    private List<String> mIncludePackages = [];
    private List<String> mIncludeClasses = ["android.support.v7.app.AppCompatViewInflater\$DeclaredOnClickListener"];

    // 添加构造，为了方便从plugin中拿到project对象，待会有用
    public InjectTransform(Project project, InjectConfig config) {
        this.project = project
        this.config = config
    }

    // Transfrom在Task列表中的名字
    // TransfromClassesWithPreDexForXXXX
    @Override
    String getName() {
        return NAME
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

        boolean isDebug = isDebug(context)
        if ((isDebug && !config.debugEnabled)
                || (!isDebug && !config.releaseEnabled)) {
            careScopes.clear();
            LogUtils.printLog("block canary ex disabled")
        } else {
            setFilter(config)
            setCareScope(config.getScope())
        }

        project.logger.error "================开始注入监控代码================"

        Set<File> javassistClassPath = new HashSet<>()
        Set<File> careFiles = new HashSet<>()

        //遍历输入文件，初始化相应参数并复制目标文件
        inputs.each { TransformInput input ->
            // 遍历目录
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //获得产物的目标目录
                File dest = outputProvider.
                        getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes,
                                           Format.DIRECTORY);
                javassistClassPath.add(directoryInput.file)
                if (careScopes.containsAll(directoryInput.scopes)) {
                    careFiles.add(dest)
                }
                // 将input文件拷到目标文件
                FileUtils.copyDirectory(directoryInput.file, dest);
            }

            //遍历jar
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.name;
                // 重名名输出文件,因为可能同名,会覆盖
                def hexName = HashUtil.createHash(jarInput.file, "MD5").asHexString();
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4);
                }
                // 获得输出文件
                File dest = outputProvider.
                        getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes,
                                           Format.JAR);
                javassistClassPath.add(jarInput.file)
                if (careScopes.containsAll(jarInput.scopes)) {
                    careFiles.add(dest)
                }
                //将input文件拷到目标文件
                FileUtils.copyFile(jarInput.file, dest);
            }
        }
        ApplicationVariantImpl applicationVariant = project.android.applicationVariants.getAt(0);
        javassistClassPath.addAll(applicationVariant.androidBuilder.computeFullBootClasspath())
        //进行代码注入
        Injector.setClassPathForJavassist(javassistClassPath)
        Injector.setPackagesConfig(mExcludePackages, mIncludePackages, mExcludeClasses, mIncludeClasses)
        careFiles.each { File file ->
            Injector.inject(project, file)
        }

    }

    //TODO 代码注入：
    //TODO 1.可配置代码筛选规则。2.可配置代码注入方式。3.可配置代码注入内容。4.日志

    void setFilter(InjectConfig config) {
        mExcludePackages.addAll(config.excludePackages)
        mIncludePackages.addAll(config.includePackages)
        mExcludeClasses.addAll(config.excludeClasses)
    }

    void setCareScope(Scope scope) {
        careScopes.clear()
        if (scope.project) {
            careScopes.add(QualifiedContent.Scope.PROJECT)
        }
        if (scope.projectLocalDep) {
            careScopes.add(QualifiedContent.Scope.PROJECT_LOCAL_DEPS)
        }
        if (scope.subProject) {
            careScopes.add(QualifiedContent.Scope.SUB_PROJECTS)
        }
        if (scope.subProjectLocalDep) {
            careScopes.add(QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
        }
        if (scope.externalLibraries) {
            careScopes.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        }
    }

    boolean isDebug(Context context) {
        String path = context.getPath()
        String debug = "Debug"
        return path != null && debug.equalsIgnoreCase(path.substring(path.length() - debug.length(), path.length()))
    }
}