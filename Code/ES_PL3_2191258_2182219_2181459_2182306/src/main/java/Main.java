import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import views.WorldAthletics;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main extends AbstractModule {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new Main());
        injector.getInstance(Main.class).start();
    }

    @Override
    protected void configure() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("Esoft_Projeto");
        EntityManager entityManager = factory.createEntityManager();
        bind(EntityManager.class).toInstance(entityManager);

        bind(WorldAthletics.class);
    }

    @Inject
    WorldAthletics mainView;

    private void start() {
        mainView.prepareData();
        mainView.openView();
    }
}
