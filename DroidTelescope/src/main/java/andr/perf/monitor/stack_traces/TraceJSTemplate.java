package andr.perf.monitor.stack_traces;

import android.os.Environment;

import java.io.File;

import andr.perf.monitor.utils.FileUtils;

/**
 * @author zhoukewen
 * @since 2018/5/24
 */
public class TraceJSTemplate {

    private static String jsTemplate = "function getJsonData(){var m = '%s';return $.parseJSON(m);}";

    public static File createJSFile(String content) {
        String jsContent = createJSString(content);
        String jsPath = Environment.getExternalStorageDirectory() + "/data.js";
        File jsFile = FileUtils.forceCreateNewFile(jsPath);
        FileUtils.writeStringToFile(jsFile, jsContent);
        return jsFile;
    }

    private static String createJSString(String content) {
        return String.format(jsTemplate, content);
    }
}
