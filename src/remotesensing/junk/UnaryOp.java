package remotesensing.junk;

public abstract class UnaryOp {
    public abstract void apply(short[] data);
    public abstract void apply(short[] data, int index, int len);
}
