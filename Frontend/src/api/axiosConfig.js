import axios from "axios";

const instance = axios.create({
  baseURL: "http://localhost:8080", // Set the base URL to your local API
  timeout: 5000, // Set a timeout for requests (optional)
  headers: {
    "Content-Type": "application/json", // Set default headers
  },
});

// Add a response interceptor for error handling
instance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Handle network errors
    if (!error.response) {
      console.error("Network Error:", error.message);
      // Display a user-friendly error message or retry the request
    }
    // Handle HTTP errors (status codes)
    if (error.response) {
      console.error("HTTP Error:", error.response.status, error.response.data);
      // Handle specific HTTP error codes or display error messages
    }
    return Promise.reject(error);
  }
);

export default instance;
