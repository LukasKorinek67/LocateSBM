package com.korinek.locate_sbm.model.api;


import com.google.gson.annotations.SerializedName;

public class ApiErrorModel {
    @SerializedName("error")
    private ErrorDetails error;

    public ErrorDetails getError() {
        return error;
    }

    public static class ErrorDetails {
        @SerializedName("code")
        private String code;
        @SerializedName("message")
        private String message;
        @SerializedName("time")
        private String time;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getTime() {
            return time;
        }
    }
}
