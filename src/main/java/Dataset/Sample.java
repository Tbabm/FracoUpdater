package Dataset;

import com.google.gson.JsonObject;

public class Sample {
    int sampleId;
    String srcMethod;

    String dstMethod;
    String srcDesc;
    String dstDesc;

    public Sample(JsonObject jObject){
        this(jObject.get("sample_id").getAsInt(),
            jObject.get("src_method").getAsString(),
            jObject.get("dst_method").getAsString(),
            jObject.get("src_desc").getAsString(),
            jObject.get("dst_desc").getAsString());
    }

    public Sample(int sampleId, String srcMethod, String dstMethod, String srcDesc, String dstDesc){
        this.sampleId = sampleId;
        this.srcMethod = srcMethod;
        this.dstMethod = dstMethod;
        this.srcDesc = srcDesc;
        this.dstDesc = dstDesc;
    }

    public String toString(){
        return String.format("Id: %d\nsrcMethod: %s\ndstMethod: %s\nsrcDesc: %s\ndstDesc: %s",
                srcMethod, dstMethod, srcDesc, dstDesc);
    }

    public int getSampleId() {
        return sampleId;
    }

    public String getSrcMethod() {
        return srcMethod;
    }

    public String getDstMethod() {
        return dstMethod;
    }

    public String getSrcDesc() {
        return srcDesc;
    }

    public String getDstDesc() {
        return dstDesc;
    }
}
