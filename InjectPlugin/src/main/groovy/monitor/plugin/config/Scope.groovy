package monitor.plugin.config

/**
 * Created by ZhouKeWen on 17/3/29.
 */
class Scope {
    /**
     * 当前项目src下的class
     */
    boolean project = true
    /**
     * 当前项目依赖的jar包，比如libs下的所有jar包
     */
    boolean projectLocalDep = false
    /**
     * 当前项目依赖的子项目，比如compile project(':testlibrary')
     */
    boolean subProject = true
    /**
     * 当前项目所依赖的子项目的依赖jar包
     */
    boolean subProjectLocalDep = false
    /**
     * 外部依赖，比如compile 'com.android.support:appcompat-v7:24.3.0'
     */
    boolean externalLibraries = false

    void project(boolean enable) {
        project = enable
    }

    void projectLocalDep(boolean enable) {
        projectLocalDep = enable
    }

    void subProject(boolean enable) {
        subProject = enable
    }

    void subProjectLocalDep(boolean enable) {
        subProjectLocalDep = enable
    }

    void externalLibraries(boolean enable) {
        externalLibraries = enable
    }
}
