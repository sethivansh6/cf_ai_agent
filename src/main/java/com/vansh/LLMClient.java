package com.vansh;

import java.net.http.*;
import java.net.URI;
import java.util.*;
import com.fasterxml.jackson.databind.*;

public class LLMClient {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CHAT_URL = "https://router.huggingface.co/v1/chat/completions";

    public static String chat(List<Map<String, String>> messages) throws Exception {
        String apiKey = Config.getHFKey();

        List<Map<String, String>> hfMessages = new ArrayList<>();
        for (Map<String, String> m : messages) {
            Map<String, String> msg = new HashMap<>();
            msg.put("role", m.get("role"));
            msg.put("content", m.get("content")); 
            hfMessages.add(msg);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "openai/gpt-oss-120b:fastest");
        payload.put("messages", hfMessages);
        payload.put("stream", false);

        String jsonPayload = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = mapper.readTree(response.body());
        if (root.has("choices") && root.get("choices").size() > 0) {
            return root.get("choices").get(0).get("message").get("content").asText();
        }

        throw new RuntimeException("Unexpected response from Hugging Face API");
    }
}