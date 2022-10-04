import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, JAsmCompiler.CompilationError {
        JAsmVM vm = new JAsmVM();
        vm.run("JAsmExamples/nested_loop.jasm");
        // vm.showRegisters();
        // vm.showStack();
        // JAsmCompiler compiler = new JAsmCompiler();
        // compiler.compile("JAsmExamples/nested_loop.jasm");
    }
}