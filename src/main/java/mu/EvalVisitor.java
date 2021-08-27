package mu;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EvalVisitor extends MuBaseVisitor<Value> {

    // variables
    private Map<String, Value> memory = new HashMap<>();

    public EvalVisitor() {
        memory.put("VAR@TEST", new Value(15.0));
        memory.put("PAR@TEST2", new Value(10.0));
        memory.put("BAS@TEST3", new Value(3.0));
        memory.put("PAR@TEST4", new Value(5.0));
        memory.put("VAR@VAR_QTD_DIAS_AB_PERM_SEM_IR", new Value(2.0));
        memory.put("VAR@VAR_QTD_DIAS_MES_PROV", new Value(30.0));
        memory.put("BAS@BASE_ABONO_CONSTITUCIONAL", new Value(1000.0));
        memory.put("BAS@BASE_PSSS", new Value(9.0));
        memory.put("VAR@VAR_TETO_INSS", new Value(21.0));
    }

    @Override
    public Value visitTernary(@NotNull MuParser.TernaryContext ctx) {
        Value condition = this.visit(ctx.expr(0));
        return condition.booleanValue() ? this.visit(ctx.expr(1)) : this.visit(ctx.expr(2));
    }

    @Override
    public Value visitIdAtom(MuParser.IdAtomContext ctx) {
        String id = ctx.getText();
        if (memory.containsKey(id)) {
            System.out.println("Obter valor de " + id);
            return memory.get(id);
        }
        return new Value(false);
    }

    @Override
    public Value visitNumberAtom(MuParser.NumberAtomContext ctx) {
        return new Value(Double.valueOf(ctx.getText()));
    }

    // expr overrides
    @Override
    public Value visitParExpr(MuParser.ParExprContext ctx) {
        return this.visit(ctx.expr());
    }

    @Override
    public Value visitUnaryMinusExpr(MuParser.UnaryMinusExprContext ctx) {
        Value value = this.visit(ctx.expr());
        return new Value(value.numberValue().negate());
    }

    @Override
    public Value visitMultiplicationExpr(@NotNull MuParser.MultiplicationExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case MuParser.MULT:
                return new Value(left.numberValue().multiply(right.numberValue()));
            case MuParser.DIV:
                return new Value(left.numberValue().divide(right.numberValue()));
            default:
                throw new RuntimeException("unknown operator: " + MuParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public Value visitAdditiveExpr(@NotNull MuParser.AdditiveExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));
        switch (ctx.op.getType()) {
            case MuParser.PLUS:
                return new Value(left.numberValue().add(right.numberValue()));
            case MuParser.MINUS:
                return new Value(left.numberValue().subtract(right.numberValue()));
            default:
                throw new RuntimeException("unknown operator: " + MuParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public Value visitRelationalExpr(@NotNull MuParser.RelationalExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case MuParser.LT:
                return new Value(left.numberValue().compareTo(right.numberValue()) < 0);
            case MuParser.LTEQ:
                return new Value(left.numberValue().compareTo(right.numberValue()) <= 0);
            case MuParser.GT:
                return new Value(left.numberValue().compareTo(right.numberValue()) > 0);
            case MuParser.GTEQ:
                return new Value(left.numberValue().compareTo(right.numberValue()) >= 0);
            default:
                throw new RuntimeException("unknown operator: " + MuParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public Value visitEqualityExpr(@NotNull MuParser.EqualityExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case MuParser.EQ:
                return new Value(left.numberValue().equals(right.numberValue()));
            case MuParser.NEQ:
                return new Value(!left.numberValue().equals(right.numberValue()));
            default:
                throw new RuntimeException("unknown operator: " + MuParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public Value visitAndExpr(MuParser.AndExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));
        if (left.booleanValue()) { // se o primeiro é verdadeiro, avalia o segundo
            return new Value(right.booleanValue());
        }
        return new Value(false); // se o primeiro é falso, o resultado com certeza é falso
    }

    @Override
    public Value visitOrExpr(MuParser.OrExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        if (left.booleanValue()) { // se o primeiro é verdadeiro, não precisa avaliar o segundo
            return new Value(true);
        }
        return new Value(this.visit(ctx.expr(1)).booleanValue());
    }
}
