package com.solanki.sahil.gojek.ui.failure;

import android.util.Log;
import android.view.View;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.solanki.sahil.gojek.R;


public class FailureViewModel extends ViewModel {


    public void onButtonClicked(View view) {

        Log.e("rttthejco($(*$*$", "hhhhhhhe");
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_failureFragment_to_mainFragment);

    }
}
