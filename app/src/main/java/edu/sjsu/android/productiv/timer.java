package edu.sjsu.android.productiv;

import android.content.Context;
import android.content.SharedPreferences;
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
    private static final String PREFS_NAME = "TimerPrefs";
    private static final String KEY_TIMER_END_TIME = "timer_end_time";
    private static final String KEY_IS_RUNNING = "timer_is_running";
    
    private EditText editTime;
    private CountDownTimer timer;
    private boolean isRunning;
    private long total;

    public timer() {
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
        Button btnClear = view.findViewById(R.id.btn_clear);

        editTime = view.findViewById(R.id.edit_time);

        MaskedTextChangedListener listener = new MaskedTextChangedListener(
                "[00]:[00]:[00]", editTime, null, null
        );
        editTime.addTextChangedListener(listener);
        editTime.setOnFocusChangeListener(listener);

        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnClear.setOnClickListener(v -> clearTimer());

        restoreTimerState();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isRunning) {
            restoreTimerState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editTime = null;
    }

    public void startTimer() {
        if (isRunning) {
            return;
        }

        String time = editTime.getText().toString().trim();
        
        if (time.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] parts = time.split(":");
        if (parts.length != 3) {
            Toast.makeText(getContext(), "Invalid time format. Use HH:MM:SS", Toast.LENGTH_SHORT).show();
            return;
        }

        int hours, minutes, seconds;
        try {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[1]);
            seconds = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid time format. Use numbers only", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hours < 0) {
            Toast.makeText(getContext(), "Hours cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }
        if (minutes < 0 || minutes > 59) {
            Toast.makeText(getContext(), "Minutes must be between 0 and 59", Toast.LENGTH_SHORT).show();
            return;
        }
        if (seconds < 0 || seconds > 59) {
            Toast.makeText(getContext(), "Seconds must be between 0 and 59", Toast.LENGTH_SHORT).show();
            return;
        }

        total = ((hours * 3600) + (minutes * 60) + seconds) * 1000L;
        if (total <= 0) {
            Toast.makeText(getContext(), "Time must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        editTime.setEnabled(false);

        long endTime = System.currentTimeMillis() + total;
        saveTimerState(endTime, true);

        timer = new CountDownTimer(total, 1000) {
            @Override
            public void onFinish() {
                if (editTime != null && getView() != null) {
                    editTime.setText("00:00:00");
                    editTime.setEnabled(true);
                    Toast.makeText(getContext(), "Timer Finished!", Toast.LENGTH_SHORT).show();
                }
                isRunning = false;
                clearTimerState();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (editTime == null || getView() == null) {
                    return;
                }
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
        clearTimerState();
    }

    public void clearTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isRunning = false;

        if (editTime != null) {
            editTime.setText("00:00:00");
            editTime.setEnabled(true);
        }

        clearTimerState();
    }

    private void saveTimerState(long endTime, boolean running) {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_TIMER_END_TIME, endTime);
        editor.putBoolean(KEY_IS_RUNNING, running);
        editor.apply();
    }

    private void clearTimerState() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TIMER_END_TIME);
        editor.remove(KEY_IS_RUNNING);
        editor.apply();
    }

    private void restoreTimerState() {
        if (getContext() == null || isRunning) return;

        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean wasRunning = prefs.getBoolean(KEY_IS_RUNNING, false);
        
        if (!wasRunning) {
            return;
        }

        long endTime = prefs.getLong(KEY_TIMER_END_TIME, 0);
        if (endTime == 0) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long remainingTime = endTime - currentTime;

        if (remainingTime <= 0) {
            editTime.setText("00:00:00");
            editTime.setEnabled(true);
            clearTimerState();
            Toast.makeText(getContext(), "Timer Finished!", Toast.LENGTH_SHORT).show();
            return;
        }

        editTime.setEnabled(false);
        total = remainingTime;

        timer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onFinish() {
                if (editTime != null && getView() != null) {
                    editTime.setText("00:00:00");
                    editTime.setEnabled(true);
                    Toast.makeText(getContext(), "Timer Finished!", Toast.LENGTH_SHORT).show();
                }
                isRunning = false;
                clearTimerState();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (editTime == null || getView() == null) {
                    return;
                }
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
}