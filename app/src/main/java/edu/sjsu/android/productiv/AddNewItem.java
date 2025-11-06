package edu.sjsu.android.productiv;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.time.LocalDate;

public class AddNewItem extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNewItem() {
        // Required empty public constructor
    }


    public static AddNewItem newInstance(String param1, String param2) {
        AddNewItem fragment = new AddNewItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText desc = view.findViewById(R.id.description);
        EditText name = view.findViewById(R.id.itemName);
        EditText priority = view.findViewById(R.id.priority);

        Button myButton = view.findViewById(R.id.addButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create object
                int prty;
                if (priority.getText().toString().isEmpty()) {
                    prty = 4;
                } else {
                    prty = Integer.parseInt(priority.getText().toString());
                }

                // need to fix local date element
                ToDoItem item = new ToDoItem(name.getText().toString(), desc.getText().toString(), LocalDate.now(), prty);

                if (!(name.getText().toString().isEmpty())) {
                    Bundle result = new Bundle();
                    result.putSerializable("result", item);
                    getParentFragmentManager().setFragmentResult("requestKey", result);
                }
                Navigation.findNavController(view).navigate(R.id.action_addNewItem_to_todoListFragment);
            }
        });
    }
}