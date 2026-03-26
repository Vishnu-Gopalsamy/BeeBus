package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.models.api.AuthData;
import com.busbooking.app.utils.SessionManager;
import com.busbooking.app.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance(this);
        
        // If already logged in, go to Home
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // Hide unimplemented features
        View btnLoginGoogle = findViewById(R.id.btn_login_google);
        if (btnLoginGoogle != null) btnLoginGoogle.setVisibility(View.GONE);
        View tvForgotPassword = findViewById(R.id.tv_forgot_password);
        if (tvForgotPassword != null) tvForgotPassword.setVisibility(View.GONE);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void observeViewModel() {
        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            setLoadingState(isLoading);
        });

        // Observe errors
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                authViewModel.clearError();
            }
        });

        // Observe auth data for saving session and navigating
        authViewModel.getAuthData().observe(this, authData -> {
            if (authData != null && authData.getUser() != null) {
                // Save session
                sessionManager.saveAuthToken(authData.getToken());
                sessionManager.saveUserData(
                    authData.getUser().get_id(),
                    authData.getUser().getName(),
                    authData.getUser().getEmail(),
                    authData.getUser().getPhone(),
                    authData.getUser().getRole()
                );

                Toast.makeText(this, "Welcome, " + authData.getUser().getName() + "!",
                    Toast.LENGTH_SHORT).show();
                
                navigateToHome();
            }
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Check network before attempting login
        if (!com.busbooking.app.utils.NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
            return;
        }

        // Default login for testing/admin
        if (email.equals("admin") && password.equals("admin")) {
            // Use actual admin credentials to authenticate with backend
            authViewModel.login("admin@busbooking.com", "admin123");
            return;
        }
        
        // Default login for owner
        if (email.equals("owner") && password.equals("owner")) {
            authViewModel.login("owner@gmail.com", "123");
            return;
        }

        // Validate input
        if (!validateInput(email, password)) {
            return;
        }

        // Call ViewModel login
        authViewModel.login(email, password);
    }

    private boolean validateInput(String email, String password) {
        etEmail.setError(null);
        etPassword.setError(null);

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 3) {
            etPassword.setError("Password must be at least 3 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoadingState(boolean loading) {
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);

        btnLogin.setText(loading ? "Logging in..." : "Login");
    }

    private void navigateToHome() {
        Intent intent;
        String role = sessionManager.getUserRole();
        
        if ("admin".equals(role)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else if ("owner".equals(role)) {
            intent = new Intent(this, OwnerDashboardActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
