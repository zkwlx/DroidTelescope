package monitor.plugin.config

import org.gradle.api.Action

/**
 * Created by ZhouKeWen on 17/3/29.
 */
class InjectConfig {
    boolean releaseEnabled = true
    boolean debugEnabled = true
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
}
