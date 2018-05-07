package monitor.plugin;

import monitor.plugin.config.InjectConfig;

/**
 * @author zhoukewen
 * @since 2018/4/17
 */
public class ConfigProvider {
    private static InjectConfig config;

    public static InjectConfig getConfig() {
        return config;
    }

    public static void setConfig(InjectConfig config) {
        this.config = config;
    }
}
