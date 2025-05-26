package app.nepaliapp.sabbaikomaster.common;

public class DataPart {
    public String fileName;
    public byte[] content;
    public String type;

    public DataPart(String fileName, byte[] content, String type) {
        this.fileName = fileName;
        this.content = content;
        this.type = type;
    }
}