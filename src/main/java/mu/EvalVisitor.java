package mu;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EvalVisitor extends MuBaseVisitor<Value> {

    // used to compare floating point numbers
    public static final double SMALL_VALUE = 0.00000000001;

    // variables
    private Map<String, Value> memory = new HashMap<>();

    public EvalVisitor() {
        memory.put("VAR@TEST", new Value(15.0));
        memory.put("PAR@TEST2", new Value(10.0));
        memory.put("BAS@TEST3", new Value(3.0));
        memory.put("PAR@TEST4", new Value(5.0));
    }

    @Override
    public Value visitTernary(@NotNull MuParser.TernaryContext ctx) {
        Value condition = this.visit(ctx.expr(0));
        return condition.asBoolean() ? this.visit(ctx.expr(1)) : this.visit(ctx.expr(2));
    }

    @Override
    public Value visitIdAtom(MuParser.IdAtomContext ctx) {
        String id = ctx.getText();
        if (memory.containsKey(id)) {
            System.out.println("Obter valor de " + id);
            return memory.get(id);
        }
        return Value.VOID;
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
        return new Value(-value.asDouble());
    }

    @Override
    public Value visitMultiplicationExpr(@NotNull MuParser.MultiplicationExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case MuParser.MULT:
                return new Value(left.asDouble() * right.asDouble());
            case MuParser.DIV:
                return new Value(left.asDouble() / right.asDouble());
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
                System.out.println("add " + left + " and " + right);
                return left.isDouble() && right.isDouble()
                        ? new Value(left.asDouble() + right.asDouble())
                        : new Value(left.asString() + right.asString());
            case MuParser.MINUS:
                return new Value(left.asDouble() - right.asDouble());
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
                return new Value(left.asDouble() < right.asDouble());
            case MuParser.LTEQ:
                return new Value(left.asDouble() <= right.asDouble());
            case MuParser.GT:
                return new Value(left.asDouble() > right.asDouble());
            case MuParser.GTEQ:
                return new Value(left.asDouble() >= right.asDouble());
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
                return left.isDouble() && right.isDouble()
                        ? new Value(Math.abs(left.asDouble() - right.asDouble()) < SMALL_VALUE)
                        : new Value(left.equals(right));
            case MuParser.NEQ:
                return left.isDouble() && right.isDouble()
                        ? new Value(Math.abs(left.asDouble() - right.asDouble()) >= SMALL_VALUE)
                        : new Value(!left.equals(right));
            default:
                throw new RuntimeException("unknown operator: " + MuParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public Value visitAndExpr(MuParser.AndExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));
        if (left.asBoolean()) // se o primeiro é verdadeiro, avalia o segundo
        {
            return new Value(right.asBoolean());
        }
        return new Value(false); // se o primeiro é falso, o resultado com certeza é falso
    }

    @Override
    public Value visitOrExpr(MuParser.OrExprContext ctx) {
        Value left = this.visit(ctx.expr(0));
        if (left.asBoolean()) { // se o primeiro é verdadeiro, não precisa avaliar o segundo
            return new Value(true);
        }
        return new Value(this.visit(ctx.expr(1)).asBoolean());
    }
}
