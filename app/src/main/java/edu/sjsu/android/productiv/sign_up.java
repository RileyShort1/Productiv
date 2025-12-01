package edu.sjsu.android.productiv;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
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

public class sign_up extends Fragment {
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSignUp = view.findViewById(R.id.btn_sign_up);
        nameEditText = view.findViewById(R.id.edit_text_signup_name);
        emailEditText = view.findViewById(R.id.edit_text_signup_email);
        passwordEditText = view.findViewById(R.id.edit_text_signup_password);

        // Navs to to-do list when clicked
        btnSignUp.setOnClickListener(v -> {signUp(v);});
    }

    public void signUp (View v) {
        ContentValues values = new ContentValues();

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!name.isEmpty() && !password.isEmpty()) {
            values.put("name", name);
            values.put("email", email);
            values.put("password", password);

            UsersDB db = new UsersDB(getContext());
            db.insert(values);

            saveCurrentUser(name, email);

            Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_todoListFragment);
        }
        else {
            Toast.makeText(getContext(), "Name and Password must be provided", Toast.LENGTH_SHORT).show();
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