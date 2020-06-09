package com.netty.demo.handler.client;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeClientHandler extends SimpleChannelInboundHandler {
    private final ByteBuf byteBuf;

    public TimeClientHandler(String msg){
        byte[] msgBytes = msg.getBytes();
        this.byteBuf = Unpooled.buffer(msgBytes.length);
        byteBuf.writeBytes(msgBytes);
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf receivedBuf = (ByteBuf) msg;
        byte[] bytes = new byte[receivedBuf.readableBytes()];
        receivedBuf.readBytes(bytes);
        String body = new String(bytes,"UTF-8");
        Integer integerBody = Integer.valueOf(body);
        log.info("received msg from server, content=[{}]",++integerBody);
        String concat = String.valueOf(integerBody);
        Thread.sleep(1000);
        ctx.writeAndFlush(Unpooled.copiedBuffer(concat.getBytes()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Unexpected exception from downstream: {}", cause.getMessage());
        ctx.close();
    }
}
