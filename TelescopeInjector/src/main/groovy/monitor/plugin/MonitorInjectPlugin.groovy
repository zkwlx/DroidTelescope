package monitor.plugin

import com.android.build.gradle.AppExtension
import monitor.plugin.config.InjectConfig
import monitor.plugin.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by ZhouKeWen on 17/3/17.
 */
class MonitorInjectPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!isAndroidProject(project)) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        InjectConfig config = project.extensions.create("injectConfig", InjectConfig)
        project.afterEvaluate {
            //要在 gradle Configure 步骤完成之后，config 的值才生效
            ConfigProvider.config = config
            Logger.isDebug = config.debugLog
        }

        // 注册transform接口
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new InjectTransform(project))
    }

    static boolean isAndroidProject(Project project) {
        def android = project.extensions.getByName("android")
        return android != null && android instanceof AppExtension
    }


}