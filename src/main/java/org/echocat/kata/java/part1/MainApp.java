package org.echocat.kata.java.part1;
import org.eclipse.swt.widgets.*;
public class MainApp {

    public static void main(String[] args) {
        Display display = new Display ();
        Shell shell = new Shell(display);
        shell.setText("Snippet 1");
        shell.open ();
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        display.dispose ();
    }

    protected static String getHelloWorldText() {
        return "Hello world!";
    }

}
