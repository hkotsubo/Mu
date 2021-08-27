package benchmarks;

import br.jus.trt24.folha.ejb.calculo.formula.parser.FormulaRubricaEvaluator;
import br.jus.trt24.folha.ejb.calculo.formula.parser.FormulaRubricaLexer;
import br.jus.trt24.folha.ejb.calculo.formula.parser.FormulaRubricaParser;
import br.jus.trt24.rhino.javascript.RhinoScriptEngine;
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
public class CalculoSemVariaveisBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(CalculoSemVariaveisBenchmark.class.getSimpleName()).warmupIterations(2).measurementIterations(2).forks(2).build();
        new Runner(opt).run();
    }

    @Param({"1 + 2 > 4 ? 5 : 3 - 7 < 0 ? 2 * -7 : 3 * 13"})
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
        return Double.parseDouble(String.valueOf(engine.eval(formula)));
    }
}
