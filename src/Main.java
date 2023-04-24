import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, JAsmCompiler.CompilationError, JAsmInterpreter.SyntaxError {
        JAsmVM vm = new JAsmVM();
        vm.run("JAsmExamples/test.jasm");
    }
}