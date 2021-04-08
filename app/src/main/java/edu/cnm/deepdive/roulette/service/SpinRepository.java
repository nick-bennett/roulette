package edu.cnm.deepdive.roulette.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.roulette.model.dao.SpinDao;
import edu.cnm.deepdive.roulette.model.dao.StatisticsDao;
import edu.cnm.deepdive.roulette.model.dao.WagerDao;
import edu.cnm.deepdive.roulette.model.entity.Spin;
import edu.cnm.deepdive.roulette.model.entity.Wager;
import edu.cnm.deepdive.roulette.model.pojo.SpinWithWagers;
import edu.cnm.deepdive.roulette.model.view.ValueCount;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.Iterator;
import java.util.List;

/**
 * Implements standard persistence operations on {@link Spin} instances. In some cases, these
 * operations also involve the related {@link Wager} child entity instances. All persistence
 * operations are performed on non-UI threads.
 */
public class SpinRepository {

  private final Context context;
  private final SpinDao spinDao;
  private final WagerDao wagerDao;
  private final StatisticsDao statisticsDao;

  /**
   * Initializes this instance with all supporting references to data-access objects (DAOs) needed
   * for persistence operations.
   *
   * @param context Android context, needed for access outside the app's "sandbox".
   */
  public SpinRepository(Context context) {
    this.context = context;
    RouletteDatabase database = RouletteDatabase.getInstance();
    spinDao = database.getSpinDao();
    wagerDao = database.getWagerDao();
    statisticsDao = database.getStatisticsDao();
  }

  /**
   * Persists a {@link Spin} instance, along with all of its dependent {@link Wager wagers}.
   * Actually, both of these types are incorporated into the {@code spin} parameter.
   *
   * @param spin POJO incorporating a {@link Spin} &amp; related {@link Wager wagers}.
   * @return ReactiveX {@link Single Single&lt;SpinWithWagers&gt;} task.
   */
  public Single<SpinWithWagers> save(SpinWithWagers spin) {
    if (spin.getId() > 0) {
      // Update
      return spinDao
          .update(spin)
          .map((ignored) -> spin)
          .subscribeOn(Schedulers.io());
    } else {
      // Insert
      return spinDao
          .insert(spin)
          .flatMap((spinId) -> {
            spin.setId(spinId);
            for (Wager wager : spin.getWagers()) {
              wager.setSpinId(spinId);
            }
            return wagerDao.insert(spin.getWagers());
          })
          .map((wagerIds) -> {
            Iterator<Long> idIterator = wagerIds.iterator();
            Iterator<Wager> wagerIterator = spin.getWagers().iterator();
            while (idIterator.hasNext() && wagerIterator.hasNext()) {
              wagerIterator.next().setId(idIterator.next());
            }
            return spin;
          })
          .subscribeOn(Schedulers.io());
    }
  }

  public Completable delete(Spin spin) {
    return (
        (spin.getId() == 0)
            ? Completable.complete()
            : spinDao
                .delete(spin)
                .ignoreElement()
    )
        .subscribeOn(Schedulers.io());
  }

  public LiveData<List<Spin>> getAll() {
    return spinDao.selectAll();
  }

  public LiveData<SpinWithWagers> get(long spinId) {
    return spinDao.selectById(spinId);
  }

  public LiveData<List<ValueCount>> getCounts() {
    return statisticsDao.selectCounts();
  }

}
