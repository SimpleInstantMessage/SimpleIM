package org.simpleim.server.server;

import com.lambdaworks.crypto.SCryptUtil;

import org.simpleim.common.message.FailureResponse;
import org.simpleim.common.message.LoginFailureResponse;
import org.simpleim.common.message.LoginFailureResponse.Cause;
import org.simpleim.common.message.LoginNotification;
import org.simpleim.common.message.LoginOkResponse;
import org.simpleim.common.message.LoginRequest;
import org.simpleim.common.message.LogoutNotification;
import org.simpleim.common.message.LogoutRequest;
import org.simpleim.common.message.Message;
import org.simpleim.common.message.NewAccountOkResponse;
import org.simpleim.common.message.NewAccountRequest;
import org.simpleim.common.message.ReceiveMessageNotification;
import org.simpleim.common.message.SendMessageRequest;
import org.simpleim.common.message.UpdateFinishedNotification;
import org.simpleim.common.message.sendMessageFinishedNotification;
import org.simpleim.server.database.DataBase;
import org.simpleim.server.util.AccountGenerator;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());

    private static final ConcurrentHashMap<String, ChannelHandlerContext> ONLINE_USERS
            = new ConcurrentHashMap<>();

    private String UserId = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean closeNow = true;
        Message message = null;
        if (msg instanceof NewAccountRequest) {
            // ------------------------------ 用户注册部分 返回用户注册信息 ------------------------------
            closeNow = true;
            String id = AccountGenerator.nextId();
            String password = AccountGenerator.generatePassword();
            message = new NewAccountOkResponse()
                    .setId(id)
                    .setPassword(password);
            String hashedPassword = SCryptUtil.scrypt(password, 1 << 7, 8, 1);
            DataBase.InsertNumberRow(id, hashedPassword);
        } else if (msg instanceof LoginRequest) {
            // ------------------------------ 登录部分 ------------------------------
            LoginRequest request = (LoginRequest) msg;
            String id = request.getId();
            String password = request.getPassword();
            String correctHashedPassword = DataBase.selectNumerRow(id);
            if (correctHashedPassword == null) {
                message = new LoginFailureResponse().setCause(Cause.ID_NOT_FOUND);
                closeNow = true;
            } else if (SCryptUtil.check(password, correctHashedPassword)) {
                message = new LoginOkResponse()
                        .setOnlineUsersIds(ONLINE_USERS.keySet().toArray(new String[0]));
                UserId = id;
                closeNow = false;
            } else {
                message = new LoginFailureResponse().setCause(Cause.PASSWORD_INCORRECT);
                closeNow = true;
            }
        } else if (msg instanceof UpdateFinishedNotification) {
            //防止用户掉过第一步验证，直接发送UpdateFinishedNotification
            if (UserId == null) {
                closeNow = true;
                message = new LoginFailureResponse().setCause(Cause.IILEGAL_LOGIN);
            } else {
                closeNow = false;
                message = new LoginNotification().setNewUserId(UserId);
                final Collection<ChannelHandlerContext> channels = ONLINE_USERS.values();
                for (ChannelHandlerContext aChannel : channels) {
                    aChannel.writeAndFlush(message);
                }
                ONLINE_USERS.put(UserId, ctx);
            }
        } else if (msg instanceof SendMessageRequest) {
            // ------------------------------ 转发部分 ------------------------------
            closeNow = false;
            SendMessageRequest sendRequest = (SendMessageRequest) msg;
            // 消息接收方
            String[] targetsIds = sendRequest.getTargetsIds();
            // 转发的消息内容
            message = new ReceiveMessageNotification().setMessage(sendRequest.getMessage())
                    .setSender(sendRequest.getSender());
            // 消息转发
            for (String targetId : targetsIds) {
                ONLINE_USERS.get(targetId).writeAndFlush(message);
            }
            // 设置转发完成确认
            message = new sendMessageFinishedNotification();
        } else if (msg instanceof LogoutRequest) {
            // ------------------------------ 登出部分 ------------------------------
            closeNow = true;
            ONLINE_USERS.remove(UserId);
            message = new LogoutNotification().setUserLoggedOutId(UserId);
            final Collection<ChannelHandlerContext> channels = ONLINE_USERS.values();
            for (ChannelHandlerContext aChannel : channels) {
                aChannel.writeAndFlush(message);
            }
        } else {
            // ------------------------------  ------------------------------
            closeNow = true;
            message = new FailureResponse();
        }
        ChannelFuture f = ctx.writeAndFlush(message);

        if (closeNow) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.gc();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        Message message = null;
        ONLINE_USERS.remove(UserId);
        message = new LogoutNotification().setUserLoggedOutId(UserId);
        final Collection<ChannelHandlerContext> channels = ONLINE_USERS.values();
        for (ChannelHandlerContext aChannel : channels) {
            aChannel.writeAndFlush(message);
        }
        LOGGER.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
