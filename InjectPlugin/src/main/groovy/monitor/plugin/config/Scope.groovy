package monitor.plugin.config

/**
 * Created by ZhouKeWen on 17/3/29.
 */
class Scope {
    boolean project = true
    boolean projectLocalDep = false
    boolean subProject = true
    boolean subProjectLocalDep = false
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
