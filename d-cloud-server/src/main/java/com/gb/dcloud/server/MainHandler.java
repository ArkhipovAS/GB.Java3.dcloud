package com.gb.dcloud.server;

import com.gb.dcloud.common.FileList;
import com.gb.dcloud.common.FileMessage;
import com.gb.dcloud.common.FileRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (fr.getFilename().equals("/dir")){
                    System.out.println(">Receive command from Client: " + fr.getFilename());
                }
                else{
                    System.out.println(">Receive request exist file from Client: " + fr.getFilename());
                }
                File dir = new File("server_storage");

                if (fr.getFilename().equals("/dir")){
                    if(dir.isDirectory())
                    {
                        // получаем все вложенные объекты в каталоге
                        List<String> lfl = new ArrayList<String>();
                        for(File item : dir.listFiles()){
                            if(item.isDirectory()){
                                lfl.add(item.getName() + "\t - folder");
                            }
                            else{
                                lfl.add("\t" + item.getName() + " - file");
                            }
                        }
                        lfl.stream().forEach(System.out::println);
                        FileList fl = new FileList(lfl);
                        ctx.writeAndFlush(fl);
                        System.out.println(">File list send to client");
                    }
                }
                else if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                    System.out.println(">Send to Client file: " + fr.getFilename());
                }

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    class LinkedFileList<T>
    {
        T data;
        LinkedFileList<T> next;
        LinkedFileList<T> previous;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
