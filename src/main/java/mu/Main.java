package mu;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String[]{"src/main/mu/test.mu"};
        }

        System.out.println("parsing: " + args[0]);

        MuLexer lexer = new MuLexer(new ANTLRFileStream(args[0]));
        MuParser parser = new MuParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        System.out.println(tree.toStringTree(parser));
        EvalVisitor visitor = new EvalVisitor();
        System.out.println("result=" + visitor.visit(tree));
    }
}
