package edu.sjsu.android.productiv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


public class sign_in extends Fragment {

    public sign_in() {
    }

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

        // Navigate to to-do list when clicked
        btnSignIn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_todoListFragment);
        });

        // Navigate to sign up when sing up is clicked
        btnSignUpNavigate.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_signUpFragment);
        });
    }
}