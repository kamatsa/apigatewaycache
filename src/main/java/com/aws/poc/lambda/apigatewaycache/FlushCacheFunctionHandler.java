package com.aws.poc.lambda.apigatewaycache;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.FlushStageCacheRequest;
import com.amazonaws.services.apigateway.model.FlushStageCacheResult;
import com.amazonaws.services.apigateway.model.GetStageRequest;
import com.amazonaws.services.apigateway.model.GetStageResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.google.gson.Gson;

public class FlushCacheFunctionHandler implements RequestHandler<SNSEvent, String> {

    @Override
    public String handleRequest(SNSEvent event, Context context) {

        String message = event.getRecords().get(0).getSNS().getMessage();
        System.out.println("The sns message is " + message);
        Gson gson = new Gson();
        ApiGwCacheAndStoreEnvVariables request = gson.fromJson(message, ApiGwCacheAndStoreEnvVariables.class);
        String restApiId = request.getRestApiId();
        String stageName = request.getStageName();
        String region = System.getenv("region");
        AmazonApiGateway apiGwClient = AmazonApiGatewayClientBuilder.standard().withRegion(region).build();
        FlushStageCacheRequest flushStgCReq = new FlushStageCacheRequest().withRestApiId(restApiId)
                .withStageName(stageName);
        FlushStageCacheResult flushResult = apiGwClient.flushStageCache(flushStgCReq);
        GetStageRequest stgReq = new GetStageRequest().withRestApiId(restApiId).withStageName(stageName);
        GetStageResult stgRes = apiGwClient.getStage(stgReq);
        String cacheClusterStatus = stgRes.getCacheClusterStatus();
        return flushResult.toString();

    }

}
