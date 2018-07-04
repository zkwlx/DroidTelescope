package monitor.plugin.config

import org.gradle.api.Action

/**
 * Created by ZhouKeWen on 17/3/29.
 */
class InjectConfig {
    //TODO 这两个开关可能没用了，删掉
//    boolean releaseEnabled = true
//    boolean debugEnabled = true
    boolean forRelease = false
    boolean memoryLeakEnable = false
    boolean cpuTimeEnable = false
    boolean interactiveEnable = false
    boolean debugLog = false

    List<String> includePackages = new ArrayList<>()
    List<String> excludePackages = new ArrayList<>()
    List<String> excludeClasses = new ArrayList<>()

    Scope scope = new Scope()

    void scope(Action<Scope> action) {
        action.execute(scope)
    }

    Scope getScope() {
        return scope
    }

    @Override
    String toString() {
        return "InjectConfig{" +
                "forRelease=" + forRelease +
                ", memoryLeakEnable=" + memoryLeakEnable +
                ", cpuTimeEnable=" + cpuTimeEnable +
                ", interactiveEnable=" + interactiveEnable +
                ", debugLog=" + debugLog +
                ", includePackages=" + includePackages +
                ", excludePackages=" + excludePackages +
                ", excludeClasses=" + excludeClasses +
                '}'
    }
}
