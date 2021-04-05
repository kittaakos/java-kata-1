package org.echocat.kata.java.part1;

import org.echocat.kata.java.part1.ui.Catalog;

public class MainApp {

    public static void main(String[] args) {
        new Catalog().run();
    }

    protected static String getHelloWorldText() {
        return "Hello world!";
    }

}
