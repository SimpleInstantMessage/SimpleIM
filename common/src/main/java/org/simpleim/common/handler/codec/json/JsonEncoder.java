package org.simpleim.common.handler.codec.json;

import org.simpleim.common.message.Message;

import java.util.List;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class JsonEncoder extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out)
            throws Exception {
        out.add(JsonDecoder.GSON.toJson(msg));
    }

}
