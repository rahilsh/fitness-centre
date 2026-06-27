package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.repository.FitnessCentreRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FitnessCentreService {

  private final SequenceGenerator sequenceGenerator;
  private final FitnessCentreRepository fitnessCentreRepository;
  private final SlotRepository slotRepository;

  @Autowired
  public FitnessCentreService(
      SequenceGenerator sequenceGenerator,
      FitnessCentreRepository fitnessCentreRepository,
      SlotRepository slotRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.fitnessCentreRepository = fitnessCentreRepository;
    this.slotRepository = slotRepository;
  }

  @Transactional
  public FitnessCentre addCentre(
      String name, Set<List<Integer>> timings, Set<Activity> supportedActivities) {
    FitnessCentre fitnessCentre =
        new FitnessCentre(sequenceGenerator.getNext("FitnessCentre"), name);
    fitnessCentreRepository.save(fitnessCentre);
    return fitnessCentre;
  }

  public FitnessCentre getCentreByName(String name) {
    return fitnessCentreRepository.findByName(name).orElse(null);
  }

  public Set<Slot> getSlotsOfADay(int centreId, LocalDate date) {
    return new HashSet<>(slotRepository.getSlotsByDate(centreId, date));
  }

  @Transactional
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
    slotRepository.save(fitnessCentreSlot);
    return fitnessCentreSlot;
  }

  public Set<FitnessCentre> getAllCentres() {
    return new HashSet<>(fitnessCentreRepository.findAll());
  }

  public Set<Slot> getCentreActivities(int fitnessCentreId) {
    return new HashSet<>(slotRepository.getSlotsByCenter(fitnessCentreId));
  }
}
