package edu.cnm.deepdive.roulette.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.roulette.R;
import edu.cnm.deepdive.roulette.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.roulette.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

  private FragmentHomeBinding binding;
  private HomeViewModel homeViewModel;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    binding.spinWheel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        homeViewModel.spinWheel();
      }
    });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    homeViewModel.getRouletteValue().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(@Nullable String s) {
        binding.rouletteValue.setText(s);
      }
    });
  }
}