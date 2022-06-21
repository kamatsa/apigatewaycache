package com.aws.poc.lambda.apigatewaycache;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.Op;
import com.amazonaws.services.apigateway.model.PatchOperation;
import com.amazonaws.services.apigateway.model.UpdateStageRequest;
import com.amazonaws.services.apigateway.model.UpdateStageResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class ApiGatewayCacheFunctionHandler implements RequestHandler<ApiGwCacheAndStoreEnvVariables, String> {

    @Override
    public String handleRequest(ApiGwCacheAndStoreEnvVariables input, Context context) {
        context.getLogger().log("Input: " + input);

        String restApiId = input.getRestApiId();
        String stageName = input.getStageName();
        Boolean enable = input.getEnable();

        System.out.println("restApi:" + restApiId + " stageName:" + stageName + " enable:" + enable);
        String region = System.getenv("region");
        AmazonApiGateway apiGwClient = AmazonApiGatewayClientBuilder.standard().withRegion(region).build();
        PatchOperation patchOp = null;

        UpdateStageRequest upStgReq = null;
        UpdateStageResult upStgRes = null;

        if (enable) {


            PatchOperation patchOp2 = new PatchOperation().withOp(Op.Replace).withPath("/*/*/caching/enabled")
                    .withValue("True");
            upStgReq = new UpdateStageRequest().withRestApiId(restApiId).withStageName(stageName)
                    .withPatchOperations(patchOp2);
            upStgRes = apiGwClient.updateStage(upStgReq);

            patchOp = new PatchOperation().withOp(Op.Replace).withPath("/cacheClusterEnabled").withValue("True");
            PatchOperation pathOpTtl = new PatchOperation().withOp(Op.Replace).withPath("/*/*/caching/ttlInSeconds").withValue("3600");
            List<PatchOperation> opsList = new ArrayList<PatchOperation>();
            PatchOperation patchOp4 = new PatchOperation().withOp(Op.Replace).withPath("/cacheClusterSize").withValue("0.5");
            opsList.add(patchOp);
            opsList.add(patchOp4);
            opsList.add(pathOpTtl);
            System.out.println("The list parameters are " + opsList.toString());
            upStgReq = new UpdateStageRequest().withRestApiId(restApiId).withStageName(stageName)
                    .withPatchOperations(opsList);
            upStgRes = apiGwClient.updateStage(upStgReq);
            System.out.println("Enabling stage cache " + stageName + ": " + upStgRes.getCacheClusterStatus());
            System.out.println("Enabling cache on functions  " + stageName + ": " + upStgRes.getCacheClusterStatus());


            PatchOperation patchOp3 = new PatchOperation().withOp(Op.Replace)
                    .withPath("/pets/{petId}/GET/caching/enabled").withValue("False");
            upStgReq = new UpdateStageRequest().withRestApiId(restApiId).withStageName(stageName)
                    .withPatchOperations(patchOp3);
            upStgRes = apiGwClient.updateStage(upStgReq);


        } else {
            patchOp = new PatchOperation().withOp(Op.Replace).withPath("/cacheClusterEnabled").withValue("False");
            upStgReq = new UpdateStageRequest().withRestApiId(restApiId).withStageName(stageName)
                    .withPatchOperations(patchOp);
            upStgRes = apiGwClient.updateStage(upStgReq);
            System.out.println("Disabling cache " + stageName + ": " + upStgRes.getCacheClusterStatus());
        }


        return upStgRes.getCacheClusterStatus();

    }


}
