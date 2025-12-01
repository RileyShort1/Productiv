package edu.sjsu.android.productiv;

import static android.content.Context.MODE_PRIVATE;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


public class sign_in extends Fragment {
    private EditText nameEditText;
    private EditText passwordEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSignIn = view.findViewById(R.id.btn_sign_in);
        Button btnSignUpNavigate = view.findViewById(R.id.btn_sign_up_nav);
        nameEditText = view.findViewById(R.id.edit_text_sign_in_name);
        passwordEditText = view.findViewById(R.id.edit_text_sign_in_password);

        // Navigate to to-do list when clicked
        btnSignIn.setOnClickListener(v -> {signIn(v);});

        // Navigate to sign up when sing up is clicked
        btnSignUpNavigate.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_signUpFragment);
        });
    }

    public void signIn(View v) {
        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Name and Password must be provided", Toast.LENGTH_SHORT).show();
        }

        UsersDB db = new UsersDB(getContext());
        Cursor c = db.getAllUsers(null);
        boolean valid = false;
        String userEmail = "";

        if (c.moveToFirst()) {
            do {
                String dbName = c.getString(c.getColumnIndexOrThrow("name"));
                String dbPassword = c.getString(c.getColumnIndexOrThrow("password"));

                if (name.equals(dbName) && password.equals(dbPassword)) {
                    valid = true;
                    break;
                }
            } while (c.moveToNext());
        }
        c.close();

        if (valid) {
            saveCurrentUser(name, userEmail);
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_todoListFragment);
        } else {
            Toast.makeText(getContext(), "Invalid Name or Password", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveCurrentUser(String name, String email) {
        getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .putString("current_user_name", name)
                .putString("current_user_email", email)
                .apply();
    }
}