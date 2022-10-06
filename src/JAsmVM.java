import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class JAsmVM {
    private static final int REGISTERS_NUMBER = 10;
    private static final int DEFAULT_VALUE = 0;

    private final Stack stack;
    private final JAsmInterpreter interpreter;
    private final JAsmCompiler compiler;
    private HashMap<String, Integer> registers;
    private final String[] registersSet = {"A", "B", "C", "D", "E", "Z"};


    public JAsmVM() {
        stack = new Stack();
        initRegisters();
        interpreter = new JAsmInterpreter(this);
        compiler = new JAsmCompiler();
    }

    public void run(String path) throws IOException, JAsmCompiler.CompilationError, JAsmInterpreter.SyntaxError {
        compiler.compile(path);
        path = path + JAsmCompiler.compiledSuffix;
        interpreter.execute(JAsmIO.OpenFile(path));
    }

    private void initRegisters() {
        registers = new HashMap<>();
        Arrays.stream(registersSet)
                .forEach(reg -> {
                    for (int i = 0; i < REGISTERS_NUMBER; i++) {
                        registers.put(reg + i, DEFAULT_VALUE);
                    }
                });
    }

    public boolean isValidRegister(String register) {
        return registers.containsKey(register);
    }

    public boolean areSameType(String register1, String register2) {
        return register1.charAt(0) == register2.charAt(0);
    }

    public boolean comesFirst(String register1, String register2) {
        return register1.charAt(1) < register2.charAt(1);
    }

    public int get(String register) {
        return registers.get(register);
    }

    public void put(String reg, int value) {
        registers.put(reg, value);
    }

    public static int DEFAULT_VALUE() {
        return DEFAULT_VALUE;
    }

    public void showRegisters() {
        // show registers in a table like format
        System.out.println("----------------- Registers -----------------");
        for (int i = 0; i < REGISTERS_NUMBER; i++) {
            int j = i;
            Arrays.stream(registersSet)
                    .forEach(reg -> System.out.print(reg + j + ": " + registers.get(reg + j) + "\t"));
            System.out.println();
        }
        System.out.println("-------------------- END --------------------");
    }

    public void showStack() {
        // show stack in a table like format
        System.out.println("------------------- Stack -------------------");
        stack.show();
        System.out.println("-------------------- END --------------------");
    }
}
