package com.vansh;

import java.util.*;
/**
1. Load memory from file
2. Add new user message
3. Send memory + message to LLM
4. Get response
5. Save everything back to memory
6. Repeat
*/
public class Agent {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Java AI Agent (type 'exit' to quit)\n");

        // Ask for email
        System.out.print("Enter your email ID: ");
        String email = sc.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Email is mandatory!");
            return;
        }

        while (true) {
            System.out.print("You: ");
            String in = sc.nextLine();
            if ("exit".equalsIgnoreCase(in)) break;

            List<Map<String, String>> memory = MemoryStore.load(email);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
            messages.addAll(memory);
            messages.add(Map.of("role", "user", "content", in));

            String reply = LLMClient.chat(messages);

            System.out.println("Agent: " + reply);
            
            memory.add(Map.of("role", "user", "content", in));
            memory.add(Map.of("role", "assistant", "content", reply));
            MemoryStore.save(email, memory);
        }
    }
}