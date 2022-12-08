package java.lang.ref;

import java.util.concurrent.ThreadFactory;

public class Cleaner {
    private static interface Cleanable {
        public void clean();
    }
    public static Cleaner create() {
        return null;
    }
    public static Cleaner create(ThreadFactory factory) {
        return null;
    }
    public Cleanable register (Object o, Runnable r) {
        return null;
    }
}
