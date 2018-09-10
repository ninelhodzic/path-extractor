package org.datazup.pathextractor;


import org.datazup.exceptions.PathExtractorException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by admin@datazup on 3/14/16.
 */
public class PathExtractorTest extends ExtractorTestBase {
    @Test
    public void itCompilesHandleBarsString() throws IOException {
        String strToCompile = "hello {{child.name}}";
        Object compiled = pathExtractor.compileString(strToCompile);
        Assert.assertNotNull(compiled);
        Assert.assertTrue(compiled instanceof String);
        Assert.assertTrue(((String) compiled).equals("hello child"));
        //Assert.assertTrue(compiled.equals("hello "));
    }

    @Test
    public void isNewHelpersWorking() throws IOException {
        String strToCompile = "";
        strToCompile += " {{#IFB (COMPARE child.name '==' 'child') }} ";
        strToCompile += " THIS IS Child name {{child.name}} ";
        strToCompile += " {{else}} ";
        strToCompile += " THIS IS NOT CHID NAME ";
        strToCompile += " {{/IFB}}";
        Object compiled = pathExtractor.compileString(strToCompile);
        Assert.assertNotNull(compiled);
        Assert.assertTrue(compiled instanceof String);
        System.out.println(compiled);
        Assert.assertTrue(((String) compiled).trim().equals("THIS IS Child name child"));
    }

    @Test
    public void isNewHelpersOrWorking() throws IOException {
        String strToCompile = "";
        strToCompile += " {{#IFB (OR (COMPARE child.name '==' 'child') (COMPARE child.name '!=' 'child1')) }} ";
        strToCompile += " THIS IS Child name {{child.name}} ";
        strToCompile += " {{else}} ";
        strToCompile += " THIS IS NOT CHID NAME ";
        strToCompile += " {{/IFB}}";
        Object compiled = pathExtractor.compileString(strToCompile);
        Assert.assertNotNull(compiled);
        Assert.assertTrue(compiled instanceof String);
        System.out.println(compiled);
        Assert.assertTrue(((String) compiled).trim().equals("THIS IS Child name child"));
    }

    @Test
    public void itCompilesReturnString() throws IOException {
        String strToCompile = "neki text";
        Object compiled = pathExtractor.compileString(strToCompile);
        Assert.assertNotNull(compiled);
        Assert.assertTrue(compiled instanceof String);
        Assert.assertTrue(((String) compiled).equals("neki text"));
    }

    @Test
    public void itCompilesReturnStringBenchmark() throws IOException {
        String strToCompile = " ovo je moj text {{child.name}}";
        long start = System.currentTimeMillis();
        int num = 10000;
        for (int i = 0; i < num; i++) {
            Object compiled = pathExtractor.compileString(strToCompile);
            //   Assert.assertNotNull(compiled);
        }
        long end = System.currentTimeMillis();
        System.out.println("Num: " + num + " executed in: " + (end - start) + " ms, average: " + ((end - start) / num) + " ms");


        //Assert.assertTrue(compiled.equals("hello "));
    }

    @Test
    public void isCompileHtmlString() throws IOException {
        String strToCompile = "<html> ovo je moj text {{child.name}} </html>";
        Object compiled = pathExtractor.compileString(strToCompile);

        Assert.assertNotNull(compiled);
        Assert.assertTrue(compiled instanceof String);
        Assert.assertTrue(((String) compiled).startsWith("<html>"));
        Assert.assertTrue(((String) compiled).endsWith("</html>"));
        Assert.assertFalse(((String) compiled).contains("{{"));
    }

    @Test
    public void itCompilesObjectReturnMap() throws IOException {
        String strToCompile = "$child$";
        Object compiled = pathExtractor.compileString(strToCompile);
        Assert.assertNotNull(compiled);
        Assert.assertTrue(compiled instanceof Map);
        //Assert.assertTrue(compiled.equals("hello "));
    }

    @Test
    public void itCompilesObjectReturnValue() throws IOException {
        String path = "$child.value$";
        Object obj = pathExtractor.compileString(path);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof Integer);
    }

    @Test
    public void itExtractsObject() {
        String path = "$child.value$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof Integer);
    }

    @Test
    public void itExtractsChildObject() {
        String path = "$child.name$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
        Assert.assertEquals("child", obj);
    }

    @Test
    public void itExtractsListType() {
        String path = "$child.list$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof List);
    }

    @Test
    public void itExtractsList() {
        String path = "$child.list$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof List);
    }

    @Test
    public void itExtractListWithParenthesis() {
        String path = "$child.list[]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }
    @Test
    public void itExtractListWithParenthesisChild() {
        String path = "$items[].data$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof List);
    }

    @Test
    public void itExtractListFirstObject() {
        String path = "$child.list[0]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObject() {
        String path = "$child.list[last]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMap() {
        String path = "$child.list[last].first$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildIn() {
        String path = "$child.list[last].third.thirdChild$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInBench() {
        /*String path = "$child.list[last].third.thirdChild$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);*/

        String path = "$child.list[last].third.thirdChild$";
        long start = System.currentTimeMillis();
        int num = 1000000;
        for (int i = 0; i < num; i++) {
            Object obj = pathExtractor.extractObjectValue(path);
            //   Assert.assertNotNull(compiled);
        }
        long end = System.currentTimeMillis();
        System.out.println("Num: " + num + " executed in: " + (end - start) + " ms, average: " + ((end - start) / num) + " ms");

    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInList() {

        String path = "$child.list[].third.thirdlist$";

        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof List);

    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInListIn() {
        String path = "$child.list[last].fourth[]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInListInFirst() {
        String path = "$child.list[last].fourth[0]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInListInLast() {
        String path = "$child.list[last].fourth[last]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInListInLastMapList() {
        String path = "$child.list[last].third.thirdlist[]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInListInLastMapListFirst() {
        String path = "$child.list[last].third.thirdlist[0]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInListInLastMapListLast() {
        String path = "$child.list[last].third.thirdlist[last]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);
    }

    @Test
    public void itExtractListLastObjectWithFieldInMapChildInListInLastMapListLastUpdate() {
        String path = "$child.list[last].third.thirdlist[last]$";
        Object obj = pathExtractor.extractObjectValue(path);
        Assert.assertNotNull(obj);

        pathExtractor.update("child.list[last].third.thirdlist[last]", null);

        path = "$child.list[last].third.thirdlist[last]$";
        obj = pathExtractor.extractObjectValue(path);
        Assert.assertNull(obj);
    }

    @Test
    public void itThrowsObjectPathException() {
        String path = "$child";
        try {
            Object o = pathExtractor.extractObjectValue(path);
            Assert.assertTrue(false);
        }catch (Throwable t){
            Assert.assertTrue(t instanceof PathExtractorException);
            System.out.println(t.getMessage());
        }
    }

   /* @Test TODO - Fix parentheses typos errors and test - we should throw exception
    public void itThrowsObjectPathParenthesesException() {
        String path = "$child.list[$";
        try {
            Object o = pathExtractor.extractObjectValue(path);
            Assert.assertTrue(false);
        }catch (Throwable t){
            Assert.assertTrue(t instanceof PathExtractorException);
            System.out.println(t.getMessage());
        }
    }*/

}

