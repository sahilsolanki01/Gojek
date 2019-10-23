package com.solanki.sahil.gojek.ui.home;

import androidx.lifecycle.LiveData;

public interface MainListener {

    void onSuccess(LiveData<String> response);
}
