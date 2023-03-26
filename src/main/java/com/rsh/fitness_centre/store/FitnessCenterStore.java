package com.rsh.fitness_centre.store;

import com.rsh.fitness_centre.entity.FitnessCentre;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FitnessCenterStore {

  //Fitness Center to slot to workout mapping
  private final Map<Integer, FitnessCentre> fitnessCentres = new HashMap<>();
  private final Map<String, FitnessCentre> fitnessCentresByName = new HashMap<>();

  public void addFitnessCentre(FitnessCentre fitnessCentre) {
    fitnessCentres.put(fitnessCentre.getId(), fitnessCentre);
    fitnessCentresByName.put(fitnessCentre.getName(), fitnessCentre);
  }

  public FitnessCentre getFitnessCentre(String name) {
    return fitnessCentresByName.get(name);
  }

  public Set<FitnessCentre> getFitnessCentres() {
    Set<FitnessCentre> centres = new HashSet<>();
    fitnessCentresByName.forEach((k, v) -> centres.add(v));
    return centres;
  }
}
