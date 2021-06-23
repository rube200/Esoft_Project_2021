package views.model;

public interface ModelCrudRow {
    void onModelPress(int positionX);

    void setXPositions(int editX, int editMaxX, int deleteX, int deleteMaxX);
}
