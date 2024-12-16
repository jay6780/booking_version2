package com.hair.booking.activity.tools.Model;

public class ApiResponse {
    private ImageProcessResponse image_process_response;

    public ImageProcessResponse getImageProcessResponse() {
        return image_process_response;
    }

    public static class ImageProcessResponse {
        private String request_id;
        private String status;
        private String result_url;

        public String getRequestId() {
            return request_id;
        }

        public String getStatus() {
            return status;
        }

        public String getResultUrl() {
            return result_url;
        }
    }
}
