package andr.perf.monitor.stack_traces;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import andr.perf.monitor.utils.FileUtils;

/**
 * @author zhoukewen
 * @since 2018/5/24
 */
public class TraceHtmlReporter {

    private static final String JS_TEMPLATE = "function getJsonData(){var m = '%s';return $.parseJSON(m);}";
    private static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String REPORT_DIR = ROOT_DIR + "/trace_html_report/";

    /**
     * 创建报告 zip 文件，
     * 由于要从 Assets 中复制文件，所以需要 Context。
     *
     * @param context
     * @param traceContent
     * @return
     */
    public static File createTraceReportFile(Context context, String traceContent) {
        File dirFile = FileUtils.forceCreateDir(REPORT_DIR);
        FileUtils.copyAssetsDir(context, "trace_html_report", dirFile.getAbsolutePath());
        createJSFileToReportDir(traceContent);
        File outZipFile = new File(ROOT_DIR, "/trace_html_report.zip");
        try {
            FileUtils.compressDirToZip(dirFile, outZipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outZipFile;
    }

    private static void createJSFileToReportDir(String content) {
        String jsContent = createJSString(content);
        String jsPath = REPORT_DIR + "data.js";
        File jsFile = FileUtils.forceCreateNewFile(jsPath);
        FileUtils.writeStringToFile(jsFile, jsContent);
    }

    private static String createJSString(String content) {
        return String.format(JS_TEMPLATE, content);
    }
}
