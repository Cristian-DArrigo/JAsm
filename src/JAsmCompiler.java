import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class JAsmCompiler {
    private final HashMap<String, Integer> etiquetteMap;
    private static final String commentDelimiter = "//";
    public static final String compiledSuffix = "~";

    public static class CompilationError extends Exception {
        public CompilationError(String message) {
            super(message);
        }
    }

    public JAsmCompiler() {
        etiquetteMap = new HashMap<>();
    }

    public void compile(String path) throws IOException, CompilationError {
        File file = new File(path);
        String[] lines = Files.readAllLines(file.toPath()).toArray(new String[0]);
        String[] compiled = new String[lines.length];

        lines = render(lines);
        ParseEtiquette(lines);

        for (int programCounter = 1; programCounter <= lines.length; programCounter++) {
            String line = lines[programCounter - 1];

            String[] args = line.split(" ");
            if (args[0].startsWith("J")) { // Jump instruction
                int etiquetteIndex;
                if (args[0].equals("JMP")) {
                    etiquetteIndex = 1;
                } else if (args[0].equals("JZ") || args[0].equals("JNZ")) {
                    etiquetteIndex = 2;
                } else {
                    etiquetteIndex = 3;
                }
                if (etiquetteMap.containsKey(args[etiquetteIndex])) {
                    line = line.replace(args[etiquetteIndex], String.valueOf(etiquetteMap.get(args[etiquetteIndex])));
                } else {
                    throw new CompilationError("The etiquette '" + args[etiquetteIndex] + " was not defined");
                }
            }
            compiled[programCounter - 1] = line;
        }
        saveCompiledFile(path, compiled);
    }

    private String[] render(String[] lines) {
        // This method removes comment lines, blank lines and excessive whitespaces
        String[] new_lines = new String[lines.length];
        int i = 0;
        int linesSkipped = 0;
        for (String line : lines) {
            if (!(line.startsWith(commentDelimiter) || line.isEmpty() || line.equals("\n"))) {
                if (line.startsWith(" ")) {
                    int j = 0;
                    while (line.charAt(j) == ' ') {
                        j++;
                    }
                    line = line.substring(j);
                }
                new_lines[i++] = line;
            } else {
                linesSkipped++;
            }
        }
        String[] rendered = new String[lines.length - linesSkipped];
        for (int j = 0, k = 0; j < lines.length - linesSkipped; j++) {
            if (new_lines[j] != null) {
                rendered[k++] = new_lines[j];
            }
        }
        return rendered;
    }

    private void saveCompiledFile(String path, String[] compiled) throws IOException {
        File file = new File(path + compiledSuffix);
        if (!file.createNewFile()) {
            System.out.println(path + compiledSuffix + " already exists. Overwriting...");
        }
        FileWriter writer = new FileWriter(file);
        for (String line : compiled) {
            if (line != null) {
                writer.write(line + System.lineSeparator());
            }
        }
        writer.close();
    }

    private void ParseEtiquette(String[] lines) throws CompilationError {
        int lineNumber = 1;
        for (String line : lines) {
            if (line != null) {
                String[] args = line.split(" ");
                if (args[0].contains(":")) {
                    String etiquette = args[0].split(":")[0];
                    if (!etiquetteMap.containsKey(etiquette)) {
                        etiquetteMap.put(etiquette, lineNumber);
                    } else {
                        throw new CompilationError("Etiquette '" + etiquette + "' already defined at line " + etiquetteMap.get(etiquette) + ".");
                    }
                }
                lineNumber++;
            }
        }

    }
}
