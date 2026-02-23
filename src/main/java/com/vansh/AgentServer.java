package com.vansh;

import static spark.Spark.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class AgentServer {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        FirestoreConfig.initFirestore();  
        port(getPort());

        post("/chat", (req, res) -> {
            res.type("application/json");

            Map<String, String> body = mapper.readValue(req.body(), Map.class);
            String email = body.get("email");
            String message = body.get("message");

            if (email == null || email.isEmpty()) {
                return mapper.writeValueAsString(Map.of("error", "Email is required"));
            }

            List<Map<String, String>> memory = MemoryStore.load(email);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
            messages.addAll(memory);
            messages.add(Map.of("role", "user", "content", message));

            String reply = LLMClient.chat(messages);

            memory.add(Map.of("role", "user", "content", message));
            memory.add(Map.of("role", "assistant", "content", reply));
            MemoryStore.save(email, memory);

            return mapper.writeValueAsString(Map.of("reply", reply));
        });
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        return port != null ? Integer.parseInt(port) : 8080;
    }
}