package com.busbooking.app.test;

// Test to verify if Retrofit dependencies are available
public class DependencyTest {

    // Test if Retrofit classes can be imported
    public void testDependencies() {
        try {
            // This should compile if Retrofit is available
            Class<?> retrofitClass = Class.forName("retrofit2.Retrofit");
            Class<?> okhttpClass = Class.forName("okhttp3.OkHttpClient");
            Class<?> gsonClass = Class.forName("com.google.gson.Gson");

            System.out.println("Retrofit dependencies are available");
            System.out.println("Retrofit: " + retrofitClass.getName());
            System.out.println("OkHttp: " + okhttpClass.getName());
            System.out.println("Gson: " + gsonClass.getName());

        } catch (ClassNotFoundException e) {
            System.err.println("Missing dependency: " + e.getMessage());
        }
    }
}
