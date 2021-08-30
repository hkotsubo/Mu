package br.jus.trt24.folha.ejb.calculo.formula.parser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormulaRubricaEvaluator extends FormulaRubricaBaseVisitor<FormulaRubricaValue> {

    private Map<String, FormulaRubricaValue> cache = new HashMap<>();

    private List<String> variaveisNecessarias = new ArrayList();

    private MathContext mathContext;

    public FormulaRubricaEvaluator() {
        this(MathContext.DECIMAL32);
    }

    public FormulaRubricaEvaluator(MathContext mathContext) {
        this.mathContext = mathContext;
        cache.put("VAR@AUXILIO_TRANSPORTE_MAIOR_65_ANOS", new FormulaRubricaValue(10));
        cache.put("VAR@VAR_REF_INDEN_TRANSP_MES_ANT", new FormulaRubricaValue(10));
        cache.put("VAR@TEM_AUX_TRANSP", new FormulaRubricaValue(0));
        cache.put("VAR@QTDE_DIAS_DESC_AUX_TRANSP", new FormulaRubricaValue(9));
    }

    public Map<String, FormulaRubricaValue> getVars() {
        return this.cache;
    }

    @Override
    public FormulaRubricaValue visitTernary(@NotNull FormulaRubricaParser.TernaryContext ctx) {
        FormulaRubricaValue condition = this.visit(ctx.expr(0));
        return condition.booleanValue() ? this.visit(ctx.expr(1)) : this.visit(ctx.expr(2));
    }

    @Override
    public FormulaRubricaValue visitIdAtom(FormulaRubricaParser.IdAtomContext ctx) {
        String id = ctx.getText();
        if (cache.containsKey(id)) {
            return cache.get(id);
        } else {
            variaveisNecessarias.add(id);
        }
        return new FormulaRubricaValue(false);
    }

    @Override
    public FormulaRubricaValue visitNumberAtom(FormulaRubricaParser.NumberAtomContext ctx) {
        return new FormulaRubricaValue(new BigDecimal(ctx.getText(), this.mathContext));
    }

    // expr overrides
    @Override
    public FormulaRubricaValue visitParExpr(FormulaRubricaParser.ParExprContext ctx) {
        return this.visit(ctx.expr());
    }

    @Override
    public FormulaRubricaValue visitUnaryMinusExpr(FormulaRubricaParser.UnaryMinusExprContext ctx) {
        FormulaRubricaValue value = this.visit(ctx.expr());
        if (ctx.getText().contains("-")) {
            return new FormulaRubricaValue(value.numberValue().negate(this.mathContext));
        }
        return value;
    }

    @Override
    public FormulaRubricaValue visitMultiplicationExpr(@NotNull FormulaRubricaParser.MultiplicationExprContext ctx) {
        FormulaRubricaValue left = this.visit(ctx.expr(0));
        FormulaRubricaValue right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case FormulaRubricaParser.MULT:
                return new FormulaRubricaValue(left.numberValue().multiply(right.numberValue(), this.mathContext));
            case FormulaRubricaParser.DIV:
                return new FormulaRubricaValue(left.numberValue().divide(right.numberValue(), this.mathContext));
            default:
                throw new RuntimeException("unknown operator: " + FormulaRubricaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaRubricaValue visitAdditiveExpr(@NotNull FormulaRubricaParser.AdditiveExprContext ctx) {
        FormulaRubricaValue left = this.visit(ctx.expr(0));
        FormulaRubricaValue right = this.visit(ctx.expr(1));
        switch (ctx.op.getType()) {
            case FormulaRubricaParser.PLUS:
                return new FormulaRubricaValue(left.numberValue().add(right.numberValue(), this.mathContext));
            case FormulaRubricaParser.MINUS:
                return new FormulaRubricaValue(left.numberValue().subtract(right.numberValue(), this.mathContext));
            default:
                throw new RuntimeException("unknown operator: " + FormulaRubricaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaRubricaValue visitRelationalExpr(@NotNull FormulaRubricaParser.RelationalExprContext ctx) {
        FormulaRubricaValue left = this.visit(ctx.expr(0));
        FormulaRubricaValue right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case FormulaRubricaParser.LT:
                return new FormulaRubricaValue(left.numberValue().compareTo(right.numberValue()) < 0);
            case FormulaRubricaParser.LTEQ:
                return new FormulaRubricaValue(left.numberValue().compareTo(right.numberValue()) <= 0);
            case FormulaRubricaParser.GT:
                return new FormulaRubricaValue(left.numberValue().compareTo(right.numberValue()) > 0);
            case FormulaRubricaParser.GTEQ:
                return new FormulaRubricaValue(left.numberValue().compareTo(right.numberValue()) >= 0);
            default:
                throw new RuntimeException("unknown operator: " + FormulaRubricaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaRubricaValue visitEqualityExpr(@NotNull FormulaRubricaParser.EqualityExprContext ctx) {
        FormulaRubricaValue left = this.visit(ctx.expr(0));
        FormulaRubricaValue right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case FormulaRubricaParser.EQ:
                return new FormulaRubricaValue(left.numberValue().equals(right.numberValue()));
            case FormulaRubricaParser.NEQ:
                return new FormulaRubricaValue(!left.numberValue().equals(right.numberValue()));
            default:
                throw new RuntimeException("unknown operator: " + FormulaRubricaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaRubricaValue visitAndExpr(FormulaRubricaParser.AndExprContext ctx) {
        FormulaRubricaValue left = this.visit(ctx.expr(0));
        FormulaRubricaValue right = this.visit(ctx.expr(1));
        if (left.booleanValue()) { // se o primeiro é verdadeiro, avalia o segundo
            return new FormulaRubricaValue(right.booleanValue());
        }
        return new FormulaRubricaValue(false); // se o primeiro é falso, o resultado com certeza é falso
    }

    @Override
    public FormulaRubricaValue visitOrExpr(FormulaRubricaParser.OrExprContext ctx) {
        FormulaRubricaValue left = this.visit(ctx.expr(0));
        if (left.booleanValue()) { // se o primeiro é verdadeiro, não precisa avaliar o segundo
            return new FormulaRubricaValue(true);
        }
        return new FormulaRubricaValue(this.visit(ctx.expr(1)).booleanValue());
    }

    public void setVariavel(String variavel, double valor) {
        variaveisNecessarias.remove(variavel);
        cache.put(variavel, new FormulaRubricaValue(new BigDecimal(valor, this.mathContext)));
    }

    public List<String> getVariaveisNecessarias() {
        return variaveisNecessarias;
    }

    public void setVariaveisNecessarias(List<String> variaveisNecessarias) {
        this.variaveisNecessarias = variaveisNecessarias;
    }
}
