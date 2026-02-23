package com.vansh;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class FirestoreConfig {
    private static Firestore firestore;
    private static final String FIREBASE_FILE = "firebase_key_memory_store.json";

    public static void initFirestore() {
        try {
            writeFirebaseKeyIfNeeded();

            firestore = FirestoreOptions.newBuilder()
                .setProjectId("ai-agent-f4638")
                .setCredentials(
                    GoogleCredentials.fromStream(
                        new java.io.FileInputStream(FIREBASE_FILE)
                    )
                )
                .build()
                .getService();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeFirebaseKeyIfNeeded() throws Exception {
        File f = new File(FIREBASE_FILE);
        if (f.exists()) return;

        String firebaseJson = System.getenv("FIREBASE_KEY_JSON");
        if (firebaseJson == null || firebaseJson.isEmpty()) {
            throw new RuntimeException("FIREBASE_KEY_JSON env variable not set");
        }

        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(firebaseJson.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static Firestore getFirestore() {
        if (firestore == null) initFirestore();
        return firestore;
    }
}