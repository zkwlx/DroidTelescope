package monitor.plugin

import monitor.plugin.asm_inject.AsmHandler
import org.gradle.api.Project

/**
 * Created by ZhouKeWen on 17/3/17.
 */
public class Injector {

    public static void injectDir(Project project, String path) {

        File classDir = new File(path)
        if (classDir.isDirectory()) {
            classDir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('R.class') &&
                        !filePath.contains("BuildConfig.class")) {
                    project.logger.error "======>>>>name::> ${filePath}"
                    AsmHandler.handleClass(file)
                }
            }
        }
    }

    public static void injectJar(Project project, String jarPath) {
    }

}