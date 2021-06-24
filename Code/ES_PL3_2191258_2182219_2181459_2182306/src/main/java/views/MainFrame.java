package views;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final String TITLE = "World Athletics";
    private static final Dimension DefaultSize = new Dimension(800, 400);

    public MainFrame() {
        super(TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(DefaultSize);
    }
}
