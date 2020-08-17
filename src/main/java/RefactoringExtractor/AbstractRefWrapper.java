package RefactoringExtractor;

import fraco.utils.Comments;
import fraco.utils.Identifier;

public abstract class AbstractRefWrapper {
    Identifier identifier = null;
    Comments comment = null;

    public abstract Identifier getIdentifier();
    public abstract void createComment(String comment);
    public abstract Comments getComment();

    public abstract String getNewName();
}
