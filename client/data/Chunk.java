package data;

public class Chunk {
    private String fineName;
    private int chunkNumber;
    private Object file;

    public Chunk(String fineName, int chunkNumber, Object file) {
        this.fineName = fineName;
        this.chunkNumber = chunkNumber;
        this.file = file;
    }

    public String getFineName() {
        return fineName;
    }

    public void setFineName(String fineName) {
        this.fineName = fineName;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
    }
}
