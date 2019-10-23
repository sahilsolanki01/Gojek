package com.solanki.sahil.gojek.ui.home;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.solanki.sahil.gojek.data.repository.UserRepository;

public class MainViewModel extends ViewModel {
    private UserRepository userRepository;
    public MainListener mainListener;


    public MainViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public void load(){
        Log.e("chech1($($*", "MainViewModel: ");
        LiveData<String> response = userRepository.result();
        mainListener.onSuccess(response);
        Log.e("chech2($($*", "MainViewModel: ");
    }

}
