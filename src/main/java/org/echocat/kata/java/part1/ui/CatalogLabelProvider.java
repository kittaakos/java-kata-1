package org.echocat.kata.java.part1.ui;

import java.util.stream.Collectors;

import org.echocat.kata.java.part1.model.Media;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class CatalogLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof String) {
            return columnIndex == 0 ? (String) element : null;
        }
        if (!(element instanceof Media)) {
            return null;
        }
        Media media = (Media) element;
        switch (columnIndex) {
        case 0:
            return media.getTitle();
        case 1:
            return media.getAuthors().stream().map(Object::toString).sorted().collect(Collectors.joining(", "));
        case 2:
            return media.getIsbn();
        case 3:
            return media.getDetail();
        default:
            return null;
        }
    }

}
