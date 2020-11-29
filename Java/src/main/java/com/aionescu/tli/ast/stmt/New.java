package com.aionescu.tli.ast.stmt;

import com.aionescu.tli.ast.Ident;
import com.aionescu.tli.ast.expr.Expr;
import com.aionescu.tli.ast.prog.ThreadState;
import com.aionescu.tli.ast.type.TRef;
import com.aionescu.tli.ast.type.varinfo.VarInfo;
import com.aionescu.tli.ast.type.varinfo.VarState;
import com.aionescu.tli.ast.val.VRef;
import com.aionescu.tli.exn.eval.OutOfMemoryException;
import com.aionescu.tli.exn.typeck.UndeclaredVariableException;
import com.aionescu.tli.utils.data.map.Map;

public final class New implements Stmt {
  private final Ident _ident;
  private final Expr _expr;

  public New(Ident ident, Expr expr) {
    _ident = ident;
    _expr = expr;
  }

  @Override
  public String toString() {
    return String.format("%s = new %s", _ident, _expr);
  }

  @Override
  public Map<Ident, VarInfo> typeCheck(Map<Ident, VarInfo> sym) {
    var t = _expr.typeCheck(sym);
    var typ = sym.lookup(_ident).match(
      () -> { throw new UndeclaredVariableException(_ident); },
      a -> a.type);

    typ.mustBe(new TRef(t));
    return sym.insert(_ident, new VarInfo(typ, VarState.INIT));
  }

  @Override
  public ThreadState eval(ThreadState prog) {
    var global = prog.global.get();

    if (global.gcStats.crrHeapSize == global.gcStats.maxHeapSize)
      throw new OutOfMemoryException(global.gcStats.maxHeapSize);

    var v = _expr.eval(global.heap, prog.sym);
    var sym = prog.sym.insert(_ident, new VRef(global.gcStats.crrHeapSize));
    var heap = global.heap.insert(global.gcStats.crrHeapSize, v);

    prog.global.getAndUpdate(g ->
      g.withGCStats(
        g.gcStats
          .withAllocsSinceGC(g.gcStats.allocsSinceGC + 1)
          .withCrrHeapSize(g.gcStats.crrHeapSize + 1)));

    prog.global.getAndUpdate(g -> g.withHeap(heap));
    return prog.withSym(sym);
  }
}
