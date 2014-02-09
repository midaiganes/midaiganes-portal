package ee.midaiganes.cache;

public class SingleVmPoolUtil {
    private static SingleVmPool singleVmPool = new SingleVmPool();

    public static SingleVmCache getCache(String name) {
        return SingleVmPoolUtil.singleVmPool.getCache(name);
    }

    public static void destroy() {
        SingleVmPoolUtil.singleVmPool.preDestroy();
    }
}
