package io.github.ludongrong.netftp.carry;

@FunctionalInterface
public interface Consumer<T1, T2, R> {

    boolean apply(T1 t1, T2 t2);
}
