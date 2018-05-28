package com.chachazhan.demo.trie.util;

import org.apache.commons.lang3.StringUtils;
import com.chachazhan.demo.trie.core.FastHashSet;
import lombok.experimental.var;
import lombok.val;

import java.util.Arrays;

/**
 * @author jack
 * @date 2018/5/28
 * @time 16:50
 */
public class KeywordUtils {

  private static final String BLANK = " ";

  public static String sort(String word) {
    if (StringUtils.isNotBlank(word)) {
      val str = StringUtils.removeAll(word, BLANK);
      val chars = str.toLowerCase().toCharArray();
      val charSet = new FastHashSet<Character>(word.length());
      for (char c : chars) {
        if (isChineseChar(c) || isLetterOrDigit(c)) {
          charSet.add(c);
        }
      }
      val newChars = new char[charSet.size()];
      var i = 0;
      for (char c : charSet) {
        newChars[i] = c;
        i++;
      }
      Arrays.sort(newChars);
      return new String(newChars);
    }
    return word;
  }

  public static boolean isChineseChar(char c) {
    return c >= 0x4e00 && c <= 0x9fbb;
  }

  public static boolean isLetterOrDigit(char c) {
    return (c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122) ;
  }

}
