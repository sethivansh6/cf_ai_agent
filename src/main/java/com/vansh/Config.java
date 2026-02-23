package com.vansh;
import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.configure()
                                               .directory("./")
                                               .ignoreIfMissing()
                                               .load();

    private static final String FIREBASE_PROJECT_ID = "FIREBASE_PROJECT_ID";
    private static final String HF_API_KEY = "HF_API_KEY";
    private static final String FIREBASE_KEY_JSON = "FIREBASE_KEY_JSON";
    public static String getHFKey() {
        String key = System.getenv(HF_API_KEY);
        if (key != null && !key.isEmpty()) {
            return key;
        }
        return dotenv.get(HF_API_KEY);
    }
    public static String getFireBaseProjectId() {
        String key = System.getenv(FIREBASE_PROJECT_ID);
        if (key != null && !key.isEmpty()) {
            return key;
        }
        return dotenv.get(FIREBASE_PROJECT_ID);
    }
    public static String getFirebaseKeyJson() {
        String json = System.getenv(FIREBASE_KEY_JSON);
        if (json != null && !json.isEmpty()) {
            return json;
        }
        return dotenv.get(FIREBASE_KEY_JSON);
    }
}