package br.jus.trt24.folha.ejb.calculo.formula.parser;

import br.jus.trt24.rhino.javascript.RhinoScriptEngine;
import java.util.Map;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String[]{"src/main/mu/test.mu"};
        }
        String formula = "VAR@AUXILIO_TRANSPORTE_MAIOR_65_ANOS == 0 ? 10 : VAR@VAR_REF_INDEN_TRANSP_MES_ANT > 0 ? 20 : VAR@TEM_AUX_TRANSP == 0 ? 30 : 22 - VAR@QTDE_DIAS_DESC_AUX_TRANSP";
        formula = "1 + 2 > 4 ? 5 : 3 - 7 < 0 ? 2 * -7 : 3 * 13";

        //FormulaRubricaLexer lexer = new FormulaRubricaLexer(new ANTLRFileStream(args[0]));
        FormulaRubricaLexer lexer = new FormulaRubricaLexer(new ANTLRInputStream(formula));
        FormulaRubricaParser parser = new FormulaRubricaParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        System.out.println(tree.toStringTree(parser));
        FormulaRubricaEvaluator evaluator = new FormulaRubricaEvaluator();
        FormulaRubricaValue resultEval = evaluator.visit(tree);
        System.out.println("result=" + resultEval);

        // comparar com a engine atual
        RhinoScriptEngine engine = new RhinoScriptEngine();
        Map<String, FormulaRubricaValue> vars = evaluator.getVars();
        for (Map.Entry<String, FormulaRubricaValue> e : vars.entrySet()) {
            formula = formula.replaceAll(e.getKey(), e.getValue().toString());
        }
        Object resultRhino = engine.eval(formula);
        System.out.println("result=" + resultRhino);

        // compara resultados considerando uma margem de erro "pequena o suficiente"
        System.out.println(Math.abs(((Double) resultRhino) - (resultEval.numberValue().doubleValue())) < 0.00001);
    }
}
