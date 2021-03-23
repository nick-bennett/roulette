package edu.cnm.deepdive.roulette.controller;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.LinearLayoutManager;
import edu.cnm.deepdive.roulette.adapter.WagerSpaceAdapter;
import edu.cnm.deepdive.roulette.databinding.FragmentWagerBinding;
import edu.cnm.deepdive.roulette.viewmodel.PlayViewModel;

public class WagerFragment extends Fragment {

  private static final int FULL_WIDTH = 6;
  private static final int ZERO_SPACE_WIDTH = 3;
  private static final int NORMAL_SPACE_WIDTH = 2;

  private FragmentWagerBinding binding;
  private PlayViewModel viewModel;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentWagerBinding.inflate(inflater, container, false);
    GridLayoutManager layoutManager =
        new GridLayoutManager(getContext(), FULL_WIDTH, LinearLayoutManager.VERTICAL, false);
    layoutManager.setSpanSizeLookup(new WagerSpanLookup());
    binding.wagerSpaces.setLayoutManager(layoutManager);
    binding.wagerSpaces.setAdapter(new WagerSpaceAdapter(getContext(),
        (view, position, value) -> Log.d(getClass().getName(), value + " clicked"),
        (view, position, value) -> Log.d(getClass().getName(), value + " long-pressed")
    ));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(PlayViewModel.class);
    // TODO Observe viewModel livedata as appropriate.
  }

  private static class WagerSpanLookup extends SpanSizeLookup {

    @Override
    public int getSpanSize(int position) {
      return (position <= 1) ? ZERO_SPACE_WIDTH : NORMAL_SPACE_WIDTH;
    }

  }
}