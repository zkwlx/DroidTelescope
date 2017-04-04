package monitor.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by ZhouKeWen on 17/3/17.
 */
public class PluginImpl implements Plugin<Project> {
    void apply(Project project) {

        /**
         * 注册transform接口
         */
//        def isApp = project.plugins.hasPlugin(PluginImpl)//TODO 注意这里的PluginImpl
//        if (isApp) {
        //TODO 注意看看app和lib是否有重复调用
        def android = project.extensions.getByType(AppExtension)
        def transform = new PreDexTransform(project)
        android.registerTransform(transform)
    }
}