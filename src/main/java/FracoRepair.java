import Dataset.RepairResult;
import Dataset.Sample;
import RefactoringExtractor.AbstractRefWrapper;
import RefactoringExtractor.RefactoringWrapperFactory;
import RefactoringExtractor.RenameExtractor;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fraco.Fraco;
import me.tongfei.progressbar.ProgressBar;
import org.refactoringminer.api.Refactoring;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FracoRepair {
    RefactoringWrapperFactory refWrapperFactory;
    RenameExtractor extractor;
    Fraco fraco;

    public static void main(String[] args) throws Exception {
        assert args.length == 2;
        String testSetPath = args[0];
        String outFile = args[1];
        List<String> lines = Files.readAllLines(Paths.get(testSetPath));
        JsonParser parser = new JsonParser();
        FracoRepair repair = new FracoRepair();
        List<RepairResult> results = new ArrayList<>();
        int count = 0;
        for (String line : ProgressBar.wrap(lines, "FracoRepair")){
            ++count;
            JsonObject jObject = parser.parse(line).getAsJsonObject();
            Sample sample = new Sample(jObject);
            RepairResult curResult = repair.repairSample(sample);
            results.add(curResult);
        }
        System.out.println(results.size());
        try(FileWriter writer = new FileWriter(new File(outFile))) {
            new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create().toJson(results, writer);
        }
        System.out.println("Finish!");
        return;
    }

    public FracoRepair(){
        extractor = new RenameExtractor();
        fraco = new Fraco(true, true);
        refWrapperFactory = new RefactoringWrapperFactory();
    }

    public RepairResult repairSample(Sample sample) throws Exception {
        return repairOne(sample.getSrcMethod(), sample.getDstMethod(), sample.getSrcDesc());
    }

    /**
     * get the comment that is repaired by Fraco
     */
    public RepairResult repairOne(String srcMethod, String dstMethod, String srcDesc) throws Exception {
        List<Refactoring> refs = extractor.extract(srcMethod, dstMethod);
        String comment = srcDesc;
        boolean matched = false;
        for (Refactoring ref : refs){
            AbstractRefWrapper refWrapper = refWrapperFactory.createRefactoringWrapper(ref);
            if (refWrapper == null)
                continue;

            refWrapper.createComment(comment);
            if (fraco.matchOne(refWrapper.getIdentifier(), refWrapper.getComment())){
                matched = true;
                comment = fraco.replaceOne(refWrapper.getIdentifier(), refWrapper.getComment(),
                        refWrapper.getNewName());
            }
        }
        return new RepairResult(matched, comment);
    }
}
