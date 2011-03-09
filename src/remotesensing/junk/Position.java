package remotesensing.junk;

public class Position {

    private int line;
    private int sample;
    
    public Position() { 
        // empty
    }
    
    public Position(int line, int sample) { 
        this.line = line;
        this.sample = sample;
    }
    
    public void set(int line, int sample) { 
        this.line = line;
        this.sample = sample;
    }
    
    public int getLine() { 
        return line;
    }
    
    public int getSample() { 
        return sample;
    }
}
