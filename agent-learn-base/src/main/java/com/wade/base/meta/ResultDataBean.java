package com.wade.base.meta;

/**
 * @author :lwy
 * @date 2018/8/7 10:59
 * 数据收集的bean
 */
public class ResultDataBean  {

    private String collectTime;

    private String methodName;

    private String totalCount;

    private String maxTime;

    private String averageTime;

    private String minTime;

    private String startMillTime;

    private String stopMillTime;

    private String requestId;


    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public String getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(String averageTime) {
        this.averageTime = averageTime;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getStartMillTime() {
        return startMillTime;
    }

    public void setStartMillTime(String startMillTime) {
        this.startMillTime = startMillTime;
    }

    public String getStopMillTime() {
        return stopMillTime;
    }

    public void setStopMillTime(String stopMillTime) {
        this.stopMillTime = stopMillTime;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String collectTime;
        private String methodName;
        private String totalCount;
        private String maxTime;
        private String averageTime;
        private String minTime;
        private String startMillTime;
        private String stopMillTime;

        private String requestId;

        private Builder() {
        }


        public Builder withCollectTime(String collectTime) {
            this.collectTime = collectTime;
            return this;
        }

        public Builder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder withTotalCount(String totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder withMaxTime(String maxTime) {
            this.maxTime = maxTime;
            return this;
        }

        public Builder withAverageTime(String averageTime) {
            this.averageTime = averageTime;
            return this;
        }

        public Builder withMinTime(String minTime) {
            this.minTime = minTime;
            return this;
        }

        public Builder withStartMillTime(String startMillTime) {
            this.startMillTime = startMillTime;
            return this;
        }

        public Builder withStopMillTime(String stopMillTime) {
            this.stopMillTime = stopMillTime;
            return this;
        }

        public Builder withRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ResultDataBean build() {
            ResultDataBean resultDataBean = new ResultDataBean();
            resultDataBean.setCollectTime(collectTime);
            resultDataBean.setMethodName(methodName);
            resultDataBean.setTotalCount(totalCount);
            resultDataBean.setMaxTime(maxTime);
            resultDataBean.setAverageTime(averageTime);
            resultDataBean.setMinTime(minTime);
            resultDataBean.setStartMillTime(startMillTime);
            resultDataBean.setStopMillTime(stopMillTime);
            resultDataBean.setRequestId(requestId);
            return resultDataBean;
        }
    }

    @Override
    public String toString() {
        return "ResultDataBean{" +
                "collectTime='" + collectTime + '\'' +
                ", methodName='" + methodName + '\'' +
                ", totalCount='" + totalCount + '\'' +
                ", maxTime='" + maxTime + '\'' +
                ", averageTime='" + averageTime + '\'' +
                ", minTime='" + minTime + '\'' +
                ", startMillTime='" + startMillTime + '\'' +
                ", stopMillTime='" + stopMillTime + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
