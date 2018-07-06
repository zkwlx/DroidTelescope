package monitor.plugin

import com.google.common.io.Files
import com.google.common.io.ByteStreams
import monitor.plugin.javassist.JavassistHandler
import monitor.plugin.utils.ClassFilterUtils
import monitor.plugin.utils.Logger

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

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
        final int index = file.absolutePath.indexOf(InjectTransform.NAME) + InjectTransform.NAME.length()
        Closure handleFileClosure = { File innerFile ->
            String filePath = innerFile.absolutePath
            if (shouldInjectFile(filePath)) {
                def outputFile = new File(buildDir, TMP_DIR + innerFile.absolutePath.substring(index))
                Files.createParentDirs(outputFile)
                FileInputStream inputStream = new FileInputStream(innerFile)
                FileOutputStream outputStream = new FileOutputStream(outputFile)
                byte[] modified = injectForInputStream(innerFile.name, inputStream)
                outputStream.write(modified)
                inputStream.close()
                outputStream.close()

                Files.copy(outputFile, innerFile)
            } else {
//                Logger.i("skip class file:>> " + filePath)
            }
        }


        if (file.isDirectory()) {
            Logger.d("################## is dir ${file.absolutePath} ################")
            file.eachFileRecurse {
                handleFileClosure
            }
        } else {
            handleFileClosure.call(file)
        }
    }

    private static void injectForJar(File outJarFile) {
        final int index = outJarFile.absolutePath.indexOf(InjectTransform.NAME) + InjectTransform.NAME.length()
        final def tmpFile = new File(buildDir, TMP_DIR + outJarFile.absolutePath.substring(index))
        Files.createParentDirs(tmpFile)

        new ZipInputStream(new FileInputStream(outJarFile)).withCloseable { zis ->
            new ZipOutputStream(new FileOutputStream(tmpFile)).withCloseable { zos ->

                ZipEntry entry
                while ((entry = zis.getNextEntry()) != null) {
                    if (shouldInjectJarEntry(entry.name)) {
                        byte[] modified = injectForInputStream(entry.name, zis)
                        zos.putNextEntry(new ZipEntry(entry.name))
                        zos.write(modified)
                    } else {
                        zos.putNextEntry(entry)
                        ByteStreams.copy(zis, zos)
                    }
                    zos.closeEntry()
                    zis.closeEntry()
                }
            }
        }

        Files.copy(tmpFile, outJarFile)
    }

    private static byte[] injectForInputStream(String className, InputStream inputStream) {
        //TODO 可以在这里替换字节码修改框架的实现
        return JavassistHandler.handleClass(className, inputStream)
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