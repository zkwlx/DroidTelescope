package monitor.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import monitor.plugin.config.InjectConfig
import monitor.plugin.utils.LogUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by ZhouKeWen on 17/3/17.
 */
public class MonitorInjectPlugin implements Plugin<Project> {

    void apply(Project project) {
        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)

        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        project.extensions.create("injectConfig", InjectConfig)
        InjectConfig config = project.injectConfig
        ConfigProvider.setConfig(config)

        /**
         * 注册transform接口
         */
        def android = project.extensions.getByType(AppExtension)
        def transform = new InjectTransform(project, config)
        android.registerTransform(transform)
    }


}