package cd.go.plugin.auth.util;

public class LogHelper {
    public static void turnOffLogging() {
        /* Doesn't yet turn off logging in tests. Needs change in GoCD core for this. */
        System.setProperty("do.not.log.to.sysout.as.fallback", "true");
    }
}
