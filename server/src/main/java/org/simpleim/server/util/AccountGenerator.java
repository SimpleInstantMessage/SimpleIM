package org.simpleim.server.util;

import com.lambdaworks.codec.Base64;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

public final class AccountGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(Preference.getAmountOfUser());

    private static final SecureRandom RANDOM = new SecureRandom();

    // 禁止实例化
    private AccountGenerator() {
    }

    public static String nextId() {
        return Long.toString(COUNTER.incrementAndGet(), Character.MAX_RADIX) +
                Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
    }

    public static long currentCounter() {
        return COUNTER.get();
    }

    public static String generatePassword() {
        byte[] password = new byte[1024];
        RANDOM.nextBytes(password);
        return String.copyValueOf(Base64.encode(password, false));
    }
}
