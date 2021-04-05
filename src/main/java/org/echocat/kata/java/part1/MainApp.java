package org.echocat.kata.java.part1;

import org.echocat.kata.java.part1.ui.Catalog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MainApp {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Magazines");
        shell.setLayout(new FillLayout());
        new Catalog(shell);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    protected static String getHelloWorldText() {
        return "Hello world!";
    }

}
