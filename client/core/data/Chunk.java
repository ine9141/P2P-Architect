package core.data;

public class Chunk {
    private String fineName;
    private int chunkNumber;
    private byte[] file;

    public Chunk(String fineName, int chunkNumber, byte[] file) {
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

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
