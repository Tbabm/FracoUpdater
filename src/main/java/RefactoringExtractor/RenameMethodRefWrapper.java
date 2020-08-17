package RefactoringExtractor;

import fraco.utils.CommentType;
import fraco.utils.Comments;
import fraco.utils.Identifier;
import fraco.utils.Type;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;

import java.util.ArrayList;
import java.util.List;

public class RenameMethodRefWrapper extends AbstractRefWrapper {
    private RenameOperationRefactoring ref;

    public RenameMethodRefWrapper(RenameOperationRefactoring ref){
        this.ref = ref;
    }

    @Override
    public Identifier getIdentifier() {
        if (identifier == null) {
            List<String> paramNames = new ArrayList<>();
            for (UMLParameter param : ref.getOriginalOperation().getParameters()) {
                if (!param.getKind().equals("return")) {
                    paramNames.add(param.getName());
                }
            }
            identifier = new Identifier(ref.getOriginalOperation().getName(), Type.METHOD, paramNames);
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
        return ref.getRenamedOperation().getName();
    }
}
