package plugin.gradle.my;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ZhouKeWen on 2017/4/13.
 */
public class FileUtils {

    private String SDPATH;

    public String getSDPATH() {
        return SDPATH;
    }

    //构造函数，得到SD卡的目录，这行函数得到的目录名其实是叫"/SDCARD"
    public FileUtils() {
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    //在SD卡上创建文件
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    //在SD卡上创建目录
    public File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
    }

    //判断SD卡上的文件夹是否存在
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    //将一个InputStream里面的数据写入到SD卡中
    //将input写到path这个目录中的fileName文件上
    public File write2SDFromInput(String path, String fileName, String content) {
        File file = null;
        BufferedWriter bufferWritter = null;
        try {
            createSDDir(path);
            file = createSDFile(path + fileName);

            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWritter = new FileWriter(file, false);
            bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferWritter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
