package com.chachazhan.demo.trie.core;

import lombok.Data;

/**
 * @author jack
 * @date 2018/5/28
 * @time 15:35
 */
@Data
public class Hit {

  // Hit不匹配
  private static final int NOT_MATCH = 0x00000000;
  // Hit完全匹配
  private static final int MATCH = 0x00000001;
  // Hit前缀匹配
  private static final int PREFIX = 0x00000010;

  private int hitState = NOT_MATCH;

  private TrieNode matchedTrieNode;

  private int begin;

  private int end;

  public boolean isMatch() {
    return (this.hitState & MATCH) > 0;
  }

  public void setMatch() {
    this.hitState = this.hitState | MATCH;
  }

  public boolean isPrefix() {
    return (this.hitState & PREFIX) > 0;
  }

  public void setPrefix() {
    this.hitState = this.hitState | PREFIX;
  }

  public boolean isNotMatch() {
    return this.hitState == NOT_MATCH;
  }

  public void setNotMatch() {
    this.hitState = NOT_MATCH;
  }

}
