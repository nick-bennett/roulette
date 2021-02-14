package edu.cnm.deepdive.roulette.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.roulette.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.roulette.viewmodel.HomeViewModel;
import java.util.Random;

public class HomeFragment extends Fragment {

  private static final int MIN_ROTATION_TIME = 2000;
  private static final int MAX_ROTATION_TIME = 5000;
  private static final int DEGREES_PER_REVOLUTION = 360;
  private static final int MIN_FULL_ROTATIONS = 3;
  private static final int MAX_FULL_ROTATIONS = 5;

  private FragmentHomeBinding binding;
  private HomeViewModel homeViewModel;
  private boolean spinning;
  private Random rng;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    rng = new Random();
  }

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    binding.spinWheel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        spinWheel();
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
    homeViewModel.getPocketIndex().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer pocketIndex) {
        startAnimation(pocketIndex);
      }
    });
  }

  private void spinWheel() {
    if (!spinning) {
      spinning = true;
      binding.spinWheel.setEnabled(false);
      binding.rouletteValue.setVisibility(View.INVISIBLE);
      homeViewModel.spinWheel();
    }
  }

  private void startAnimation(Integer pocketIndex) {
    float centerX = binding.rouletteWheel.getWidth() / 2f;
    float centerY = binding.rouletteWheel.getHeight() / 2f;
    float currentRotation = binding.rouletteWheel.getRotation();
    float finalRotation =
        -DEGREES_PER_REVOLUTION * pocketIndex / (float) HomeViewModel.POCKETS_ON_WHEEL;
    binding.rouletteWheel.setPivotX(centerX);
    binding.rouletteWheel.setPivotY(centerY);
    RotateAnimation rotation = new RotateAnimation(0, (finalRotation - currentRotation)
        - DEGREES_PER_REVOLUTION * (MIN_FULL_ROTATIONS
        + rng.nextInt(MAX_FULL_ROTATIONS - MIN_FULL_ROTATIONS + 1)), centerX, centerY);
    rotation.setDuration(
        MIN_ROTATION_TIME + rng.nextInt(MAX_ROTATION_TIME - MIN_ROTATION_TIME));
    rotation.setAnimationListener(new AnimationFinalizer(finalRotation));
    binding.rouletteWheel.startAnimation(rotation);
  }

  private class AnimationFinalizer implements AnimationListener {

    private final float finalRotation;

    private AnimationFinalizer(float finalRotation) {
      this.finalRotation = finalRotation;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
      binding.rouletteWheel.setRotation(finalRotation);
      spinning = false;
      binding.spinWheel.setEnabled(true);
      binding.rouletteValue.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

  }

}