package edu.sjsu.android.productiv;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavController navController;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppBarConfiguration appBarConfiguration;

    private static final String WORKER_URL = "https://ai-response-getter.rileyshort1.workers.dev/respond";

    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static void onMain(Runnable r) { MAIN.post(r); }

    public interface TextCallback { void onText(String text); }

    public void callAi(String prompt, TextCallback cb) {
        new Thread(() -> {
            String fallback = "Welcome!";
            String result = fallback;
            try {
                // JSON body
                JSONObject body = new JSONObject();
                body.put("prompt", prompt);
                byte[] bytes = body.toString().getBytes("UTF-8");

                // HTTPS POST
                URL url = new URL(WORKER_URL);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(20000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setFixedLengthStreamingMode(bytes.length);

                try (OutputStream os = conn.getOutputStream()) { os.write(bytes); }

                int code = conn.getResponseCode();
                InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
                String resp = readAll(is);

                try { result = new JSONObject(resp).optString("text", fallback); }
                catch (Exception ignored) {}

                conn.disconnect();
            } catch (Exception ignored) {}

            final String text = result;
            onMain(() -> cb.onText(text));
        }).start();
    }

    private static String readAll(InputStream is) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            char[] buf = new char[2048]; int n;
            while ((n = br.read(buf)) != -1) sb.append(buf, 0, n);
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Get nav controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        if (navController == null) {
            return;
        }

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.todoListFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateNavHeader();
        // Listen to nav changes to hide/show drawer
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            // Drawer doesnt show up on sign in and sign up fragments
            if (destinationId == R.id.signInFragment || destinationId == R.id.signUpFragment) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            } else {
                // Show drawer on all other screens
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });

        // Handle back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Let the system handle it
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController == null || navController.getCurrentDestination() == null) {
            return super.onSupportNavigateUp();
        }

        int currentDestinationId = navController.getCurrentDestination().getId();

        if (currentDestinationId == R.id.todoListFragment) {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }

        boolean popped = navController.popBackStack(R.id.todoListFragment, false);
        if (!popped) {
            navController.navigate(R.id.todoListFragment);
        }
        return true;
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = prefs.getString("current_user_name", "User Name");
        String userEmail = prefs.getString("current_user_email", "user@email.com");

        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);
    }
}