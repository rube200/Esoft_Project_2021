package views.model;

import model.UniqueId;

public interface ModelCrudRow {
    UniqueId getModel();

    void onModelPress(int positionX, int positionY);

    void setXPositions(int editX, int editMaxX, int deleteX, int deleteMaxX, int y, int maxY);
}
