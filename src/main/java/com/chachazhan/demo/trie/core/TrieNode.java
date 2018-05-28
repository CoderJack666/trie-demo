package com.chachazhan.demo.trie.core;

import lombok.Data;
import lombok.val;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jack
 * @date 2018/5/28
 * @time 15:36
 */
@Data
public class TrieNode implements Comparable<TrieNode> {

  private static final int ARRAY_LENGTH_LIMIT = 3;

  private TrieNode[] childrenArray;

  private Map<Character, TrieNode> childrenMap;

  private int childrenSize;

  private int status;

  private FastHashSet<String> words;

  @NotNull
  private final Character nodeChar;

  public void insert(char[] chars, String word) {
    doInsert(chars, word, 0, chars.length, 1);
  }

  private void doInsert(char[] chars, String word, int begin, int length, int enabled) {
    val keyChar = chars[begin];
    val trieNode = findNode(keyChar, enabled);
    if (trieNode != null) {
      if (length > 1) {
        trieNode.doInsert(chars, word, begin + 1, length - 1, enabled);
      } else if (length == 1) {
        trieNode.status = enabled;
        if (trieNode.getWords() == null) {
          trieNode.setWords(new FastHashSet<>(16));
        }
        trieNode.getWords().add(word);
      }
    }
  }

  private TrieNode findNode(char keyChar, int create) {
    TrieNode trieNode = null;
    if (childrenSize <= ARRAY_LENGTH_LIMIT) {
      val trieNodes = getArray();
      val keyNode = new TrieNode(keyChar);
      val position = Arrays.binarySearch(trieNodes, 0, childrenSize, keyNode);
      if (position >= 0) {
        trieNode = trieNodes[position];
      }

      if (trieNode == null && create == 1) {
        trieNode = keyNode;
        if (childrenSize < ARRAY_LENGTH_LIMIT) {
          trieNodes[childrenSize] = trieNode;
          childrenSize++;
          Arrays.sort(trieNodes, 0, childrenSize);
        } else {
          val trieNodeMap = getMap();
          migrate(trieNodes, trieNodeMap);
          trieNodeMap.put(keyChar, trieNode);
          childrenSize++;
          setChildrenArray(null);
        }
      }
    } else {
      val trieNodeMap = getMap();
      trieNode = trieNodeMap.get(keyChar);
      if (trieNode == null && create == 1) {
        trieNode = new TrieNode(keyChar);
        trieNodeMap.put(keyChar, trieNode);
        childrenSize++;
      }
    }
    return trieNode;
  }

  public boolean hasNext() {
    return childrenSize > 0;
  }

  public Hit match(char[] chars) {
    return match(chars, 0, chars.length, null);
  }

  public Hit match(char[] chars, int begin, int length) {
    return match(chars, begin, length, null);
  }

  public Hit match(char[] chars, int begin, int length, Hit searchHit) {
    if (searchHit == null) {
      searchHit = new Hit();
      searchHit.setBegin(begin);
    } else {
      searchHit.setNotMatch();
    }
    searchHit.setEnd(begin);

    val keyChar = chars[begin];
    TrieNode trieNode = null;
    val trieNodes = childrenArray;
    val trieNodeMap = childrenMap;
    if (trieNodes != null) {
      val keyTrieNode = new TrieNode(keyChar);
      val position = Arrays.binarySearch(trieNodes, 0, childrenSize, keyTrieNode);
      if (position >= 0) {
        trieNode = trieNodes[position];
      }
    } else if (trieNodeMap != null) {
      trieNode = trieNodeMap.get(keyChar);
    }

    if (trieNode != null) {
      if (length > 1) {
        return trieNode.match(chars, begin + 1, length - 1, searchHit);
      } else if (length == 1) {
        searchHit.setMatch();
        searchHit.setMatchedTrieNode(trieNode);
      }

      if (trieNode.hasNext()) {
        searchHit.setPrefix();
        searchHit.setMatchedTrieNode(trieNode);
      }

      return searchHit;
    }
    return searchHit;
  }

  private TrieNode[] getArray() {
    if (childrenArray == null) {
      childrenArray = new TrieNode[ARRAY_LENGTH_LIMIT];
    }
    return childrenArray;
  }

  private Map<Character, TrieNode> getMap() {
    if (childrenMap == null) {
      childrenMap = new HashMap<>(ARRAY_LENGTH_LIMIT * 2);
    }
    return childrenMap;
  }

  private void migrate(TrieNode[] trieNodes, Map<Character, TrieNode> trieNodeMap) {
    for (val trieNode : trieNodes) {
      if (trieNode != null) {
        trieNodeMap.put(trieNode.getNodeChar(), trieNode);
      }
    }
    trieNodes = null;
  }

  @Override
  public int compareTo(TrieNode otherTrieNode) {
    return this.nodeChar.compareTo(otherTrieNode.nodeChar);
  }
}
