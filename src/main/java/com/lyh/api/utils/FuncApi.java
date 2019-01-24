package com.lyh.api.utils;

public class FuncApi {

  @FunctionalInterface
  public static interface FuncV {
    public void apply();
  }
  @FunctionalInterface
  public static interface FuncArgV<P> {
    public void apply(P p);
  }

  @FunctionalInterface
  public static interface FuncR<R> {
    public R apply();
  }
}
