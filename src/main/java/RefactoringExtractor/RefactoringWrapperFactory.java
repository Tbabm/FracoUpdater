package RefactoringExtractor;

import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.refactoringminer.api.Refactoring;

public class RefactoringWrapperFactory {
    public AbstractRefWrapper createRefactoringWrapper(Refactoring ref){
        // Fraco will not be triggered by variable type change and return type change
        if (ref instanceof ChangeReturnTypeRefactoring)
            return null;
        if (ref instanceof ChangeVariableTypeRefactoring)
            return null;
        if (ref instanceof RenameOperationRefactoring)
            return new RenameMethodRefWrapper((RenameOperationRefactoring) ref);
        if (ref instanceof RenameVariableRefactoring)
            return new RenameVariableRefWrapper((RenameVariableRefactoring) ref);
        return null;
    }
}
