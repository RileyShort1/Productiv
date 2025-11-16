package edu.sjsu.android.productiv;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

public class timer extends Fragment {
    private EditText editTime;
    private CountDownTimer timer;
    private boolean isRunning;
    private long total;

    public timer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        Button btnStart = view.findViewById(R.id.btn_start);
        Button btnPause = view.findViewById(R.id.btn_pause);

        editTime = view.findViewById(R.id.edit_time);

        MaskedTextChangedListener listener = new MaskedTextChangedListener(
                "[00]:[00]:[00]", editTime, null, null
        );
        editTime.addTextChangedListener(listener);
        editTime.setOnFocusChangeListener(listener);

        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());

        return view;
    }

    public void startTimer() {
        editTime.setEnabled(false);

        if (isRunning) {
            return;
        }

        String time = editTime.getText().toString();
        String [] parts = time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        total = ((hours * 3600) + (minutes * 60) + seconds) * 1000L;

        timer = new CountDownTimer(total, 1000) {
            @Override
            public void onFinish() {
                editTime.setText("00:00:00");
                Toast.makeText(getContext(), "Timer Finished!", Toast.LENGTH_SHORT).show();
                editTime.setEnabled(true);
                isRunning = false;
            }

            @Override
            public void onTick(long millisUntilFinished) {
                total = millisUntilFinished;
                int remaining = (int) (millisUntilFinished / 1000);
                int hours = remaining / 3600;
                int minutes = (remaining % 3600) / 60;
                int seconds = remaining % 60;
                editTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }
        }.start();
        isRunning = true;
    }

    public void pauseTimer() {
        editTime.setEnabled(true);

        if (timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }
}