package com.solanki.sahil.gojek.ui.failure;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solanki.sahil.gojek.R;
import com.solanki.sahil.gojek.databinding.FailureFragmentBinding;

public class FailureFragment extends Fragment {

    private FailureViewModel mViewModel;


    public static FailureFragment newInstance() {
        return new FailureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = ViewModelProviders.of(this).get(FailureViewModel.class);
        FailureFragmentBinding bindingUtil = DataBindingUtil.inflate(inflater, R.layout.failure_fragment, container, false);
        bindingUtil.setModel(mViewModel);
        bindingUtil.setLifecycleOwner(this);
        return bindingUtil.getRoot();
    }


}
