package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.repository.FitnessCentreRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FitnessCentreService {

  private final FitnessCentreRepository fitnessCentreRepository;
  private final SlotRepository slotRepository;

  @Autowired
  public FitnessCentreService(
      FitnessCentreRepository fitnessCentreRepository,
      SlotRepository slotRepository) {
    this.fitnessCentreRepository = fitnessCentreRepository;
    this.slotRepository = slotRepository;
  }

  @Transactional
  public FitnessCentre addCentre(
      String name, Set<List<Integer>> timings, Set<Activity> supportedActivities) {
    FitnessCentre fitnessCentre = new FitnessCentre(null, name);
    return fitnessCentreRepository.save(fitnessCentre);
  }

  public FitnessCentre getCentreByName(String name) {
    return fitnessCentreRepository.findByName(name).orElse(null);
  }

  public FitnessCentre getCentreById(Long centreId) {
    return fitnessCentreRepository.findById(centreId).orElse(null);
  }

  public Set<Slot> getSlotsOfADay(Long centreId, LocalDate date) {
    return new HashSet<>(slotRepository.getSlotsByDate(centreId, date));
  }

  @Transactional
  public Slot addActivity(
      Long fitnessCentreId, Activity activity, int startTime, int endTime, int noOfSlots) {
    Optional<FitnessCentre> centreOpt = fitnessCentreRepository.findById(fitnessCentreId);
    if (centreOpt.isEmpty()) {
      return null;
    }

    FitnessCentre centre = centreOpt.get();
    Slot fitnessCentreSlot =
        new Slot(
            null,
            LocalDate.now(),
            activity,
            startTime,
            endTime,
            noOfSlots,
            centre);
    return slotRepository.save(fitnessCentreSlot);
  }

  public Set<FitnessCentre> getAllCentres() {
    return new HashSet<>(fitnessCentreRepository.findAll());
  }

  public Set<Slot> getCentreActivities(Long fitnessCentreId) {
    return new HashSet<>(slotRepository.getSlotsByCenter(fitnessCentreId));
  }
}
