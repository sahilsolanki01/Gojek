package com.solanki.sahil.gojek.ui.success;

import androidx.lifecycle.ViewModel;

import com.solanki.sahil.gojek.data.model.Items;

public class SuccessViewModel extends ViewModel {
    public String day, temp, city, temp_today;

    public SuccessViewModel(Items items) {

        this.day = items.day;
        this.temp = items.temp;

    }

    public SuccessViewModel() {
    }


}
