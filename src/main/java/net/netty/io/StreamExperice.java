package net.netty.io;

import java.io.*;

@FunctionalInterface
public interface StreamExperice {
    void apply(String  path) throws IOException;
    static void main(String[] args) throws IOException {
        ByteStream.apply("src/main/java/net/netty/io/xanadu.txt");
        CharacterStream.apply("src/main/java/net/netty/io/xanadu.txt");
    }

    // 接口中的成员变量，默认public static  final
    StreamExperice ByteStream = (path) ->  {
        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = bis.read(bytes)) != -1) {
            System.out.println(new String(bytes, 0, len));
        }
        bis.close();
    };

    StreamExperice CharacterStream = (path) ->  {
        FileReader inputStream = null;
        FileWriter outputStream = null;

        try {
            inputStream = new FileReader("src/main/java/net/netty/io/xanadu.txt");
            outputStream = new FileWriter(path);

            int c;
            while ((c = inputStream.read()) != -1) {
                outputStream.write(c);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    };
}

