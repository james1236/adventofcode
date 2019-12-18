import java.util.ArrayList;
import java.util.Arrays;

public class Interpreter
{
    private Memory memory;
    
    public Interpreter(long[] program)
    {
        setProgram(program);
    }
    
    public Interpreter(String program)
    {
        setProgram(program);
    }
    
    public void setProgram(long[] program) {
        memory = new Memory(program);
    }
    
    public void setProgram(String programString) {
        String[] programSplit = programString.split("\\s*,\\s*");
        long[] program = new long[programSplit.length];
        
        for (int i = 0; i < programSplit.length; i++) {
            program[i] = Long.parseLong(programSplit[i]);
        }
        
        setProgram(program);
    }

    private int getOpcode()
    {
        String dataString = Long.toString(memory.getPointerData()); //Get combined opcode & parameter modes string
        //If no param modes are given, return opcode alone;
        if (dataString.length() < 3) {
            return Integer.parseInt(dataString);
        }
        
        //Return last two characters of the stringified data at
        //the instruction pointer as an integer
        return Integer.parseInt(dataString.substring(dataString.length()-2));
    }
    
    private int getParameterMode(int parameterIndex) {
        //Returns the parameter mode for the supplied parameter's index
        String dataString = Long.toString(memory.getPointerData()); //Get combined opcode & parameter modes string
        //Return 0 for all leading 0 parameter modes that have been trimmed
        if (dataString.length()-3-parameterIndex < 0) {
            return 0;
        }
        //Return the correct parameter mode index
        return Integer.parseInt(Character.toString(dataString.charAt(dataString.length()-3-parameterIndex)));
    }
    
    private long getModeData(int parameterIndex) {
        switch (getParameterMode(parameterIndex)) {
            case 1:
                //Immediate mode
                return memory.getImmediateData(parameterIndex+1);
            case 2:
                //Relative mode
                return memory.getRelativeData(parameterIndex+1);
            default:
                //Position mode
                return memory.getPositionData(parameterIndex+1);
        }
    }
    
    private void setModeData(int parameterIndex, long data) {
        switch (getParameterMode(parameterIndex)) {
            case 1:
                //Immediate mode
                memory.setImmediateData(parameterIndex+1, data);
                break;
            case 2:
                //Relative mode
                memory.setRelativeData(parameterIndex+1, data);
                break;
            default:
                //Position mode (0)
                memory.setPositionData(parameterIndex+1, data);
                break;
        }
    }
    
    //Overloading for optional input
    
    public long step() {
        return step(Long.MAX_VALUE);
    }
    
    public long step(long input) {
        int parameters;
        switch (getOpcode()) {
            case 1:
                //Add -- numbers are parameter indexes
                parameters = 3;
                setModeData(2, getModeData(0) + getModeData(1));
                break;
            case 2:
                //Multiply
                parameters = 3;
                setModeData(2, getModeData(0) * getModeData(1));
                break;
            case 3:
                //Input
                parameters = 1;
                if (input == Long.MAX_VALUE) {
                    System.out.println("Error: Unsupplied Input");
                }
                setModeData(0, input);
                break;
            case 4:
                //Output
                parameters = 1;
                System.out.println(getModeData(0));
                memory.incrementInstructionPointer(parameters+1);
                return getModeData(0);
            case 5:
                //Jump if true (non-zero) return for non-increment case, break for increment case
                parameters = 2;
                if (getModeData(0) != 0l) {
                    memory.setInstructionPointer((int)getModeData(1));
                    return Long.MAX_VALUE;
                }
                break;
            case 6:
                //Jump if false (zero)
                parameters = 2;
                if (getModeData(0) == 0l) {
                    memory.setInstructionPointer((int)getModeData(1));
                    return Long.MAX_VALUE;
                }
                break;
            case 7:
                //Less than (1 < 2)
                parameters = 3;
                if (getModeData(0) < getModeData(1)) {
                    setModeData(2, 1l);
                } else {
                    setModeData(2, 0l);
                }
                break;
            case 8:
                //Equals
                parameters = 3;
                if (getModeData(0) == getModeData(1)) {
                    setModeData(2, 1l);
                } else {
                    setModeData(2, 0l);
                }
                break;
            case 9:
                //Adjust Relative Base
                parameters = 1;
                memory.incrementRelativeBase((int)getModeData(0));
                break;
            case 99:
                //Exit
                parameters = 0;
                System.out.println("Exit");
                return Long.MIN_VALUE; //MIN_VALUE represents halt action
            default:
                parameters = 0;
                System.out.println("Error: Invalid operation");
                return Long.MIN_VALUE; //MIN_VALUE represents halt action
        }
        memory.incrementInstructionPointer(parameters+1);
        return Long.MAX_VALUE;
    }
}
