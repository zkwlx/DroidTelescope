package monitor.plugin

import org.gradle.api.Project

/**
 * Created by ZhouKeWen on 17/3/17.
 */
public class PluginExtension {
    HashSet<String> includePackage = []
    HashSet<String> excludeClass = []
    String oldNuwaDir

    PluginExtension(Project project) {
    }
}