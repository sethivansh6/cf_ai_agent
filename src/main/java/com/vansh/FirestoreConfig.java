package com.vansh;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FirestoreConfig {
    private static Firestore firestore;

    public static void initFirestore() {
        try {
            File f = new File("./firebase_key_memory_store.json");

            InputStream serviceAccount;
            if (f.exists()) {
                serviceAccount = new FileInputStream(f);
            } else {
                String json = System.getenv("FIREBASE_KEY_JSON");
                if (json == null || json.isEmpty()) {
                    throw new RuntimeException("FIREBASE_KEY_JSON not set");
                }
            serviceAccount = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        }

            firestore = FirestoreOptions.newBuilder()
                    .setProjectId(Config.getFireBaseProjectId())
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()
                    .getService();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firestore");
        }
    }

    public static Firestore getFirestore() {
        if (firestore == null) initFirestore();
        return firestore;
    }
}