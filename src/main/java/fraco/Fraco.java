package fraco;

import fraco.rules.*;
import fraco.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Fraco {
    private static Logger logger = LoggerFactory.getLogger(Fraco.class);

    private boolean identicalMatch = false;
    private boolean filterProximity = true;
    private RulesFactory rulesFactory;
    private CommonRules commonRules = new CommonRules();


    public static void main(String[] args){
        // set flags for DetectFragileComments here
        // default is _filterProximity
        // DetectFragileComments._identicalMatch = true;
        Fraco fraco = new Fraco(true, true);
        Identifier identifier = new Identifier("testFraco", Type.METHOD, null);
        String comment1 = "method to testFraco";
        Comments comment = new Comments(comment1, true, CommentType.JDOCFREE);
        String newName = "testCoraf";
        fraco.testOne(identifier, comment, newName);

        String comment2 = "method to test class fraco";
        comment = new Comments(comment2, true, CommentType.JDOCFREE);
        fraco.testOne(identifier, comment, newName);
    }

    public Fraco(Boolean identicalMatch, Boolean filterProximity) {
        this.identicalMatch = identicalMatch;
        this.filterProximity= filterProximity;
        this.rulesFactory = new RulesFactory();
    }

    public void testOne(Identifier identifier, Comments comment, String newName){
        boolean result = matchOne(identifier, comment);
        String newComment = comment.get_comment();
        if (result)
           newComment = replaceOne(identifier, comment, newName);
        logger.info("" + result);
        logger.info(newComment);
    }

    public boolean matchOne(Identifier identifier, Comments comment){
        boolean hasIdenticalMatch = false;
        boolean hasSemanticMatch = false;
        List<Match> allMatches = new ArrayList<>();

        // matches will be overwritten, so we need to store it after one match
        if (identicalMatch){
            DetectFragileComments._identicalMatch = true;
            Rules matchRule = rulesFactory.getRule(RuleTypes.IDENTICAL_MATCH);
            if (matchRule.ApplyRules(identifier, comment))
                hasIdenticalMatch = true;
        }
        if (comment.get_matches() != null)
            allMatches.addAll(comment.get_matches());
        comment.set_matches(null);

        if (filterProximity){
            DetectFragileComments._identicalMatch = false;
            DetectFragileComments._filterProximity = true;
            Rules matchRule = rulesFactory.getRule(RuleTypes.LEMMA_TOKENS_SEMANTIC);
            if (matchRule.ApplyRules(identifier, comment))
                hasSemanticMatch = true;
        }
        if (comment.get_matches() != null)
            allMatches.addAll(comment.get_matches());
        comment.set_matches(allMatches);
        return (hasIdenticalMatch || hasSemanticMatch);
    }

    /***
     * See Fraco source code  ca.mcgill.cs.stg.fraco.Properties.handler.QuickFix
     * @param identifier
     * @param comment
     * @return
     */
    public String replaceOne(Identifier identifier, Comments comment, String newName){
        String commentStr = comment.get_comment();
        for (Match match : comment.get_matches()){
            // the original code does not have trim()
            // I add trim() to enhance Fraco
            String wordToFind = match.get_matchString().substring(0,
                    match.get_matchString().indexOf("#") - 1).trim();
            String wordToReplace = newName;
            String originalIdentifier = identifier.get_name();
            if (!originalIdentifier.equalsIgnoreCase(wordToFind)) {
                wordToReplace = commonRules.convertSingularToPlural(wordToReplace);
            }
            commentStr = replaceComment(commentStr, wordToFind, wordToReplace);
        }
        return commentStr;
    }

    public String matchAndReplaceOne(Identifier identifier, Comments comment, String newName){
       if (matchOne(identifier, comment))
            return replaceOne(identifier, comment, newName);
        return comment.get_comment();
    }

    private String replaceComment(String commentStr, String wordToFind, String wordToReplace){
        String newCommentStr = commentStr.replaceFirst("(?<=(\\W|^))" + wordToFind + "(?=(\\s|$))",
                wordToReplace);
        if (!newCommentStr.equals(commentStr))
            return newCommentStr;

        newCommentStr = commentStr.replaceFirst("(?<=(\\W|^))" + wordToFind + "\\(",
                wordToReplace + "(");
        if (!newCommentStr.equals(commentStr))
            return newCommentStr;

        newCommentStr = commentStr.replaceFirst("(?<=(\\W|^))" + wordToFind + "\\.",
                wordToReplace + ".");
        return newCommentStr;
    }
}
