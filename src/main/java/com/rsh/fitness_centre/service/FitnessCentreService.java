package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.store.FitnessCenterSlotStore;
import com.rsh.fitness_centre.store.FitnessCenterStore;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.time.LocalDate;
import java.util.List;import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FitnessCentreService {

  private final SequenceGenerator sequenceGenerator;
  private final FitnessCenterStore fitnessCenterStore;

  private final FitnessCenterSlotStore fitnessCenterSlotStore;

  @Autowired
  public FitnessCentreService(
      SequenceGenerator sequenceGenerator,
      FitnessCenterStore fitnessCenterStore,
      FitnessCenterSlotStore fitnessCenterSlotStore) {
    this.sequenceGenerator = sequenceGenerator;
    this.fitnessCenterStore = fitnessCenterStore;
    this.fitnessCenterSlotStore = fitnessCenterSlotStore;
  }

  public FitnessCentre addCentre(
      String name, Set<List<Integer>> timings, Set<Activity> supportedActivities) {
    FitnessCentre fitnessCentre =
        new FitnessCentre(sequenceGenerator.getNext("FitnessCentre"), name);
    fitnessCenterStore.addFitnessCentre(fitnessCentre);
    return fitnessCentre;
  }

  public FitnessCentre getCentreByName(String name) {
    return fitnessCenterStore.getFitnessCentre(name);
  }

  public Set<Slot> getSlotsOfADay(int centreId, LocalDate date) {
    return fitnessCenterSlotStore.getSlotsForADay(centreId, date);
  }

  // Make checks
  public Slot addActivity(
      int fitnessCentreId, Activity activity, int startTime, int endTime, int noOfSlots) {
    Slot fitnessCentreSlot =
        new Slot(
            sequenceGenerator.getNext("FitnessCentreSlot"),
            LocalDate.now(),
            activity,
            startTime,
            endTime,
            noOfSlots,
            fitnessCentreId);
    fitnessCenterSlotStore.addDataSlot(fitnessCentreSlot);
    return fitnessCentreSlot;
  }

  public Set<FitnessCentre> getAllCentres() {
    return fitnessCenterStore.getFitnessCentres();
  }

  public Set<Slot> getCentreActivities(int fitnessCentreId) {
    return fitnessCenterSlotStore.getSlotsForCentre(fitnessCentreId);
  }
}
