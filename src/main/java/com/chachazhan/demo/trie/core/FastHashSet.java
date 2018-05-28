package com.chachazhan.demo.trie.core;

import lombok.val;

import java.io.Serializable;
import java.util.*;

/**
 * @author jack
 * @date 2018/5/28
 * @time 19:36
 */
public class FastHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {

  private transient HashMap<E, Object> map;

  public FastHashSet() {
    map = new HashMap<>();
  }

  public FastHashSet(Collection<? extends E> collection) {
    map = new HashMap<>(Math.max((int) (collection.size() / .75F) + 1, 16));
    addAll(collection);
  }

  public FastHashSet(int initialCapacity, float loadFactor) {
    map = new HashMap<>(initialCapacity, loadFactor);
  }

  public FastHashSet(int initialCapacity) {
    map = new HashMap<>(initialCapacity);
  }

  @Override
  public Iterator<E> iterator() {
    return map.keySet().iterator();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return map.containsKey(o);
  }

  @Override
  public boolean add(E e) {
    return map.put(e, null) == null;
  }

  @Override
  public boolean remove(Object o) {
    return map.remove(o) == null;
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected FastHashSet clone() {
    FastHashSet newSet = null;
    try {
      newSet = (FastHashSet<E>) super.clone();
    } catch (CloneNotSupportedException e) {
    }
    newSet.map = (HashMap<E, Object>) map.clone();
    return newSet;
  }
}
