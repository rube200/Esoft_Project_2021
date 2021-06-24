import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.*;
import com.google.inject.name.Names;
import controllers.*;
import views.atletas.Atletas;
import views.MainFrame;
import views.Medalhas;
import views.WorldAthletics;
import views.eventos.Eventos;
import views.modalidades.Modalidades;
import views.inscricoes.InscreverAtleta;
import views.provas.Provas;

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
        bind(DatabaseConnector.class).to(DatabaseQuery.class);
        bind(JFrame.class).to(MainFrame.class);
        bindView(WorldAthletics.class);
        bind(ViewController.class);

        bindCrudController(new TypeLiteral<>() {
        }, AtletasController.class, Atletas.class);
        bindCrudController(new TypeLiteral<>() {
        }, EventosController.class, Eventos.class);
        bindCrudController(new TypeLiteral<>() {
        }, ModalidadesController.class, Modalidades.class);
        bindCrudController(new TypeLiteral<>() {
        }, ProvasController.class, Provas.class);

        bind(MedalhasController.class);
        bindView(Medalhas.class);

        bind(InscricoesController.class);
        bindView(InscreverAtleta.class);
    }

    private <T> void bindCrudController(TypeLiteral<CrudController<T>> typeLiteral, Class<? extends CrudController<T>> controller, Class<? extends ViewBase> view) {
        bind(typeLiteral).annotatedWith(Names.named(controller.getSimpleName())).to(controller);
        bindView(view);
    }

    private void bindView(Class<? extends ViewBase> view) {
        bind(ViewBase.class).annotatedWith(Names.named(view.getSimpleName() + "View")).to(view);
    }

    private void start() {
        viewController.initiateProgram();
    }
}