package com.solanki.sahil.gojek.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.solanki.sahil.gojek.data.repository.UserRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private UserRepository userRepository;


    public MainViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(userRepository);
    }
}
