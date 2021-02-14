package edu.cnm.deepdive.roulette.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.roulette.R;
import java.security.SecureRandom;
import java.util.Random;

public class HomeViewModel extends AndroidViewModel {

  private final MutableLiveData<String> rouletteValue;
  private final MutableLiveData<Integer> pocketIndex;
  private final Random rng;
  private final String[] pocketValues;

  public HomeViewModel(Application application) {
    super(application);
    rouletteValue = new MutableLiveData<>("00");
    pocketIndex = new MutableLiveData<>();
    rng = new SecureRandom();
    pocketValues = application.getResources().getStringArray(R.array.pocket_values);
  }

  public LiveData<String> getRouletteValue() {
    return rouletteValue;
  }

  public void spinWheel() {
    int selection = rng.nextInt(38);
    pocketIndex.setValue(selection);
    rouletteValue.setValue(pocketValues[selection]);
  }

}