package com.vansh;
// memory handling
import java.util.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

public class MemoryStore {
    private static final String COLLECTION = "memory";

    static {
        FirestoreConfig.initFirestore();
    }
    
    public static List<Map<String, String>> load(String email) {
        List<Map<String, String>> memory = new ArrayList<>();
        System.out.println("[DEBUG] MemoryStore.load: email=" + email);
        Firestore firestore = FirestoreConfig.getFirestore();

        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(email);
            System.out.println("[DEBUG] MemoryStore.load: fetching document");
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot doc = future.get();
            if (doc.exists()) {
                System.out.println("[DEBUG] MemoryStore.load: document exists");
                List<Map<String, String>> messages = (List<Map<String, String>>) doc.get("messages");
                if (messages != null) {
                    memory.addAll(messages);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] MemoryStore.load: failed for " + email);
            e.printStackTrace();
        }

        return memory;
    }

    public static void save(String email, List<Map<String, String>> memory) {
        System.out.println("[DEBUG] MemoryStore.save: email=" + email + " size=" + memory.size());
        Firestore firestore = FirestoreConfig.getFirestore();

        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(email);
            Map<String, Object> data = Map.of("messages", memory);
            ApiFuture<?> future = docRef.set(data);
            future.get();
            System.out.println("[DEBUG] MemoryStore.save: write complete");
        } catch (Exception e) {
            System.err.println("[ERROR] MemoryStore.save: failed for " + email);
            e.printStackTrace();
        }
    }

}