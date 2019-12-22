package com.gb.dcloud.client;

import com.gb.dcloud.common.AbstractMessage;
import com.gb.dcloud.common.FileList;
import com.gb.dcloud.common.FileMessage;
import com.gb.dcloud.common.FileRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;


public class MainApp {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        Reader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String tfFileName = bufferedReader.readLine();
        System.out.println(tfFileName);


        Network.start();
        Thread t = new Thread(() -> {
            System.out.println(">Connect to server");
            Network.sendMsg(new FileRequest(tfFileName));
            if (tfFileName.equals("/dir")) {
                System.out.println(">Send " + tfFileName + " command to server");
            }

            try {
                    while (true) {
                        AbstractMessage am = Network.readObject();
                        System.out.println(">\treceive obj from Server");
                        if (am instanceof FileList) {
                            System.out.println(">\t\tit file list from Server:");
                            FileList fl = (FileList) am;
                            fl.getData().stream().forEach(System.out::println);

                            FileWriter writer = new FileWriter("client_storage/server_file_list.txt");
                            for(String list : fl.getData()) {
                                String filename1 = list;
                                writer.write(filename1 + System.getProperty("line.separator"));
                            }
                            writer.close();
                        }
                        else if(am instanceof FileMessage) {
                            System.out.println(">\t\tit file from Server");
                            FileMessage fm = (FileMessage) am;
                            Files.write(Paths.get("client_storage/" + tfFileName), fm.getData(), StandardOpenOption.CREATE);
                        }
                    }
                }
                catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                } finally {
                    Network.stop();
                    System.out.println(">Network stop");
                }
        });
//        t.setDaemon(true);
        t.start();
    }


}
