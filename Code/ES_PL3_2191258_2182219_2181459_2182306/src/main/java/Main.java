import API.DatabaseConnector;
import API.MainView;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.spi.InjectionPoint;
import controllers.DatabaseQuery;
import views.WorldAthletics;

import javax.swing.*;

class Main extends AbstractModule {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Injector injector = Guice.createInjector(new Main());
            injector.getInstance(Main.class).start();
        });
    }

    @Override
    protected void configure() {
        bind(DatabaseConnector.class).to(DatabaseQuery.class);
        bind(MainView.class).to(WorldAthletics.class);
    }

    @Inject
    MainView mainView;

    private void start() {
        mainView.prepareView();
        mainView.displayView();
    }
}