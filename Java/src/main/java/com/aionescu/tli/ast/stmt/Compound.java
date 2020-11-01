package com.aionescu.tli.ast.stmt;

import com.aionescu.tli.ast.Ident;
import com.aionescu.tli.ast.prog.ProgState;
import com.aionescu.tli.ast.type.VarInfo;
import com.aionescu.tli.utils.collections.map.Map;

public final class Compound implements Stmt {
  private final Stmt _stmt1, _stmt2;

  public Compound(Stmt stmt1, Stmt stmt2) {
    _stmt1 = stmt1;
    _stmt2 = stmt2;
  }

  @Override
  public String toString() {
    return String.format("%s; %s", _stmt1, _stmt2);
  }

  @Override
  public Map<Ident, VarInfo> typeCheck(Map<Ident, VarInfo> sym) {
    return _stmt2.typeCheck(_stmt1.typeCheck(sym));
  }

  @Override
  public ProgState eval(ProgState prog) {
    return prog.withToDo(prog.toDo.push(_stmt2).push(_stmt1));
  }
}