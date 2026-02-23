package com.vansh;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class FirestoreConfig {
    private static Firestore firestore;

    public static void initFirestore() {
        try {
            String firebaseJson = Config.getFirebaseKeyJson();

            if (firebaseJson == null || firebaseJson.isEmpty()) {
                throw new RuntimeException("FIREBASE_KEY_JSON not set");
            }

            ByteArrayInputStream serviceAccount =
                new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));

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