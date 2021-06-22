package API;

import java.awt.*;

public interface ViewBase {
    Container getViewContainer();

    boolean prepareView();

    void setupBackButton(Runnable buttonBackCallback);
}
