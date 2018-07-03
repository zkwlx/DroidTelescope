package monitor.plugin

import monitor.plugin.javassist.JavassistHandler
import monitor.plugin.utils.ClassFilterUtils
import monitor.plugin.utils.Logger

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 代码注入器
 * Created by ZhouKeWen on 17/3/17.
 */
class Injector {

    static final String TMP_DIR = "tmp/Telescope_Inject/"
    static File buildDir

    private static Set<String> excludePackage
    private static Set<String> includePackage
    private static Set<String> excludeClass

    static void setPackagesConfig(List<String> excludePackage, List<String> includePackage,
                                  List<String> excludeClass) {
        this.excludePackage = ClassFilterUtils.formatPath(excludePackage)
        this.includePackage = ClassFilterUtils.formatPath(includePackage)
        this.excludeClass = ClassFilterUtils.formatClass(excludeClass)
        Logger.i("include:" + this.includePackage + " excludePkg:" + this.excludePackage + " excludeCls:" + this.excludeClass)
    }

    static void setClassPathForJavassist(List<File> files) {
        JavassistHandler.addClassPath(files)
    }

    static void inject(File file) {
        if (file) {
            String filePath = file.absolutePath
            if (filePath.endsWith(".jar")) {
                injectForJar(file)
            } else {
                injectForFile(file)
            }
        } else {
            Logger.i("Inject error! file is null!!")
        }
    }

    private static void injectForFile(File file) {
        Closure handleFileClosure = { File f ->
            String filePath = f.absolutePath
            if (shouldInjectFile(filePath)) {
//                Logger.i("---->handle file:${file.absolutePath}")
                JavassistHandler.handleClass(f)
            } else {
//                Logger.i("skip class file:>> " + filePath)
            }
        }
        if (file.isDirectory()) {
            file.eachFileRecurse {
                handleFileClosure
            }
        } else {
            handleFileClosure.call(file)
        }
    }

    private static void injectForJar(File file) {
//        Logger.i("[process jar]============" + file.absolutePath)
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
            if (shouldInjectJarEntry(entryName)) {
                def bytes = JavassistHandler.handleClass(entryName, inputStream)
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
    private static boolean shouldInjectJarEntry(String entryName) {
        if (ClassFilterUtils.skipThisClassForJar(entryName)) {
            return false
        }
        return ClassFilterUtils.isIncluded(entryName, includePackage) &&
                !ClassFilterUtils.isExcluded(entryName, excludePackage, excludeClass)
    }

    //过滤Class文件
    private static boolean shouldInjectFile(String filePath) {
        if (ClassFilterUtils.skipThisClassForFile(filePath)) {
            return false
        }
        return ClassFilterUtils.isIncluded(filePath, includePackage) &&
                !ClassFilterUtils.isExcluded(filePath, excludePackage, excludeClass)
    }
}