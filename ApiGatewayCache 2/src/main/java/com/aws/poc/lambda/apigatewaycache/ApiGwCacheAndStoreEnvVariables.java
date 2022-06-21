package com.aws.poc.lambda.apigatewaycache;

public class ApiGwCacheAndStoreEnvVariables {

    String restApiId;
    String stageName;
    Boolean enable;

    public String getRestApiId() {
        return restApiId;
    }

    public void setRestApiId(String restApiId) {
        this.restApiId = restApiId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }


}
