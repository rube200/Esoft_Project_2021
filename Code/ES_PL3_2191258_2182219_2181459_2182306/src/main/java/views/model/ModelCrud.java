package views.model;

public class ModelCrud<T> implements ModelCrudRow {
    private final T model;
    private final Runnable editCallback;
    private final Runnable deleteCallback;
    private int editX;
    private int editMaxX;
    private int deleteX;
    private int deleteMaxX;
    public ModelCrud(T model, Runnable editCallback, Runnable deleteCallback) {
        this.model = model;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
    }

    @Override
    public void onModelPress(int positionX) {
        if (editX <= positionX && positionX <= editMaxX) {
            editCallback.run();
            return;
        }

        if (deleteX <= positionX && positionX <= deleteMaxX) {
            deleteCallback.run();
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    @Override
    public void setXPositions(int editX, int editMaxX, int deleteX, int deleteMaxX) {
        if (editX != 0 && editMaxX != 0) {
            this.editX = editX;
            this.editMaxX = editMaxX;
        }

        if (deleteX != 0 && editMaxX != 0) {
            this.deleteX = deleteX;
            this.deleteMaxX = deleteMaxX;
        }
    }

    @Override
    public String toString() {
        return model.toString();
    }
}

