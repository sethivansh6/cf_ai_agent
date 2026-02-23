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
        Firestore firestore = FirestoreConfig.getFirestore();

        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(email);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot doc = future.get();
            if (doc.exists()) {
                List<Map<String, String>> messages = (List<Map<String, String>>) doc.get("messages");
                if (messages != null) {
                    memory.addAll(messages);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return memory;
    }

    public static void save(String email, List<Map<String, String>> memory) {
        Firestore firestore = FirestoreConfig.getFirestore();

        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(email);
            Map<String, Object> data = Map.of("messages", memory);
            docRef.set(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}