package fraco;

import fraco.utils.CommentType;
import fraco.utils.Comments;
import fraco.utils.Identifier;
import fraco.utils.Type;
import org.junit.Test;

import static org.junit.Assert.*;

public class FracoTest {
    @Test
    public void testNormalCase(){
        testOne("testFraco","testCoraf",
                "Method testFraco for testing", "Method testCoraf for testing",
                true);
    }

    @Test
    public void testSemantic(){
        testOne("testFraco", "testCoraf",
                "Method test fraco for testing", "Method testCorafs for testing",
                true);
        // insert other tokens
        testOne("testFraco", "testCoraf",
                "Method test class Fraco for testing", "Method test class Fraco for testing",
                true);
    }

    @Test
    public void testBeginOfComment(){
        testOne("testFraco", "testCoraf",
                "testFraco is a method", "testCoraf is a method",
                true);
    }

    @Test
    public void testEndOfComment(){
        testOne("testFraco", "testCoraf",
                "Method named testFraco", "Method named testCoraf",
                true);
    }

    @Test
    public void testParenthesis(){
        testOne("testFraco", "testCoraf",
                "Method testFraco() for testing", "Method testCoraf() for testing",
                true);
    }

    @Test
    public void testDot(){
        // NOTE: this case is special
        testOne("testFraco", "testCoraf",
                "Class TestFraco.testFraco for testing", "Class TestFraco.testCoraf for testing",
                true);
        testOne("TestFraco", "TestCoraf",
                "Class TestFraco.testFraco for testing", "Class TestCoraf.testFraco for testing",
                true);
    }

    @Test
    public void testMultipleMatches(){
        testOne("testFraco", "testCoraf",
                "Method testFraco is used for testFraco", "Method testCoraf is used for testCoraf",
                true);
        testOne("testFraco", "testCoraf",
                "Method testFraco is used for test fraco", "Method testCoraf is used for testCorafs",
                true);
        testOne("testFraco", "testCoraf",
                "Method testFraco is used for test class fraco", "Method testCoraf is used for test class fraco",
                true);
    }

    @Test
    public void testNotMatch(){
        testOne("testFraco", "testCoraf",
                "This is a useless test", "This is a useless test",
                false);
    }

    @Test
    public void testEmptyComment(){
        testOne("testFraco", "testCoraf",
                "", "",
                false);
    }

    private void testOne(String identifier, String newName, String commentStr, String newCommentStr,
                         Boolean match){
        Fraco fraco = new Fraco(true, true);
        Identifier ident = new Identifier(identifier, Type.METHOD, null);
        Comments comment = new Comments(commentStr, true, CommentType.JDOCFREE);
        Boolean matchResult = fraco.matchOne(ident, comment);
        assertEquals(match, matchResult);
        if (matchResult)
            assertEquals(newCommentStr, fraco.replaceOne(ident, comment, newName));
    }

}