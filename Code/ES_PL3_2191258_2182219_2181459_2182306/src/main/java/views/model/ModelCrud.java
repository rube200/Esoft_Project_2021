package views.model;

import model.UniqueId;

public class ModelCrud<T extends UniqueId> implements ModelCrudRow {
    private final T model;
    private final Runnable editCallback;
    private final Runnable deleteCallback;
    private int editX;
    private int editMaxX;
    private int deleteX;
    private int deleteMaxX;
    private int y;
    private int maxY;

    public ModelCrud(T model) {
        this(model, null, null);
    }

    public ModelCrud(T model, Runnable editCallback, Runnable deleteCallback) {
        this.model = model;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public void onModelPress(int positionX, int positionY) {
        if (editCallback == null && deleteCallback == null)
            return;

        if (y > positionY || positionY > maxY)
            return;

        if (editCallback != null && editX <= positionX && positionX <= editMaxX) {
            editCallback.run();
            return;
        }

        if (deleteCallback != null && deleteX <= positionX && positionX <= deleteMaxX) {
            deleteCallback.run();
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    @Override
    public void setXPositions(int editX, int editMaxX, int deleteX, int deleteMaxX, int y, int maxY) {
        if (editCallback == null && deleteCallback == null)
            return;

        if (editX != 0 && editMaxX != 0) {
            this.editX = editX;
            this.editMaxX = editMaxX;
        }

        if (deleteX != 0 && editMaxX != 0) {
            this.deleteX = deleteX;
            this.deleteMaxX = deleteMaxX;
        }

        this.y = y;
        this.maxY = maxY;
    }

    @Override
    public String toString() {
        return model.toString();
    }
}

