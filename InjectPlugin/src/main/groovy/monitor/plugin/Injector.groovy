package monitor.plugin

import monitor.plugin.javassist_inject.JavassistHandler
import org.gradle.api.Project

/**
 * Created by ZhouKeWen on 17/3/17.
 */
public class Injector {

    public static void setClassPathForJavassist(Set<File> files) {
        JavassistHandler.setClassPath(files)
    }

    public static void injectDir(Project project, File dirFile) {

        if (dirFile.isDirectory()) {
            dirFile.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('R.class') &&
                        !filePath.contains("BuildConfig.class")) {
                    project.logger.error "======>>>>name::> ${filePath}"
                    JavassistHandler.handleClass(file)
                }
            }
        }
    }

    public static void injectJar(Project project, String jarPath) {
        if (jarPath.endsWith(".jar")) {

        }
    }

}