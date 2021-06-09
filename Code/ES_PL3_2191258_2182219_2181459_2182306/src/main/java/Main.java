import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import database.DatabaseConnector;
import views.WorldAthletics;

public class Main extends AbstractModule {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new Main());
        injector.getInstance(Main.class).start();
    }

    @Override
    protected void configure() {
        bind(DatabaseConnector.class);
        bind(WorldAthletics.class);
    }

    @Inject
    WorldAthletics mainView;

    private void start() {
        mainView.openView();
    }
}
