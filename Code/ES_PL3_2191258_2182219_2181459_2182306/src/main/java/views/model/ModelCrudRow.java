package views.model;

public interface ModelCrudRow {
    void onModelPress(int positionX, int positionY);

    void setXPositions(int editX, int editMaxX, int deleteX, int deleteMaxX, int y, int maxY);
}
