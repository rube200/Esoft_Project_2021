package views.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

class ModelListRow {
    private JPanel mainPanel;
    private JLabel modelName;
    private JButton buttonEdit;
    private JButton buttonDelete;

    JComponent getComponent() {
        return mainPanel;
    }

    void setText(String text) {
        modelName.setText(text);
    }

    void setButtons() {
        setImage(buttonDelete, "imagens/delete.png", "[DELETE]");
        setImage(buttonEdit, "imagens/edit.png", "[EDIT]");
    }

    int getButtonEditX() {
        return buttonEdit.getX();
    }

    int getButtonEditMaxX() {
        return buttonEdit.getX() + buttonEdit.getWidth();
    }

    int getButtonDeleteX() {
        return buttonDelete.getX();
    }

    int getButtonDeleteMaxX() {
        return buttonDelete.getX() + buttonDelete.getWidth();
    }

    private void setImage(AbstractButton button, String path, String alt) {
        try {
            int size = modelName.getFont().getSize();
            URL location;
            if ((location = ClassLoader.getSystemResource(path)) != null) {
                Image editImage = ImageIO.read(location);
                editImage = editImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(editImage));
            } else
                button.setText(alt);
        } catch (IOException ex) {
            ex.printStackTrace();
            button.setText(alt);
        }
    }
}
