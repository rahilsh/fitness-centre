package com.rsh.fitness_centre.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SequenceGenerator {

  private final Map<String, Integer> currentValue = new ConcurrentHashMap<>();

  public synchronized int getNext(String key) {
    Integer value = currentValue.get(key);
    if (value == null) {
      currentValue.put(key, 1);
      return 1;
    }
    currentValue.put(key, value + 1);
    return value + 1;
  }
}
