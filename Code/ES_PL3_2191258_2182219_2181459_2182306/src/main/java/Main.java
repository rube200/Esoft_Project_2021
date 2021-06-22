import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.*;
import com.google.inject.name.Names;
import controllers.DatabaseQuery;
import controllers.EventosController;
import controllers.ProvasController;
import controllers.ViewController;
import model.Evento;
import model.Prova;
import views.MainFrame;
import views.provas.Provas;
import views.WorldAthletics;
import views.eventos.Eventos;

import javax.swing.*;

class Main extends AbstractModule {
    @Inject
    ViewController viewController;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new Main());
        injector.getInstance(Main.class).start();
    }

    @Override
    protected void configure() {
        //bind controllers
        bind(DatabaseConnector.class).to(DatabaseQuery.class);
        bind(new TypeLiteral<CrudController<Evento>>() {
        }).annotatedWith(Names.named("EventosController")).to(EventosController.class);
        bind(new TypeLiteral<CrudController<Prova>>() {
        }).annotatedWith(Names.named("ProvasController")).to(ProvasController.class);
        bind(ViewController.class);

        //bind views
        bind(JFrame.class).to(MainFrame.class);
        bind(ViewBase.class).annotatedWith(Names.named("EventosView")).to(Eventos.class);
        bind(ViewBase.class).annotatedWith(Names.named("ProvasView")).to(Provas.class);
        bind(ViewBase.class).annotatedWith(Names.named("WorldAthleticsView")).to(WorldAthletics.class);
    }

    private void start() {
        viewController.initiateProgram();
    }
}