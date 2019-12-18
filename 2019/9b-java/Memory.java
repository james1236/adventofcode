import java.util.ArrayList;
import java.util.Arrays;

public class Memory
{
    private int instructionPointer = 0;
    private int relativeBase = 0;
    private ArrayList<Long> memory = new ArrayList<Long>();

    public Memory(long[] program)
    {
        for(long data:program) {
            this.memory.add(data);
        }
    }

    //Modifiers
    
    public void incrementRelativeBase(int amount)
    {
        relativeBase += amount;
    }
    
    public void incrementInstructionPointer(int amount)
    {
        instructionPointer += amount;
    }
    
    public long getPointerData() {
        return getData(instructionPointer);
    }
    
    //Direct getters/setters
    
    public void setInstructionPointer(int instructionPointer)
    {
        this.instructionPointer = instructionPointer;
    }
    
    private void setData(int address, long data) {
        expandMemory(address);
        memory.set(address, data);
    }
    
    private long getData(int address) {
        expandMemory(address);
        return memory.get(address);
    }
    
    //Get Modes *CONFIRMED*
    
    public long getPositionData(int offset) {
        //Get the data from the address stored at ip + offset
        return getData((int)getImmediateData(offset));
    }
    
    public long getImmediateData(int offset) {
        //Get the data directly from the slot at address ip + offset
        return getData(instructionPointer + offset);
    }
    
    public long getRelativeData(int offset) {
        //Get the data from the address stored at ip + offset plus the relative base
        return getData(relativeBase + (int)getImmediateData(offset));
    }
    
    //Set Modes
    
    public void setPositionData(int offset, long data) {
        setData((int)getImmediateData(offset), data);
    }
    
    public void setImmediateData(int offset, long data) {
        setData(instructionPointer + offset, data);
    }
    
    public void setRelativeData(int offset, long data) {
        setData(relativeBase + (int)getImmediateData(offset), data);
    }
    
    //On every get/set if the address requested is outside of memory then expand memory to include that index
    private void expandMemory(int end) {
        while (end >= memory.size()) {
            memory.add(0l);
        }
    }
}
