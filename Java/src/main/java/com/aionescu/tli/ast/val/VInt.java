package com.aionescu.tli.ast.val;

import com.aionescu.tli.exn.eval.DidYouRunTheTypeCheckerException;

public final class VInt implements Val {
  public final int val;

  public VInt(int val) {
    this.val = val;
  }

  @Override
  public String toString() {
    return String.valueOf(val);
  }

  @Override
  public int compareTo(Val rhs) {
    if (!(rhs instanceof VInt))
      throw new DidYouRunTheTypeCheckerException();

    return Integer.compare(val, ((VInt)rhs).val);
  }
}