package org.echocat.kata.java.part1.ui;

import java.util.Collection;

import com.google.common.collect.Iterables;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public class CatalogContentProvider implements IStructuredContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Iterable) {
            if (Iterables.isEmpty((Iterable<?>) inputElement)) {
                return new String[] { "No result" };
            }
        }
        if (inputElement instanceof String) {
            return new String[] { (String) inputElement };
        }
        if (inputElement instanceof Object[]) {
            return (Object[]) inputElement;
        }
        if (inputElement instanceof Collection) {
            return ((Collection<?>) inputElement).toArray();
        }
        return null;
    }

}
