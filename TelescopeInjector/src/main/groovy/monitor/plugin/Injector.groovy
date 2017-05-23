package monitor.plugin

import monitor.plugin.javassist.JavassistHandler
import monitor.plugin.utils.ClassFilterUtils
import monitor.plugin.utils.LogUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 代码注入器
 * Created by ZhouKeWen on 17/3/17.
 */
public class Injector {

    private static Set<String> excludePackage
    private static Set<String> includePackage
    private static Set<String> excludeClass

    public static void setPackagesConfig(List<String> excludePackage, List<String> includePackage,
            List<String> excludeClass) {
        this.excludePackage = ClassFilterUtils.formatPath(excludePackage)
        this.includePackage = ClassFilterUtils.formatPath(includePackage)
        this.excludeClass = ClassFilterUtils.formatPath(excludeClass)
    }

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
            if (shouldInjectFileClass(filePath)) {
//                LogUtils.printLog("======>>>>name::> ${filePath}")
                JavassistHandler.handleClass(file)
            }
        }
    }

    private static void injectForJar(Project project, File file) {
//        LogUtils.printLog("[process jar]============" + file.absolutePath)
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
            if (shouldInjectJarClass(entryName)) {
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

    //过滤Jar包中的Class实体
    private static boolean shouldInjectJarClass(String entryName) {
        if (ClassFilterUtils.skipThisClassForJar(entryName)) {
            return false
        }
        return ClassFilterUtils.isIncluded(entryName, includePackage) &&
                !ClassFilterUtils.isExcluded(entryName, excludePackage, excludeClass)
    }

    //过滤Class文件
    private static boolean shouldInjectFileClass(String filePath) {
        if (ClassFilterUtils.skipThisClassForFile(filePath)) {
            return false
        }
        return ClassFilterUtils.isIncluded(filePath, includePackage) &&
                !ClassFilterUtils.isExcluded(filePath, excludePackage, excludeClass)
    }
}