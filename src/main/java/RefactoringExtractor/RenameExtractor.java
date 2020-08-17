package RefactoringExtractor;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RenameExtractor {
    private GitHistoryRefactoringMinerImpl miner;
    private static String tempDir = "/tmp/RefactoringMiner";

    public RenameExtractor(){
        miner = new GitHistoryRefactoringMinerImpl();
        miner.setRefactoringTypesToConsider(RefactoringType.RENAME_METHOD, RefactoringType.RENAME_PARAMETER,
                RefactoringType.RENAME_VARIABLE, RefactoringType.CHANGE_RETURN_TYPE,
                RefactoringType.CHANGE_PARAMETER_TYPE, RefactoringType.CHANGE_VARIABLE_TYPE);
    }

    private RevCommit updateRepo(Git git, File file, String fileContent, String commitMsg) throws IOException, GitAPIException {
        try(FileWriter writer = new FileWriter(file, false)){
            writer.write(fileContent);
            writer.flush();
        }
        git.add().addFilepattern(file.getName()).call();
        return git.commit().setMessage(commitMsg).call();
    }

    private String getFakeCompilationUnit(String method) {
        String header1 = "public class Test {\n";
        String header2 = "\n}";
        return header1 + method + header2;
    }

    public List<Refactoring> extract(String method1, String method2) throws Exception {
        method1 = getFakeCompilationUnit(method1);
        method2 = getFakeCompilationUnit(method2);

        File localPath = new File(tempDir);
        Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
        repository.create();

        RevCommit commit1, commit2;
        try(Git git = new Git(repository)){
            File myFile = new File(repository.getDirectory().getParent(), "Test.java");
            if (!myFile.createNewFile()){
                throw new IOException("Can not create file " + myFile);
            }
            commit1 = updateRepo(git, myFile, method1, "first commit");
            commit2 = updateRepo(git, myFile, method2, "second commit");
        }

        List<List<Refactoring>> results = new ArrayList<>();
        miner.detectBetweenCommits(repository, commit1.getId().getName(), commit2.getId().getName(),
                new RefactoringHandler() {
                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        results.add(refactorings);
                    }
                });
        repository.close();
        FileUtils.deleteDirectory(localPath);
        return results.get(0);
    }
}
