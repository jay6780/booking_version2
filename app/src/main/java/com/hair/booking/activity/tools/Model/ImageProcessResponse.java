package com.hair.booking.activity.tools.Model;

public class ImageProcessResponse {
    private ImageResponse image_process_response;

    public ImageResponse getImageProcessResponse() {
        return image_process_response;
    }

    public static class ImageResponse {
        private String request_id;
        private String status;
        private String duration;          // Added field for duration
        private String total_duration;     // Added field for total duration
        private String result_url;         // Added field for result URL
        private String description;        // You can keep this if you need it
        private String err_code;           // You can keep this if you need it

        public String getRequestId() {
            return request_id;
        }

        public String getStatus() {
            return status;
        }

        public String getDuration() {
            return duration;
        }

        public String getTotalDuration() {
            return total_duration;
        }

        public String getResultUrl() {
            return result_url;
        }

        // Add getters for other fields if needed
    }
}
