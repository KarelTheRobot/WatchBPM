package sun.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

public class Unsafe {
    public native int addressSize();
    public native Object allocateInstance(Class cls) throws InstantiationException;
    public native long allocateMemory(long bytes);
    public native int arrayBaseOffset(Class arrayClass);
    public native int arrayIndexScale(Class arrayClass);
    public final native boolean compareAndSwapInt(Object o,
                                                  long offset,
                                                  int expected,
                                                  int x);
    public final native boolean compareAndSwapLong(Object o,
                                                   long offset,
                                                   long expected,
                                                   long x);
    public final native boolean compareAndSwapObject(Object o,
                                                     long offset,
                                                     Object expected,
                                                     Object x);
    public  void copyMemory(long srcAddress,
                            long destAddress,
                            long bytes) {
    }
    public native  void copyMemory(Object srcBase,
                                   long srcOffset,
                                   Object destBase,
                                   long destOffset,
                                   long bytes);

    public native Class defineAnonymousClass(Class hostClass,
                                             byte[] data,
                                             Object[] cpPatches);
    public native Class defineClass(String name,
                                    byte[] b,
                                    int off,
                                    int len);
    public native Class defineClass(String name,
                                    byte[] b,
                                    int off,
                                    int len,
                                    ClassLoader loader,
                                    ProtectionDomain protectionDomain);
    public native  void ensureClassInitialized(Class c);
    public int fieldOffset(Field f) {
        if (Modifier.isStatic(f.getModifiers()))
            return (int) staticFieldOffset(f);
        else
            return (int) objectFieldOffset(f);
    }
    public native  void freeMemory(long address);
    public native long getAddress(long address);
    public native boolean getBoolean(Object o,
                                     long offset);
    public boolean getBoolean(Object o,
                              int offset) {
        return false;
    }
    public native boolean getBooleanVolatile(Object o,
                                             long offset);
    public native byte getByte(long address);
    public native byte getByte(Object o,
                               long offset);
    public byte getByte(Object o,
                        int offset) {
        return 0;
    }
    public native byte getByteVolatile(Object o,
                                       long offset);
    public native char getChar(long address);
    public native char getChar(Object o,
                               long offset);
    public char getChar(Object o,
                        int offset) {
        return '\0';
    }
    public native char getCharVolatile(Object o,
                                       long offset);
    public native double getDouble(long address);
    public native double getDouble(Object o,
                                   long offset);
    public double getDouble(Object o,
                            int offset) {
        return 0;
    }
    public native double getDoubleVolatile(Object o,
                                           long offset);
    public native float getFloat(long address);
    public native float getFloat(Object o,
                                 long offset);
    public float getFloat(Object o,
                          int offset) {
        return 0;
    }
    public native float getFloatVolatile(Object o,
                                         long offset);
    public native int getInt(long address);
    public native int getInt(Object o,
                             long offset);
    public int getInt(Object o,
                      int offset) {
        return 0;
    }
    public native int getIntVolatile(Object o,
                                     long offset);
    public native int getLoadAverage(double[] loadavg,
                                     int nelems);
    public native long getLong(long address);
    public native long getLong(Object o,
                               long offset);
    public long getLong(Object o,
                        int offset) {
        return 0;
    }
    public native long getLongVolatile(Object o,
                                       long offset);
    public native Object getObject(Object o,
                                   long offset);
    public Object getObject(Object o,
                            int offset) {
        return null;
    }
    public native Object getObjectVolatile(Object o,
                                           long offset);
    public native short getShort(long address);
    public native short getShort(Object o,
                                 long offset);
    public short getShort(Object o,
                          int offset) {
        return 0;
    }
    public native short getShortVolatile(Object o,
                                         long offset);
    public static Unsafe getUnsafe() {
        return null;
    }
    public native  void monitorEnter(Object o);
    public native  void monitorExit(Object o);
    public native long objectFieldOffset(Field f);
    public native int pageSize();
    public native  void park(boolean isAbsolute,
                             long time);
    public native  void putAddress(long address,
                                   long x);
    public native  void putBoolean(Object o,
                                   long offset,
                                   boolean x);
    public  void putBoolean(Object o,
                            int offset,
                            boolean x) {

    }
    public native  void putBooleanVolatile(Object o,
                                           long offset,
                                           boolean x);
    public native  void putByte(long address,
                                byte x);
    public native  void putByte(Object o,
                                long offset,
                                byte x);
    public  void putByte(Object o,
                         int offset,
                         byte x) {

    }
    public native  void putByteVolatile(Object o,
                                        long offset,
                                        byte x);
    public native  void putChar(long address,
                                char x);
    public native  void putChar(Object o,
                                long offset,
                                char x);
    public  void putChar(Object o,
                         int offset,
                         char x) {

    }
    public native  void putCharVolatile(Object o,
                                        long offset,
                                        char x);
    public native  void putDouble(long address,
                                  double x);
    public native  void putDouble(Object o,
                                  long offset,
                                  double x);
    public  void putDouble(Object o,
                           int offset,
                           double x) {

    }
    public native  void putDoubleVolatile(Object o,
                                          long offset,
                                          double x);
    public native  void putFloat(long address,
                                 float x);
    public native  void putFloat(Object o,
                                 long offset,
                                 float x);
    public  void putFloat(Object o,
                          int offset,
                          float x) {

    }
    public native  void putFloatVolatile(Object o,
                                         long offset,
                                         float x);
    public native  void putInt(long address,
                               int x);
    public native  void putInt(Object o,
                               long offset,
                               int x);
    public  void putInt(Object o,
                        int offset,
                        int x) {

    }
    public native  void putIntVolatile(Object o,
                                       long offset,
                                       int x);
    public native  void putLong(long address,
                                long x);
    public native  void putLong(Object o,
                                long offset,
                                long x);
    public  void putLong(Object o,
                         int offset,
                         long x) {

    }
    public native  void putLongVolatile(Object o,
                                        long offset,
                                        long x);
    public native  void putObject(Object o,
                                  long offset,
                                  Object x);
    public  void putObject(Object o,
                           int offset,
                           Object x) {

    }
    public native  void putObjectVolatile(Object o,
                                          long offset,
                                          Object x);
    public native  void putOrderedInt(Object o,
                                      long offset,
                                      int x);
    public native  void putOrderedLong(Object o,
                                       long offset,
                                       long x);
    public native  void putOrderedObject(Object o,
                                         long offset,
                                         Object x);
    public native  void putShort(long address,
                                 short x);
    public native  void putShort(Object o,
                                 long offset,
                                 short x);
    public  void putShort(Object o,
                          int offset,
                          short x) {

    }
    public native  void putShortVolatile(Object o,
                                         long offset,
                                         short x);
    public native long reallocateMemory(long address,
                                        long bytes);
    public  void setMemory(long address,
                           long bytes,
                           byte value) {

    }
    public native  void setMemory(Object o,
                                  long offset,
                                  long bytes,
                                  byte value);
    public Object staticFieldBase(Class c) {
        return null;
    }
    public native Object staticFieldBase(Field f);
    public native long staticFieldOffset(Field f);
    public native  void throwException(Throwable ee);
    public native boolean tryMonitorEnter(Object o);
    public native  void unpark(Object thread);
}
