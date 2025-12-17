package ex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ExceptionExperience {
    public static void main(String[] args) {
        try {
            int a = 1 / 0;
        } finally {
            System.out.println("finally");
        }


        // error
        /*try {

        } catch (Exception e) {

        } catch (ArithmeticException a) {

        }*/

        // true
        try {

        } catch (ArithmeticException a) {

        } catch (Exception e) {

        }


    }

    public static void cat(File file) throws IOException{
        RandomAccessFile input = null;
        String line = null;

        try {
            input = new RandomAccessFile(file, "r");
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            return;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
    }
}
