package edu.cnm.deepdive.roulette.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.Relation;
import edu.cnm.deepdive.roulette.model.entity.Spin;
import edu.cnm.deepdive.roulette.model.entity.Wager;

public class WagerWithSpin extends Wager {

  @Relation(
      entity = Spin.class,
      entityColumn = "spin_id",
      parentColumn = "spin_id"
  )
  private Spin spin;

  @NonNull
  public Spin getSpin() {
    return spin;
  }

  public void setSpin(Spin spin) {
    this.spin = spin;
  }

}
