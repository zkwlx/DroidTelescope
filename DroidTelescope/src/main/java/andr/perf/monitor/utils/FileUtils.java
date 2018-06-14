package andr.perf.monitor.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zhoukewen
 * @since 2018/1/16
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 复制文件
     *
     * @param fromFile
     * @param toFilePath
     */
    public static void copyFile(File fromFile, String toFilePath) {
        if (fromFile == null || TextUtils.isEmpty(toFilePath) || !fromFile.exists()) {
            Log.i(TAG, "File copy failed!");
            return;
        }
        File toFile = new File(toFilePath);
        if (toFile.exists()) {
            toFile.delete(); // delete file
        }
        File fileParent = toFile.getParentFile();
        if (!fileParent.exists()) {
            if (!fileParent.mkdirs()) {
                Log.e(TAG, "Make dirs false! dir:" + fileParent.getAbsolutePath());
            }
        }
        try {
            toFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fromStream = new FileInputStream(fromFile);
             FileOutputStream toStream = new FileOutputStream(toFile)) {
            FileChannel fromChannel = fromStream.getChannel();
            FileChannel toChannel = toStream.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件
     *
     * @param fromStream
     * @param toFilePath
     */
    public static void copyFile(InputStream fromStream, String toFilePath, boolean force) {
        if (fromStream == null || TextUtils.isEmpty(toFilePath)) {
            Log.i(TAG, "File copy failed!");
            return;
        }
        File toFile = new File(toFilePath);
        if (toFile.exists() && force) {
            toFile.delete(); // delete file
        } else {
            return;
        }
        File fileParent = toFile.getParentFile();
        if (!fileParent.exists()) {
            if (!fileParent.mkdirs()) {
                Log.e(TAG, "Make dirs false! dir:" + fileParent.getAbsolutePath());
            }
        }
        try {
            toFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream fromStreamInner = fromStream;
             FileOutputStream toStream = new FileOutputStream(toFile)) {
            byte[] buffer = new byte[4096];
            int byteCount;
            while ((byteCount = fromStreamInner.read(buffer)) != -1) {
                toStream.write(buffer, 0, byteCount);
            }
            toStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyAssetsDir(Context context, String assetsPath, String toPath) {
        AssetManager assetManager = context.getAssets();
        try {
            String fileNames[] = assetManager.list(assetsPath);
            if (fileNames.length > 0) {
                forceCreateDir(toPath);
                for (String fileName : fileNames) {
                    copyAssetsDir(context, assetsPath + File.separator + fileName,
                            toPath + File.separator + fileName);
                }
            } else {
                InputStream assetInputStream = assetManager.open(assetsPath);
                copyFile(assetInputStream, toPath, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归获取目录大小（包括当前根目录）
     * 例如：获取/a/b/c/目录大小，包括c目录本身大小
     *
     * @param file
     * @return long
     */
    public static long getDirSize(File file) {
        long size = 0;
        if (file == null || !file.exists()) {
            return size;
        }
        if (file.isDirectory()) {
            //加上目录大小
            size += file.length();
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File aFile : fileList) {
                    size += getDirSize(aFile);
                }
            }
        } else {
            size = file.length();
        }
        return size;
    }

    /**
     * 递归删除文件或目录下的所有文件和目录，包括当前目录或文件
     * 例如传入目录/a/b/c/，删除完后/a/b/,
     * 如果传入文件，直接删除
     *
     * @param file
     * @return
     */
    public static boolean deleteFileOrDir(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File aFile : fileList) {
                    deleteFileOrDir(aFile);
                }
            }
        }
        return file.delete();
    }

    public static File forceCreateDir(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return null;
        }
        File dir = new File(dirPath);
        if (!dir.mkdirs()) {
            Log.e(TAG, "Make dirs false! dir:" + dir.getAbsolutePath());
        }
        return dir;
    }

    public static File forceCreateNewFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            if (!fileParent.mkdirs()) {
                Log.e(TAG, "Make dirs false! dir:" + fileParent.getAbsolutePath());
            }
        }
        try {
            boolean result = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 将String类型数据写入File，如果文件有数据则清空
     *
     * @param file
     * @param content
     * @return
     */
    public static boolean writeStringToFile(File file, String content) {
        return writeToFile(file, content, false);
    }

    /**
     * 将String类型数据写入File，如果文件有数据则在末尾追加数据
     *
     * @param file
     * @param content
     * @return
     */
    public static boolean appendStringToFile(File file, String content) {
        return writeToFile(file, content, true);
    }

    private static boolean writeToFile(File file, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath(), append))) {
            bufferedWriter.write(content);
            bufferedWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断目录下是否有文件。如果目录为空，或者只有子目录，则为false
     *
     * @param dirFile
     * @return
     */
    public static boolean isDirHasFile(File dirFile) {
        if (dirFile == null || !dirFile.isDirectory()) {
            return false;
        }
        return isFileOrHasFile(dirFile);
    }

    /**
     * 压缩目录到zip文件，并且保持目录结构
     */
    public static boolean compressDirToZip(File dirFile, File outputZipFile) throws IOException {
        if (dirFile == null || !dirFile.exists()) {
            return false;
        }
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputZipFile))) {
            if (dirFile.isDirectory()) {
                File[] fileList = dirFile.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (File aFile : fileList) {
                        compressToZipStream(aFile, zipOutputStream, null);
                    }
                } else {
                    //目录为空，返回失败
                    return false;
                }
            } else {
                //不是目录，返回失败
                return false;
            }
        }
        return true;
    }


    /**
     * 判断File是否是文件，或者是否包含文件。如果目录为空，或者只有子目录，则为false
     *
     * @param file
     * @return
     */
    private static boolean isFileOrHasFile(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File aFile : fileList) {
                    boolean hasFile = isFileOrHasFile(aFile);
                    if (hasFile) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 保持目录结构创建zip文件
     *
     * @param file
     * @param zipOutputStream
     * @param baseDir
     * @throws IOException
     */
    private static void compressToZipStream(File file, ZipOutputStream zipOutputStream, String baseDir) throws IOException {
        if (file.isDirectory()) {
            //根据目录名称，创建zip文件中的目录结构
            String dirName = file.getName();
            dirName = dirName.endsWith(File.separator) ? dirName : dirName + File.separator;
            baseDir = TextUtils.isEmpty(baseDir) ? dirName : baseDir + dirName;

            File[] fileList = file.listFiles();
            if (fileList != null && fileList.length > 0) {
                //如果有子文件, 遍历子文件
                for (File aFile : fileList) {
                    compressToZipStream(aFile, zipOutputStream, baseDir);
                }
            } else {
                //如果目录为空，则将空目录结构写进zip
                ZipEntry zipEntry = new ZipEntry(baseDir);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.closeEntry();
            }
        } else {
            //将文件写入zip，并保留其base目录结构
            String name = TextUtils.isEmpty(baseDir) ? file.getName() : baseDir + file.getName();
            ZipEntry zipEntry = new ZipEntry(name);
            zipOutputStream.putNextEntry(zipEntry);
            try (FileInputStream inputStream = new FileInputStream(file)) {
                int len;
                byte[] buffer = new byte[4096];
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, len);
                }
                zipOutputStream.flush();
            }
            zipOutputStream.closeEntry();
        }
    }

}
