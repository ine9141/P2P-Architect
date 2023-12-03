package core.struct;

public class Chunk {
    private int fileNum;
    private int chunkNum;
    private byte[] file;

    public Chunk(int fileNum, int chunkNum, byte[] file) {
        this.fileNum = fileNum;
        this.chunkNum = chunkNum;
        this.file = file;
    }

    public Integer getFileNum() {
        return fileNum;
    }
    public int getChunkNumber() {
        return chunkNum;
    }

    public byte[] getFile() {
        return file;
    }

}
