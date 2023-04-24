public class JAsmInterpreter {
    public static class SyntaxError extends Exception {
        public SyntaxError(String message) {
            super(message);
        }
    }

    private final JAsmVM vm;
    private int programCounter;
    private static final String commentDelimiter = "//";

    public JAsmInterpreter(JAsmVM vm) {
        this.vm = vm;
    }

    public void executeLine(String line) throws SyntaxError {
        // we here need to understand how the line is structured and then execute it appropriately
        if (line.contains(commentDelimiter)) {
            executeLine(line.substring(0, line.indexOf(commentDelimiter)));
        }

        String[] args = line.split(" ");
        // if the first argument is an etiquette (has ":" after a word) we dont need to do anything as the compiler
        // has already replaced the etiquette with the line numbers, so we just skip this line if it was the label only,
        // otherwise we execute the instruction
        if (args[0].contains(":")) {
            if (args.length == 1) {
                return;
            }
            line = line.substring(line.indexOf(":") + 2); // to remove the etiquette and the space after it
        }

        args = line.split(" ");
        String instruction = args[0];
        switch (instruction) {
            case "PUT" -> this.put(args[1], args[2]);
            case "ADD" -> this.add(args[1], args[2], args[3]);
            case "SUB" -> this.sub(args[1], args[2], args[3]);
            case "MUL" -> this.mul(args[1], args[2], args[3]);
            case "DIV" -> this.div(args[1], args[2], args[3]);
            case "MOD" -> this.mod(args[1], args[2], args[3]);
            case "INC" -> this.inc(args[1]);
            case "DEC" -> this.dec(args[1]);
            case "FREE" -> this.free(args[1]);
            case "SWAP" -> this.swap(args[1], args[2]);
            case "COPY" -> this.copy(args[1], args[2]);

            case "JMP" -> this.jmp(args[1]);
            case "JZ" -> this.jz(args[1], args[2]); // if the first argument is 0, jump to the second argument
            case "JNZ" -> this.jnz(args[1], args[2]); // if the first argument is not 0, jump to the second argument
            case "JE" ->
                    this.je(args[1], args[2], args[3]); // if the first argument is equal to the second argument, jump to the third argument
            case "JNE" ->
                    this.jne(args[1], args[2], args[3]); // if the first argument is not equal to the second argument, jump to the third argument
            case "JG" ->
                    this.jg(args[1], args[2], args[3]); // if the first argument is greater than the second argument, jump to the third argument
            case "JL" ->
                    this.jl(args[1], args[2], args[3]); // if the first argument is less than the second argument, jump to the third argument
            case "JGE" ->
                    this.jge(args[1], args[2], args[3]); // if the first argument is greater or equal to the second argument, jump to the third argument
            case "JLE" ->
                    this.jle(args[1], args[2], args[3]); // if the first argument is less or equal to the second argument, jump to the third argument

            case "ITER_THROUGH" ->
                    this.iterThrough(args); // e.g. ITER_THROUGH |X0:X9 | -> Z0 ( ... ), the iteration will start from register X0 to X9 and will
            // put the value of the current register in Z0, then execute the instructions between the parentheses
            case "ITER_FOR" ->
                    this.iterFor(args); // e.g. ITER_FOR |dest_reg:n| ( ... ), the iteration will start from 0 to n and will put the value of the current
            // iteration in dest_reg, then execute the instructions between the parentheses

            case "_SHOW" -> this._show(args[1]); // show the value of the argument
            case "_ASCII" -> this._ascii(args[1]); // show the ASCII character of the argument
            case "_HEX" -> this._hex(args[1]); // show the hexadecimal value of the argument
            case "_CLS" -> this._cls(); // clear the screen
            case "_NEWL" -> this._newl(); // print a new line
            case "_TAB" -> this._tab(); // print a tab

            default -> throw new SyntaxError("Unexpected instruction value: '" + instruction + "'");
        }
    }

    private void put(String dest, String src) {
        if (vm.isValidRegister(dest)) {
            if (vm.isValidRegister(src)) {
                vm.put(dest, vm.get(src));
            } else {
                try {
                    vm.put(dest, Integer.parseInt(src));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid argument: " + src + " at line " + programCounter);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + dest + " at line " + programCounter);
        }
    }

    private void add(String dest, String addend1, String addend2) {
        // we need to understand if the two addends are registers or numbers
        // if they are registers we need to get their values and then add them
        // if they are numbers we need to parse them to integers and then add them

        if (vm.isValidRegister(dest)) {
            try {
                if (vm.isValidRegister(addend1) && vm.isValidRegister(addend2)) {
                    vm.put(dest, vm.get(addend1) + vm.get(addend2));
                } else if (vm.isValidRegister(addend1) && !vm.isValidRegister(addend2)) {
                    vm.put(dest, vm.get(addend1) + Integer.parseInt(addend2));
                } else if (!vm.isValidRegister(addend1) && vm.isValidRegister(addend2)) {
                    vm.put(dest, Integer.parseInt(addend1) + vm.get(addend2));
                } else {
                    vm.put(dest, Integer.parseInt(addend1) + Integer.parseInt(addend2));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + dest + "at line " + programCounter);
        }
    }

    private void sub(String dest, String arg1, String arg2) {
        if (vm.isValidRegister(dest)) {
            try {
                if (vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    vm.put(dest, vm.get(arg1) - vm.get(arg2));
                } else if (vm.isValidRegister(arg1) && !vm.isValidRegister(arg2)) {
                    vm.put(dest, vm.get(arg1) - Integer.parseInt(arg2));
                } else if (!vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    vm.put(dest, Integer.parseInt(arg1) - vm.get(arg2));
                } else {
                    vm.put(dest, Integer.parseInt(arg1) - Integer.parseInt(arg2));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + dest + "at line " + programCounter);
        }
    }

    private void mul(String dest, String arg1, String arg2) {
        if (vm.isValidRegister(dest)) {
            try {
                if (vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    vm.put(dest, vm.get(arg1) * vm.get(arg2));
                } else if (vm.isValidRegister(arg1) && !vm.isValidRegister(arg2)) {
                    vm.put(dest, vm.get(arg1) * Integer.parseInt(arg2));
                } else if (!vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    vm.put(dest, Integer.parseInt(arg1) * vm.get(arg2));
                } else {
                    vm.put(dest, Integer.parseInt(arg1) * Integer.parseInt(arg2));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + dest + "at line " + programCounter);
        }
    }

    private void div(String dest, String arg1, String arg2) {
        if (vm.isValidRegister(dest)) {
            try {
                if (vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    if (vm.get(arg2) == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    vm.put(dest, vm.get(arg1) / vm.get(arg2));
                } else if (vm.isValidRegister(arg1) && !vm.isValidRegister(arg2)) {
                    vm.put(dest, vm.get(arg1) / Integer.parseInt(arg2));
                } else if (!vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    vm.put(dest, Integer.parseInt(arg1) / vm.get(arg2));
                } else {
                    vm.put(dest, Integer.parseInt(arg1) / Integer.parseInt(arg2));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + dest + "at line " + programCounter);
        }
    }

    private void mod(String dest, String arg1, String arg2) {
        if (vm.isValidRegister(dest)) {
            try {
                if (vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    vm.put(dest, vm.get(arg1) % vm.get(arg2));
                } else if (vm.isValidRegister(arg1) && !vm.isValidRegister(arg2)) {
                    vm.put(dest, vm.get(arg1) % Integer.parseInt(arg2));
                } else if (!vm.isValidRegister(arg1) && vm.isValidRegister(arg2)) {
                    vm.put(dest, Integer.parseInt(arg1) % vm.get(arg2));
                } else {
                    vm.put(dest, Integer.parseInt(arg1) % Integer.parseInt(arg2));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + dest + "at line " + programCounter);
        }
    }

    private void inc(String reg) {
        if (vm.isValidRegister(reg)) {
            try {
                vm.put(reg, vm.get(reg) + 1);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + reg + "at line " + programCounter);
        }
    }

    private void dec(String reg) {
        if (vm.isValidRegister(reg)) {
            try {
                vm.put(reg, vm.get(reg) - 1);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + reg + "at line " + programCounter);
        }
    }

    private void free(String reg) {
        if (vm.isValidRegister(reg)) {
            vm.put(reg, JAsmVM.DEFAULT_VALUE());
        } else {
            throw new IllegalArgumentException("Invalid destination register: " + reg + "at line " + programCounter);
        }
    }

    private void swap(String reg1, String reg2) {
        if (vm.isValidRegister(reg1) && vm.isValidRegister(reg2)) {
            int temp = vm.get(reg1);
            vm.put(reg1, vm.get(reg2));
            vm.put(reg2, temp);
        } else {
            throw new IllegalArgumentException("Invalid destination registers: " + reg1 + ", " + reg2 + " at line " + programCounter);
        }
    }

    private void copy(String dest, String src) {
        if (vm.isValidRegister(dest) && vm.isValidRegister(src)) {
            vm.put(dest, vm.get(src));
        } else {
            throw new IllegalArgumentException("Invalid destination registers: " + dest + ", " + src + " at line " + programCounter);
        }
    }

    private void jmp(String lineNumber) {
        programCounter = Integer.parseInt(lineNumber);
    }

    private void jz(String reg, String lineNumber) {
        if (vm.isValidRegister(reg)) {
            if (vm.get(reg) == 0) {
                jmp(lineNumber);
            }
        } else {
            throw new IllegalArgumentException("Invalid register: " + reg + " at line " + programCounter);
        }
    }

    private void jnz(String reg, String lineNumber) {
        if (vm.isValidRegister(reg)) {
            if (vm.get(reg) != 0) {
                jmp(lineNumber);
            }
        } else {
            throw new IllegalArgumentException("Invalid register: " + reg + " at line " + programCounter);
        }
    }

    private void je(String reg1, String reg2, String lineNumber) {
        // reg 2 could be a number
        if (vm.isValidRegister(reg1) && vm.isValidRegister(reg2)) {
            if (vm.get(reg1) == vm.get(reg2)) {
                jmp(lineNumber);
            }
        } else if (vm.isValidRegister(reg1) && !vm.isValidRegister(reg2)) {
            try {
                if (vm.get(reg1) == Integer.parseInt(reg2)) {
                    jmp(lineNumber);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid registers: " + reg1 + ", " + reg2 + " at line " + programCounter);
        }
    }

    private void jne(String reg1, String reg2, String lineNumber) {
        // reg 2 could be a number
        if (vm.isValidRegister(reg1) && vm.isValidRegister(reg2)) {
            if (vm.get(reg1) != vm.get(reg2)) {
                jmp(lineNumber);
            }
        } else if (vm.isValidRegister(reg1) && !vm.isValidRegister(reg2)) {
            try {
                if (vm.get(reg1) != Integer.parseInt(reg2)) {
                    jmp(lineNumber);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid registers: " + reg1 + ", " + reg2 + " at line " + programCounter);
        }
    }

    private void jg(String reg1, String reg2, String lineNumber) {
        // reg 2 could be a number
        if (vm.isValidRegister(reg1) && vm.isValidRegister(reg2)) {
            if (vm.get(reg1) > vm.get(reg2)) {
                jmp(lineNumber);
            }
        } else if (vm.isValidRegister(reg1) && !vm.isValidRegister(reg2)) {
            try {
                if (vm.get(reg1) > Integer.parseInt(reg2)) {
                    jmp(lineNumber);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid registers: " + reg1 + ", " + reg2 + " at line " + programCounter);
        }
    }

    private void jl(String reg1, String reg2, String lineNumber) {
        // reg 2 could be a number
        if (vm.isValidRegister(reg1) && vm.isValidRegister(reg2)) {
            if (vm.get(reg1) < vm.get(reg2)) {
                jmp(lineNumber);
            }
        } else if (vm.isValidRegister(reg1) && !vm.isValidRegister(reg2)) {
            try {
                if (vm.get(reg1) < Integer.parseInt(reg2)) {
                    jmp(lineNumber);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid registers: " + reg1 + ", " + reg2 + " at line " + programCounter);
        }
    }

    private void jge(String reg1, String reg2, String lineNumber) {
        // reg 2 could be a number
        if (vm.isValidRegister(reg1) && vm.isValidRegister(reg2)) {
            if (vm.get(reg1) >= vm.get(reg2)) {
                jmp(lineNumber);
            }
        } else if (vm.isValidRegister(reg1) && !vm.isValidRegister(reg2)) {
            try {
                if (vm.get(reg1) >= Integer.parseInt(reg2)) {
                    jmp(lineNumber);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid registers: " + reg1 + ", " + reg2 + " at line " + programCounter);
        }
    }

    private void jle(String reg1, String reg2, String lineNumber) {
        // reg 2 could be a number
        if (vm.isValidRegister(reg1) && vm.isValidRegister(reg2)) {
            if (vm.get(reg1) <= vm.get(reg2)) {
                jmp(lineNumber);
            }
        } else if (vm.isValidRegister(reg1) && !vm.isValidRegister(reg2)) {
            try {
                if (vm.get(reg1) <= Integer.parseInt(reg2)) {
                    jmp(lineNumber);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid registers: " + reg1 + ", " + reg2 + " at line " + programCounter);
        }
    }

    private void iterThrough(String[] args) {
        // ITER_THROUGH |X0:X9| -> Z0 (
        // ...
        // )
        // args[0] = ITER_THROUGH, args[1] = |X0:X9|, args[2] = ->, args[3] = Z0
        //

        // check syntax and validity of args
        if (args.length != 4) {
            throw new IllegalArgumentException("Invalid number of arguments for ITER_THROUGH at line " + programCounter);
        }

        String[] regs = args[1].replace("|", "").split(":");
        String reg1 = regs[0], reg2 = regs[1];
        String reg3 = args[3];

        if (!vm.isValidRegister(reg1) || !vm.isValidRegister(reg2) || !vm.isValidRegister(reg3)) {
            if (!vm.areSameType(reg1, reg2)) {
                throw new IllegalArgumentException("Invalid registers of different type for ITER_THROUGH at line " + programCounter + ": " + reg1 + ", " + reg2 + ".");
            }
            throw new IllegalArgumentException("Invalid registers for ITER_THROUGH at line " + programCounter + ": " + reg1 + ", " + reg2 + ", " + reg3 + ".");
        }

        // throw an exception if reg1 comes first by order of registers than reg2
        if (!vm.comesFirst(reg1, reg2)) {
            throw new IllegalArgumentException("Invalid registers order for ITER_THROUGH at line " + programCounter + ": " + reg1 + ", " + reg2 + ".");
        }

        char type = reg1.charAt(0);
        int start = Integer.parseInt(String.valueOf(reg1.charAt(1)));
        int end = Integer.parseInt(String.valueOf(reg2.charAt(1)));
        for (int i = start; i <= end; i++) {
            vm.put(reg3, vm.get(type + String.valueOf(i)));
            // execute the code inside the loop
            // TODO: implement this
        }
    }

    private void iterFor(String[] args) {
        // ITER_FOR |X0:n| (...), where n is a non-negative integer, and ... is a block of code
        // args[0] = ITER_FOR, args[1] = |X0:n|, args[2] = (...)
        // parse args
        String[] reg_n = args[1].replace("|", "").split(":");
        String reg = reg_n[0];
        int n = Integer.parseInt(reg_n[1]);

        // check syntax and validity of args
        if (args.length != 3) {
            throw new IllegalArgumentException("Invalid number of arguments for ITER_FOR at line " + programCounter);
        }

        if (!vm.isValidRegister(reg)) {
            throw new IllegalArgumentException("Invalid register for ITER_FOR at line " + programCounter + ": " + reg);
        }

        for (int i = 0; i < n; i++) {
            vm.put(reg, i);
            // execute the code inside the loop
            // TODO: implement this
        }
    }

    private void _show(String reg) {
        // _SHOW reg
        if (!vm.isValidRegister(reg)) {
            throw new IllegalArgumentException("Invalid register for _SHOW at line " + programCounter + ": " + reg);
        }
        System.out.println(vm.get(reg));
    }

    private void _ascii(String reg) {
        // _ASCII reg
        if (!vm.isValidRegister(reg)) {
            throw new IllegalArgumentException("Invalid register for _ASCII at line " + programCounter + ": " + reg);
        }
        System.out.print((char) vm.get(reg));
    }

    private void _hex(String reg) {
        // _HEX reg
        if (!vm.isValidRegister(reg)) {
            throw new IllegalArgumentException("Invalid register for _HEX at line " + programCounter + ": " + reg);
        }
        System.out.print(Integer.toHexString(vm.get(reg)));
    }

    private void _cls() {
        // _CLS
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void _newl() {
        // _NEWL
        System.out.println();
    }

    private void _tab() {
        // _TAB
        System.out.print("\t");
    }

    public void execute(String[] lines) throws SyntaxError {
        for (programCounter = 1; programCounter <= lines.length; programCounter++) {
            String line = lines[programCounter - 1];
            executeLine(line);
        }
    }
}
