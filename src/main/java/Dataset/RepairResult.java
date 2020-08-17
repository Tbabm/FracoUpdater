package Dataset;

import java.io.Serializable;

public class RepairResult implements Serializable {
    boolean matched;
    String result;

    public boolean isMatched() {
        return matched;
    }

    public String getResult() {
        return result;
    }

    public RepairResult(boolean matched, String result){
        this.matched = matched;
        this.result = result;
    }
}
