package monitor.plugin.utils

/**
 * <p>Created by ZhouKeWen on 17-4-4.</p>
 */
class Logger {

    static boolean isDebug = false

    static void i(String content) {
        System.out.println("[==injector==] " + content)
    }

    static d(String s) {
        if (isDebug) {
            System.out.println("[==injector==d== t:${Thread.currentThread().name}] | " + s)
        }
    }

}
