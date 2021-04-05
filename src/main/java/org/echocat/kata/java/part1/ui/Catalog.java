package org.echocat.kata.java.part1.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.echocat.kata.java.part1.core.InMemoryStore;
import org.echocat.kata.java.part1.core.Store;
import org.echocat.kata.java.part1.model.Media;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class Catalog extends ApplicationWindow {

    private final Store store;

    public Catalog() {
        super(null);
        this.store = InMemoryStore.INSTANCE;
    }

    public void run() {
        setBlockOnOpen(true);
        open();
        Display.getCurrent().dispose();
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setSize(640, 480);
        shell.setText("Magazines Kata");
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite control = new Composite(parent, SWT.NONE);
        control.setLayout(new GridLayout(2, false));
        control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        ComboViewer combo = new ComboViewer(control, SWT.SINGLE | SWT.READ_ONLY);
        combo.setContentProvider(new ArrayContentProvider());
        combo.setInput(Store.FilterType.values());
        combo.getCombo().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        combo.getCombo().select(0);

        Text text = new Text(control, SWT.BORDER);
        text.setMessage("Type to filter...");
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        TableViewer viewer = createTableViewer(control);

        combo.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = combo.getCombo().getSelectionIndex();
                Store.FilterType type = Store.FilterType.values()[index];
                search(viewer, text.getText(), type);
            }

        });
        text.addModifyListener(new DelayedModifyListener(text, 200) {

            @Override
            protected void onTextDidModify(ModifyEvent e, String value) {
                int index = combo.getCombo().getSelectionIndex();
                Store.FilterType type = Store.FilterType.values()[index];
                search(viewer, value, type);
            }

        });

        viewer.setInput(Collections.singleton("Loading data..."));
        search(viewer, null, null);
        return control;
    }

    protected TableViewer createTableViewer(Composite parent) {
        TableViewer viewer = new TableViewer(parent,
                SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        for (String text : Arrays.asList("Title", "Authors", "ISBN", "Details")) {
            TableColumn column = new TableColumn(viewer.getTable(), SWT.FILL | SWT.BORDER);
            column.setText(text);
            column.setWidth(200);
        }
        viewer.setContentProvider(new CatalogContentProvider());
        viewer.setLabelProvider(new CatalogLabelProvider());
        viewer.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof Media && e2 instanceof Media) {
                    return ((Media) e1).getTitle().compareToIgnoreCase(((Media) e2).getTitle());
                }
                return super.compare(viewer, e1, e2);
            }
        });
        viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        return viewer;
    }

    protected void search(TableViewer viewer, String filterText, Store.FilterType type) {
        store.search(filterText, type).thenApply(media -> media.collect(Collectors.toList()))
                .thenAccept(media -> this.refresh(viewer, media));
    }

    // Note: this is a hack. `setInput` should be sufficient to refresh the `viewer`
    // content.
    // For some reason on Big Sur we have to refresh the viewer columns manually.
    protected void refresh(TableViewer viewer, Object input) {
        viewer.getControl().getDisplay().asyncExec(() -> {
            viewer.setInput(input);
            viewer.refresh();
            for (int i = 0, n = viewer.getTable().getColumnCount(); i < n; i++) {
                viewer.getTable().getColumn(i).pack(); // This is the hack!
                viewer.getTable().getColumn(i).setWidth(200);
            }
        });
    }

}
