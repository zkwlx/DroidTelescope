package monitor.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.io.Files
import monitor.plugin.config.InjectConfig
import monitor.plugin.config.Scope
import monitor.plugin.utils.Logger
import org.gradle.api.Project

import java.util.function.Consumer

/**
 * Created by ZhouKeWen on 17/3/17.
 */
class InjectTransform extends IncrementalTransform {

    private static final String NAME = "injectPlugin"

    private Project project
    private static Set<QualifiedContent.Scope> careScopes
    private static List<File> javassistClassPath
    private static List<File> toBeInjectFiles
    private long timestamp
    private boolean isDisable


    private List<String> mExcludePackages = ["andr.perf.monitor"]
    private List<String> mExcludeClasses = []
    private List<String> mIncludePackages = []

    // 添加构造，为了方便从plugin中拿到project对象，待会有用
    InjectTransform(Project project) {
        this.project = project
        javassistClassPath = new LinkedList<>()
        toBeInjectFiles = new LinkedList<>()
        careScopes = new HashSet<>()
    }

    // Transfrom在Task列表中的名字
    // TransfromClassesWithPreDexForXXXX
    @Override
    String getName() {
        return NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    void onTransformStart(TransformInvocation invocation) {
        timestamp = System.currentTimeMillis()
        def config = ConfigProvider.config
        boolean isDebug = isDebug(invocation.context)
        isDisable = ((isDebug && !config.debugEnabled) || (!isDebug && !config.releaseEnabled))
        if (isDisable) {
            //TODO 这里有个 disable 功能，重构时注意啊！！！！！！！！！！！！！！！！！！
            Logger.i("================DroidTelescope disabled!================")
        } else {
            Logger.i("================开始注入监控代码（新版！）================")
            setFilter(config)
            setCareScope(config.getScope())
        }
    }

    @Override
    void onEachDirectory(DirectoryInput dirInput) {
        // 最大化 javassist 的 classpool
        javassistClassPath.add(dirInput.file)
    }

    @Override
    void onRealTransformFile(DirectoryInput dirInput, File inputFile, File outputFile) {
        Logger.d("FILE ready inject:--->" + outputFile.absolutePath)
        //复制 inputFile 到 outputFile，并将 outputFile 添加进处理列表
        Files.createParentDirs(outputFile)
        FileUtils.copyFile(inputFile, outputFile)
        if (careScopes.containsAll(dirInput.scopes)) {
            toBeInjectFiles.add(outputFile)
        }
    }

    @Override
    void onEachJar(JarInput jarInput) {
        // 最大化 javassist 的 classpool
        javassistClassPath.add(jarInput.file)
    }

    @Override
    void onRealTransformJar(JarInput jarInput, File outJarFile) {
        Logger.d("JAR ready inject:-->" + jarInput.name)
        //将输入的 jar 包复制到输出文件
        Files.createParentDirs(outJarFile)
        FileUtils.copyFile(jarInput.file, outJarFile)
        // 对相应 jar 包做处理
        if (careScopes.containsAll(jarInput.scopes)) {
            toBeInjectFiles.add(outJarFile)
        }
    }

    @Override
    void onTransformEnd(TransformInvocation invocation) {
        ApplicationVariantImpl applicationVariant = project.android.applicationVariants.getAt(0);
        javassistClassPath.addAll(applicationVariant.androidBuilder.computeFullBootClasspath())
        if (!isDisable) {
            //进行代码注入
            Injector.setClassPathForJavassist(javassistClassPath)
            Injector.setPackagesConfig(mExcludePackages, mIncludePackages, mExcludeClasses)
            toBeInjectFiles.parallelStream().forEach(new Consumer<File>() {
                @Override
                void accept(File file) {
                    Logger.i(">>>>>>>>>>>>inject!!!")
                    Injector.inject(file)
                }
            })
//            toBeInjectFiles.each { File file ->
//                Injector.inject(file)
//            }
        }
        int duration = System.currentTimeMillis() - timestamp
        Logger.i("-------InjectPluginTransform finish in ${duration} ms-------")
    }

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

    static boolean isDebug(Context context) {
        String path = context.getPath()
        String debug = "Debug"
        return path != null && debug.equalsIgnoreCase(path.substring(path.length() - debug.length(), path.length()))
    }
}