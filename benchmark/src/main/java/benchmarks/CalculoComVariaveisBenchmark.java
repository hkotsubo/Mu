package benchmarks;

import br.jus.trt24.folha.ejb.calculo.formula.parser.FormulaRubricaEvaluator;
import br.jus.trt24.folha.ejb.calculo.formula.parser.FormulaRubricaLexer;
import br.jus.trt24.folha.ejb.calculo.formula.parser.FormulaRubricaParser;
import br.jus.trt24.folha.ejb.calculo.formula.parser.FormulaRubricaValue;
import br.jus.trt24.rhino.javascript.RhinoScriptEngine;
import java.util.Map;
import javax.script.ScriptException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class CalculoComVariaveisBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(CalculoComVariaveisBenchmark.class.getSimpleName()).warmupIterations(2).measurementIterations(2).forks(2).build();
        new Runner(opt).run();
    }

    @Param({"VAR@AUXILIO_TRANSPORTE_MAIOR_65_ANOS == 0 ? 10 : VAR@VAR_REF_INDEN_TRANSP_MES_ANT > 0 ? 20 : VAR@TEM_AUX_TRANSP == 0 ? 30 : 22 - VAR@QTDE_DIAS_DESC_AUX_TRANSP"})
    String formula;

    FormulaRubricaEvaluator evaluator;
    RhinoScriptEngine engine;

    @Setup
    public void setup() {
        evaluator = new FormulaRubricaEvaluator();
        engine = new RhinoScriptEngine();
    }

    @Benchmark
    public void testAntlr() {
        antlr();
    }

    @Benchmark
    public void testEngine() throws ScriptException {
        engine();
    }

    private double antlr() {
        FormulaRubricaLexer lexer = new FormulaRubricaLexer(new ANTLRInputStream(formula));
        FormulaRubricaParser parser = new FormulaRubricaParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        return evaluator.visit(tree).numberValue().doubleValue();
    }

    private double engine() throws ScriptException {
        Map<String, FormulaRubricaValue> vars = evaluator.getVars();
        for (Map.Entry<String, FormulaRubricaValue> e : vars.entrySet()) {
            formula = formula.replaceAll(e.getKey(), e.getValue().toString());
        }
        return Double.parseDouble(String.valueOf(engine.eval(formula)));
    }
}
