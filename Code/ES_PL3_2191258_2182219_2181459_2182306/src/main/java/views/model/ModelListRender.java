package views.model;

import javax.swing.*;
import java.awt.*;

public class ModelListRender<T extends ModelCrudRow> implements ListCellRenderer<T> {
    private final ModelListRow modelListRow;

    public ModelListRender() {
        modelListRow = new ModelListRow();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        modelListRow.setButtons();
        modelListRow.setText((value == null) ? "" : value.toString());

        JComponent component = modelListRow.getComponent();
        ComponentOrientation orientation = list.getComponentOrientation();
        component.setComponentOrientation(orientation);
        updateComponent(component, list, index, isSelected);

        if (isSelected && cellHasFocus && value != null)
            value.setXPositions(modelListRow.getButtonEditX(), modelListRow.getButtonEditMaxX(), modelListRow.getButtonDeleteX(), modelListRow.getButtonDeleteMaxX());

        return component;
    }

    private void updateComponent(JComponent component, JList<? extends T> list, int index, boolean isSelected) {
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {
            isSelected = true;
        }

        if (isSelected) {
            component.setBackground(list.getSelectionBackground());
            component.setForeground(list.getSelectionForeground());
        } else {
            component.setBackground(list.getBackground());
            component.setForeground(list.getForeground());
        }

        component.setEnabled(list.isEnabled());
        component.setFont(list.getFont());
    }
}