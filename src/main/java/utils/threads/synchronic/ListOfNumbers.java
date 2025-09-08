package utils.threads.synchronic;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class ListOfNumbers {

    private List<Integer> list;
    private static final int SIZE = 10;

    public ListOfNumbers () {
        list = new ArrayList<Integer>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            list.add(i);
        }
    }

    public static class MyFileOperation extends PrintWriter {
        public MyFileOperation(Writer out) {
            super(out);
        }

        @Override
        public void close() {
            System.out.println("Inside MyFileOperation.close()");
        }
    }

    public void writeList() {
        try (MyFileOperation out = new MyFileOperation(new FileWriter("usnumbers.txt"))){
            for (int i = 0; i < SIZE; i++) {
                // The get(int) method throws IndexOutOfBoundsException, which must be caught.
                out.println("Value at: " + i + " = " + list.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ListOfNumbers listOfNumbers = new ListOfNumbers();
        listOfNumbers.writeList();
    }
}