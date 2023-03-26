package com.rsh.fitness_centre.store;

import com.rsh.fitness_centre.entity.Slot;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FitnessCenterSlotStore {

  private final Map<Integer, Map<LocalDate, Set<Slot>>> fitnessCenterSlots = new HashMap<>();
  private final Map<Integer, Slot> slotById = new HashMap<>();

  public void addDataSlot(Slot fitnessCentreSlot) {
    slotById.put(fitnessCentreSlot.getId(), fitnessCentreSlot);
    int fitnessCenterId = fitnessCentreSlot.getFitnessCenterId();
    Map<LocalDate, Set<Slot>> daySlots = fitnessCenterSlots.get(fitnessCenterId);
    if (daySlots == null) {
      fitnessCenterSlots.put(fitnessCenterId, new HashMap<>());
      daySlots = fitnessCenterSlots.get(fitnessCenterId);
    }
    if (daySlots.containsKey(fitnessCentreSlot.getDate())) {
      daySlots.computeIfPresent(
          fitnessCentreSlot.getDate(),
          (date, slots) -> {
            // deep copy slots set
            Set<Slot> sts = new HashSet<>(slots);
            sts.add(fitnessCentreSlot);
            return sts;
          });
    } else {
      daySlots.computeIfAbsent(fitnessCentreSlot.getDate(), s -> Set.of(fitnessCentreSlot));
    }
  }

  public Set<Slot> getSlotsForADay(int fitnessCentreId, LocalDate date) {
    Map<LocalDate, Set<Slot>> dateSetMap = fitnessCenterSlots.get(fitnessCentreId);
    if (dateSetMap == null) {
      return new HashSet<>();
    }
    return dateSetMap.get(date);
  }

  public Slot getSlotById(int slotId) {
    return slotById.get(slotId);
  }

  public Set<Slot> getSlotsForCentre(int fitnessCentreId) {

    Set<Slot> slots = new HashSet<>();
    fitnessCenterSlots.get(fitnessCentreId).forEach((k, v) -> slots.addAll(v));
    return slots;
  }
}
