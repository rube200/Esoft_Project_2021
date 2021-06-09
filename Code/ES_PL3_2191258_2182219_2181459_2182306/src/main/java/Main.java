import com.google.inject.AbstractModule;
import com.google.inject.Guice;

public class MainModule extends AbstractModule {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new MainModule());

    }

    @Override
    protected void configure() {
        bind();
    }
}
