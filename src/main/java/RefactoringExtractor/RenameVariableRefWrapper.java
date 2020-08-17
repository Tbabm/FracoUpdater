package RefactoringExtractor;

import fraco.utils.CommentType;
import fraco.utils.Comments;
import fraco.utils.Identifier;
import fraco.utils.Type;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;

import java.util.ArrayList;
import java.util.List;

public class RenameVariableRefWrapper extends AbstractRefWrapper {
    private RenameVariableRefactoring ref;

    public RenameVariableRefWrapper(RenameVariableRefactoring ref){
        this.ref = ref;
    }

    @Override
    public Identifier getIdentifier() {
        if (identifier == null) {
            // always return paramNames
            List<String> paramNames = new ArrayList<>();
            for (UMLParameter param : ref.getOperationBefore().getParameters()) {
                if (!param.getKind().equals("return")) {
                    paramNames.add(param.getName());
                }
            }
            identifier = new Identifier(ref.getOriginalVariable().getVariableName(), Type.LOCAL, paramNames);
        }
        return identifier;
    }

    @Override
    public void createComment(String commentStr) {
        assert comment == null;
        comment = new Comments(commentStr, true, CommentType.JDOCFREE);
    }

    @Override
    public Comments getComment() {
        assert comment != null;
        return comment;
    }

    @Override
    public String getNewName() {
        return ref.getRenamedVariable().getVariableName();
    }
}
