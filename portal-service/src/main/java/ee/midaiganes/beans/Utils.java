package ee.midaiganes.beans;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public class Utils {
    private final Injector injector;
    private static final Utils instance = new Utils();

    private Utils() {
        this.injector = Guice.createInjector(Stage.PRODUCTION, new PortalModule());
    }

    public static final Injector getInstance() {
        return instance.injector;
    }
}
