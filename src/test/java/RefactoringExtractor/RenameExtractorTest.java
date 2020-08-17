package RefactoringExtractor;

import RefactoringExtractor.RenameExtractor;
import org.junit.Test;
import org.refactoringminer.api.Refactoring;

import java.util.List;

import static org.junit.Assert.*;

public class RenameExtractorTest {

    @Test
    public void testExtract() throws Exception {
        String method1 = "Map<MetricFunction, Double> processAggregateResponse(\n" +
                "      Map<ThirdEyeRequest, PinotThirdEyeResponse> queryResultMap) {\n" +
                "    Map<MetricFunction, Double> metricOnlyData = new HashMap<>();\n" +
                "    for (Entry<ThirdEyeRequest, PinotThirdEyeResponse> entry : queryResultMap.entrySet()) {\n" +
                "      ThirdEyeRequest thirdEyeRequest = entry.getKey();\n" +
                "      ThirdEyeResponse thirdEyeResponse = entry.getValue();\n" +
                "      if (CollectionUtils.isEmpty(thirdEyeRequest.getGroupBy())) {\n" +
                "        extractFirstMetricFunctionValues(metricOnlyData, thirdEyeResponse);\n" +
                "      }\n" +
                "\n" +
                "    }\n" +
                "    return metricOnlyData;\n" +
                "  }";
        String method2 = "Map<MetricFunctions, Double> processAggregatesResponse(\n" +
                "            Map<ThirdEyeRequest, ThirdEyeResponse> queryResultsMap) {\n" +
                "        Map<MetricFunctions, Double> metricOnlyDatas = new HashMap<>();\n" +
                "        for (Entry<ThirdEyeRequest, ThirdEyeResponse> entry : queryResultsMap.entrySet()) {\n" +
                "            ThirdEyeRequest thirdEyeRequest = entry.getKey();\n" +
                "            ThirdEyeResponse thirdEyeResponse = entry.getValue();\n" +
                "            if (CollectionUtils.isEmpty(thirdEyeRequest.getGroupBy())) {\n" +
                "                extractFirstMetricFunctionValues(metricOnlyDatas, thirdEyeResponse);\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "        return metricOnlyDatas;\n" +
                "    }";
        RenameExtractor extractor = new RenameExtractor();
        List<Refactoring> refs = extractor.extract(method1, method2);
        assertEquals(7, refs.size());
        for (Refactoring ref : refs){
            System.out.println(ref);
        }
    }
}