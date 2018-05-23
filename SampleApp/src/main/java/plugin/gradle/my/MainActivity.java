package plugin.gradle.my;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shit.testlibrary.TestLibraryClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import andr.perf.monitor.DroidTelescope;
import plugin.gradle.my.concurrent_test.ExecutorManager;

public class MainActivity extends AppCompatActivity {

    private TestLibraryClass test;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            g2();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        gogo(100);

        Fragment f = new Fragment();
        android.app.Fragment ff = new android.app.Fragment();

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }

    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this).setTitle("存储权限不可用").setMessage("由于需要获取存储空间，为你存储个人信息；\n否则，您将无法正常使用支付宝")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this).setTitle("存储权限不可用").setMessage("请在-应用设置-权限-中，允许使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //        try {
        //            Thread.sleep(200);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //
        //        try {
        //            Thread.sleep(200);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //
        //        try {
        //            Thread.sleep(400);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("zkw", "----------------_>>>on trim memory:" + level);
    }

    public void onGoListClick(View view) {
//        Intent i = new Intent(this, ListActivity.class);
//        startActivity(i);
        ExecutorManager manager = new ExecutorManager();
        manager.execut();
    }

    public void onGoClick(View view) {
        Intent i = new Intent(this, ScrollingActivity.class);
        startActivity(i);
    }

    public void onSlowClick(View view) {
        Intent i = new Intent(this, SecondActivity.class);
        startActivity(i);
        try {
            g2();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        gogo(87878787);

        new Thread(new Runnable() {
            @Override
            public void run() {
                isT("");
                try {
                    g2();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (test == null) {
            test = new TestLibraryClass();
        }
        test.startTestt();

        new TestLibraryClass().startTestt();

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int gogo(int c) {
        int a = Log.i("zkw", "hello");
        if (a > 0) {
            Log.i("zkw", ">>0");
        } else {
            Log.i("zkw", "<<0");
        }
        isT(new Paint());
        isT(this);
        return a;
    }

    public Object g2() throws IllegalAccessException {
        int a = new Random().nextInt(100);
        if (isT("")) {
            Log.i("g2", "isT() true");
        } else if (a > 5) {
            Log.i("g2", "isT() false>>> " + a);
            throw new IllegalAccessException();
        }
        if (a < 5) {
            Log.i("g2", "a>>> " + a);
            return "aaa";
        } else {
            Log.i("g2", "......." + a);
        }
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {
            Log.i("g2", "NullPointerException!!!!");
        } finally {
            Log.i("g2", "finally");
        }
        Log.i("g2", ">>>>>" + a);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isT(Object o) {
        Log.i("", "=======" + o);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(10) > 5;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        launchFinished();
    }

    private void launchFinished() {
        String jsonString = DroidTelescope.stopMethodTracing();
        if (!TextUtils.isEmpty(jsonString)) {
            FileUtils fileUtils = new FileUtils();
            Random r = new Random();
            String fileName = "apm_method_tracing" + r.nextInt(100);
            fileUtils.write2SDFromInput("", fileName, jsonString);
            Log.i("zkw", "加载完成。。。。。。。。:::>" + fileName);
        } else {
            Log.i("zkw", "json is null!!!!!!");
        }
    }

}
