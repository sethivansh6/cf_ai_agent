package com.vansh;
import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.configure()
                                               .directory("./")
                                               .load();

    public static String getHFKey() {
        return dotenv.get("HF_API_KEY");
    }
    public static String getFirebaseKeyPath() {
        return dotenv.get("FIREBASE_KEY_JSON");
    }
}