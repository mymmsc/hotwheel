package org.mymmsc.asio.samples;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;


public class Client {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Client client = new Client();
        client.start();
    }



    public void start() {
        Selector selector = null;
        System.out.println("start ... ");
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress("www.sina.com.cn", 80));
            channel.configureBlocking(false);

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_WRITE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        ByteBuffer  buffer = ByteBuffer.allocate(1024 * 5);
        StringBuffer result = new StringBuffer();


        while(true) {
            try {
                int index = selector.select();
                System.out.println(index);
                if(index > 0) {
                    Iterator it = selector.selectedKeys().iterator();
                    //System.out.println(it);
                    while(it.hasNext()) {
                        SelectionKey key = (SelectionKey) it.next();
                        it.remove();

                        if(key.isReadable()) {
                            key.cancel();
                            System.out.println("read");

                            SocketChannel channel = (SocketChannel) key.channel();
                            //channel.socket().setSoTimeout(1000);
                            while(channel.read(buffer) != -1) {
                                buffer.flip();
                                decoder.decode(buffer, charBuffer, false);
                                charBuffer.flip();
                                System.out.println(charBuffer.toString());
                                result.append(charBuffer);
                                buffer.clear();
                                charBuffer.clear();
                            }

                            //System.out.println(result);
                            channel.finishConnect();
                            channel.close();
                            System.out.println("end.");
                        } else if(key.isWritable()) {

                            System.out.println("write");
                            SocketChannel channel = (SocketChannel) key.channel();

                            channel.write(encoder.encode(CharBuffer.wrap("GET / HTTP/1.1\r\n")));
                            channel.write(encoder.encode(CharBuffer.wrap("accept: www/source; text/html; image/gif; image/jpeg; */*\r\n")));
                            channel.write(encoder.encode(CharBuffer.wrap("User_Agent: myAgent\r\n")));
                            channel.write(encoder.encode(CharBuffer.wrap("\r\n")));
//       strs[0] = "GET / HTTP/1.0";
//       strs[1] = "accept: www/source; text/html; image/gif; image/jpeg; */*";
//       strs[2] = "User_Agent: myAgent";

                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ);
                            //key.cancel();
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}