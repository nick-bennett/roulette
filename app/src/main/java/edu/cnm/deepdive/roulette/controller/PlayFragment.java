package edu.cnm.deepdive.roulette.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import edu.cnm.deepdive.roulette.databinding.FragmentPlayBinding;
import edu.cnm.deepdive.roulette.viewmodel.PlayViewModel;
import java.util.Random;

public class PlayFragment extends Fragment {

  private static final int MIN_ROTATION_TIME = 2000;
  private static final int MAX_ROTATION_TIME = 5000;
  private static final int DEGREES_PER_REVOLUTION = 360;
  private static final int MIN_FULL_ROTATIONS = 3;
  private static final int MAX_FULL_ROTATIONS = 5;

  private FragmentPlayBinding binding;
  private PlayViewModel playViewModel;
  private boolean spinning;
  private Random rng;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    rng = new Random();
  }

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentPlayBinding.inflate(inflater, container, false);
    binding.spinWheel.setOnClickListener((v) -> spinWheel());
    binding.placeWager.setOnClickListener((v) -> {
      Navigation.findNavController(binding.getRoot())
          .navigate(PlayFragmentDirections.actionNavigationPlayToNavigationWager());
    });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //noinspection ConstantConditions
    playViewModel = new ViewModelProvider(getActivity()).get(PlayViewModel.class);
    getLifecycle().addObserver(playViewModel);
    playViewModel.getRouletteValue().observe(getViewLifecycleOwner(),
        (s) -> binding.rouletteValue.setText(s));
    playViewModel.getPocketIndex().observe(getViewLifecycleOwner(), this::startAnimation);
    playViewModel.getThrowable().observe(getViewLifecycleOwner(), (throwable) -> {
      if (throwable != null) {
        //noinspection ConstantConditions
        Snackbar.make(getContext(), binding.getRoot(), throwable.getMessage(),
            BaseTransientBottomBar.LENGTH_INDEFINITE).show();
      }
    });
  }

  private void spinWheel() {
    if (!spinning) {
      spinning = true;
      binding.spinWheel.setEnabled(false);
      binding.rouletteValue.setVisibility(View.INVISIBLE);
      playViewModel.spinWheel();
    }
  }

  private void startAnimation(Integer pocketIndex) {
    float centerX = binding.rouletteWheel.getWidth() / 2f;
    float centerY = binding.rouletteWheel.getHeight() / 2f;
    float currentRotation = binding.rouletteWheel.getRotation();
    float finalRotation =
        -DEGREES_PER_REVOLUTION * pocketIndex / (float) PlayViewModel.POCKETS_ON_WHEEL;
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