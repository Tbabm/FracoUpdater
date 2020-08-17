import Dataset.RepairResult;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FracoUpdaterTest {
    String method1;
    String method2;
    FracoUpdater updater;

    @Before
    public void setMethods(){
        this.method1 = "public static void testFraco(int longInt){\n" +
                "\tString shortStr;\n" +
                "}\n";
        this.method2 = "public static void testCoraf(int shortInt){\n" +
                "\tString longStr;\n" +
                "}";
        updater = new FracoUpdater();
    }

    @Test
    public void testRepairOne() throws Exception {
        String srcDesc = "This method testFraco gets longInt and return shortStr";
        String destDesc = "This method testCoraf gets shortInt and return longStr";
        RepairResult result = updater.updateOne(method1, method2, srcDesc);
        assertEquals(true, result.isMatched());
        assertEquals(destDesc, result.getResult());
    }

    @Test
    public void testRepairOne_splitTokens() throws Exception {
        String srcDesc = "This method test fraco gets long int and return short str";
        String destDesc = "This method testCorafs gets shortInts and return longStrs";
        RepairResult result = updater.updateOne(method1, method2, srcDesc);
        assertEquals(true, result.isMatched());
        assertEquals(destDesc, result.getResult());
    }

    @Test
    public void testSample1() throws Exception {
        // NOTE: in this case, RefactoringMiner can not find rename or change type refactorings!
        String method1 = "public static HttpPipeline build(HttpPipelineOptions pipelineOptions, Iterable<RequestPolicyFactory> requestPolicyFactories) {\n" +
                "        final HttpPipelineBuilder builder = new HttpPipelineBuilder(pipelineOptions);\n" +
                "        if (requestPolicyFactories != null) {\n" +
                "            for (final RequestPolicyFactory requestPolicyFactory : requestPolicyFactories) {\n" +
                "                builder.withRequestPolicy(requestPolicyFactory);\n" +
                "            }\n" +
                "        }\n" +
                "        return builder.build();\n" +
                "    }";
        String method2 = "public Mono<HttpResponse> send(HttpPipelineCallContext context) {\n" +
                "        // Return deferred to mono for complete lazy behaviour.\n" +
                "        //\n" +
                "        return Mono.defer(() -> {\n" +
                "            NextPolicy next = new NextPolicy(this, context);\n" +
                "            return next.process();\n" +
                "        });\n" +
                "    }";
        String srcDesc = "Build a new HttpPipeline that will use the provided HttpClient and RequestPolicy factories.";
        RepairResult result = updater.updateOne(method1, method2, srcDesc);
        assertEquals(false, result.isMatched());
        System.out.println(result.getResult());
    }

    @Test
    public void testSample2() throws Exception {
        // NOTE: in this case, RefactoringMiner can not find rename or change type refactorings!
        String method1 = "public void setIpConfigurations(List<VirtualMachineScaleSetIPConfiguration> ipConfigurations) {\n" +
                "        this.ipConfigurations = ipConfigurations;\n" +
                "    }";
        String method2 = "public void setProperties(VirtualMachineScaleSetNetworkConfigurationProperties properties) {\n" +
                "        this.properties = properties;\n" +
                "    }";
        String srcDesc = "Set the ipConfigurations value.";
        RepairResult result = updater.updateOne(method1, method2, srcDesc);
        assertEquals(false, result.isMatched());
        System.out.println(result.getResult());
    }

    @Test
    public void testSample3() throws Exception{
        String method1 = "public BigDecimal getPriceActual()\n" +
                "{\n" +
                "BigDecimal bd = (BigDecimal)get_Value(\"PriceActual\");\n" +
                "if (bd == null) return Env.ZERO;\n" +
                "return bd;\n" +
                "}";
        String method2 = "public BigDecimal getQtyInvoiced ()\n" +
                "\t{\n" +
                "\t\tBigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyInvoiced);\n" +
                "\t\tif (bd == null)\n" +
                "\t\t\t return Env.ZERO;\n" +
                "\t\treturn bd;\n" +
                "\t}";
        String srcDesc = "Get Unit Price.";
        RepairResult result = updater.updateOne(method1, method2, srcDesc);
        assertEquals(false, result.isMatched());
        System.out.println(result.getResult());
    }

    @Test
    public void testSample4() throws Exception {
        String method1 = "@Override\n" +
                "\tpublic SyncDLObject fetchByParentFolderId_First(long parentFolderId,\n" +
                "\t\tOrderByComparator<SyncDLObject> orderByComparator) {\n" +
                "\t\tList<SyncDLObject> list = findByParentFolderId(parentFolderId, 0, 1,\n" +
                "\t\t\t\torderByComparator);\n" +
                "\n" +
                "\t\tif (!list.isEmpty()) {\n" +
                "\t\t\treturn list.get(0);\n" +
                "\t\t}\n" +
                "\n" +
                "\t\treturn null;";
        String method2 = "@Override\n" +
                "\tpublic SyncDLObject fetchByM_R_First(long modifiedTime, long repositoryId,\n" +
                "\t\tOrderByComparator<SyncDLObject> orderByComparator) {\n" +
                "\t\tList<SyncDLObject> list = findByM_R(modifiedTime, repositoryId, 0, 1,\n" +
                "\t\t\t\torderByComparator);\n" +
                "\n" +
                "\t\tif (!list.isEmpty()) {\n" +
                "\t\t\treturn list.get(0);\n" +
                "\t\t}\n" +
                "\n" +
                "\t\treturn null;\n" +
                "\t}";
        String srcDesc = "Returns the first sync d l object in the ordered set where parentFolderId = ?.";
        RepairResult result = updater.updateOne(method1, method2, srcDesc);
        assertEquals(false, result.isMatched());
        System.out.println(result.getResult());
    }

    @Test
    public void testSample5() throws Exception {
        String method1 = "public ServiceFuture<List<RouteTableInner>> listAllNextAsync(final String nextPageLink, final ServiceFuture<List<RouteTableInner>> serviceFuture, final ListOperationCallback<RouteTableInner> serviceCallback) {\n" +
                "        return AzureServiceFuture.fromPageResponse(\n" +
                "            listAllNextSinglePageAsync(nextPageLink),\n" +
                "            new Func1<String, Observable<ServiceResponse<Page<RouteTableInner>>>>() {\n" +
                "                @Override\n" +
                "                public Observable<ServiceResponse<Page<RouteTableInner>>> call(String nextPageLink) {\n" +
                "                    return listAllNextSinglePageAsync(nextPageLink);\n" +
                "                }\n" +
                "            },\n" +
                "            serviceCallback);\n" +
                "    }";
        String method2 = "public ServiceFuture<List<RouteTableInner>> listByResourceGroupNextAsync(final String nextPageLink, final ServiceFuture<List<RouteTableInner>> serviceFuture, final ListOperationCallback<RouteTableInner> serviceCallback) {\n" +
                "        return AzureServiceFuture.fromPageResponse(\n" +
                "            listByResourceGroupNextSinglePageAsync(nextPageLink),\n" +
                "            new Func1<String, Observable<ServiceResponse<Page<RouteTableInner>>>>() {\n" +
                "                @Override\n" +
                "                public Observable<ServiceResponse<Page<RouteTableInner>>> call(String nextPageLink) {\n" +
                "                    return listByResourceGroupNextSinglePageAsync(nextPageLink);\n" +
                "                }\n" +
                "            },\n" +
                "            serviceCallback);\n" +
                "    }";
        String srcDesc = "Gets all route tables in a subscription";
        RepairResult result = updater.updateOne(method1, method2, srcDesc);
        assertEquals(false, result.isMatched());
        System.out.println(result.getResult());
    }
}