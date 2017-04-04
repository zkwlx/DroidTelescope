package monitor.plugin

import monitor.plugin.javassist.JavassistHandler
import monitor.plugin.utils.LogUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 代码注入器
 * //TODO 过滤代码在这个类里加！
 * Created by ZhouKeWen on 17/3/17.
 */
public class Injector {

    public static void setClassPathForJavassist(Set<File> files) {
        JavassistHandler.setClassPath(files)
    }

    public static void inject(Project project, File file) {
        if (file) {
            String filePath = file.absolutePath;
            if (file.isDirectory()) {
                injectForDir(project, file)
            } else if (filePath.endsWith(".jar")) {
                injectForJar(project, file)
            }
        } else {
            LogUtils.printLog("Inject error! file is null!!")
        }
    }

    private static void injectForDir(Project project, File dirFile) {
        dirFile.eachFileRecurse { File file ->
            String filePath = file.absolutePath
            //TODO 这里是过滤代码，统一搞
            if (filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('R.class') &&
                    !filePath.contains("BuildConfig.class")) {
                project.logger.error "======>>>>name::> ${filePath}"
                JavassistHandler.handleClass(file)
            }
        }
    }

    private static void injectForJar(Project project, File file) {
        LogUtils.printLog("[process jar]============" + file.absolutePath)
        if (!file.absolutePath.contains("testlibrary")) {
            return;
        }
        JarFile jarFile = new JarFile(file)
        Enumeration enumeration = jarFile.entries()
        File tempOutJar = new File(file.getParent(), file.getName() + ".tmp")
        JarOutputStream output = new JarOutputStream(new FileOutputStream(tempOutJar))
        while (enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement()
            String entryName = entry.getName();
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(entry)
            output.putNextEntry(zipEntry)
            LogUtils.printLog("entry name............." + entryName)
            if (entryName.endsWith(".class")) {
                def bytes = JavassistHandler.handleClass(inputStream)
                output.write(bytes)
            } else {
                output.write(inputStream.getBytes())
            }
            output.closeEntry()
        }
        output.close()
        jarFile.close()
        if (file.exists()) {
            file.delete()
        }
        tempOutJar.renameTo(file)
        if (tempOutJar.exists()) {
            tempOutJar.delete()
        }
    }
}