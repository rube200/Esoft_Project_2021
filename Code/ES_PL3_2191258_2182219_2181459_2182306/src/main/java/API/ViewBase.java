package API;

import java.awt.*;

public interface ViewBase {
    /**
     * @return o painel principal da view
     */
    Container getViewContainer();

    /**
     * @return verdadeiro se a vista for preparada com sucesso, senão falso
     */
    boolean prepareView();

    void setupBackButton(Runnable buttonBackCallback);
}
