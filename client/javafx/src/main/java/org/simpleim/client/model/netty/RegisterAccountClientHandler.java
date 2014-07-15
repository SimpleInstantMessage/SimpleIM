package org.simpleim.client.model.netty;

import org.simpleim.common.message.NewAccountOkResponse;
import org.simpleim.common.message.NewAccountRequest;
import org.simpleim.common.message.Request;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class RegisterAccountClientHandler extends ChannelHandlerAdapter {

    private static final Logger LOGGER = Logger
            .getLogger(RegisterAccountClientHandler.class.getName());

    private static final Request NEW_ACCOUNT_REQUEST = new NewAccountRequest();

    private NewAccountOkResponse response;

    public NewAccountOkResponse getOkResponse() {
        return response;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(NEW_ACCOUNT_REQUEST);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = (NewAccountOkResponse) msg;
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // TODO inform user the exception
        LOGGER.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        // Close the connection when an exception is raised.
        ctx.close();
    }
}
