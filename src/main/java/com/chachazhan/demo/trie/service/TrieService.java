package com.chachazhan.demo.trie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import com.chachazhan.demo.trie.core.FastHashSet;
import com.chachazhan.demo.trie.core.Hit;
import com.chachazhan.demo.trie.core.TrieNode;
import com.chachazhan.demo.trie.util.KeywordUtils;
import lombok.Data;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author jack
 * @date 2018/5/28
 * @time 16:41
 */
@Slf4j
@Service
public class TrieService {

  private final String GET_KEYWORD_SQL = "SELECT id,keyword FROM mbxc_taoci.crawled_word WHERE id > ? ORDER BY id ASC LIMIT ?";

  private final int SIZE = 2000;

  private final JdbcTemplate jdbcTemplate;

  private final TrieNode trieNode;

  @Autowired
  public TrieService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.trieNode = new TrieNode((char) 0);
  }

  @PostConstruct
  public void init() {
    var total = 0;
    var id = 0L;
    var size = 0;
    var keywordList = getKeywordList(id);
    while (keywordList.size() > 0) {
      size = keywordList.size();
      total += size;
      keywordList.stream()
        .map(Keyword::getKeyword)
        .filter(StringUtils::isNotBlank)
        .forEach(word -> {
          val chars = KeywordUtils.sort(word).toCharArray();
          trieNode.insert(chars, word);
          log.info("word: {}", word);
        });
      id = keywordList.get(size - 1).getId();
      log.info("load {} word, next id: {}", total, id);
      keywordList = getKeywordList(id);
    }
  }

  public String findMaxLengthSubWord(String word) {
    if (StringUtils.isNotBlank(word)) {
      val sorted = KeywordUtils.sort(word);
      val nodeSet = searchBySorted(sorted);
      return nodeSet.stream()
        .flatMap(node -> node.getWords() != null ? node.getWords().stream() : Stream.of(""))
        .filter(StringUtils::isNotBlank)
        .filter(w -> !Objects.equals(w, word))
        .max(Comparator.comparingInt(String::length))
        .orElse(word);
    }
    return word;
  }

  private Set<TrieNode> searchBySorted(String sorted) {
    val result = new FastHashSet<TrieNode>(sorted.length());
    val length = sorted.length();
    for (var i = 0; i < length; i++) {
      find(sorted.substring(i, i + 1), sorted.substring(i + 1), result);
    }
    return result;
  }

  private void find(String word, String next, Set<TrieNode> result) {
    Hit hit = trieNode.match(word.toCharArray());
    if (hit.isMatch()) {
      result.add(hit.getMatchedTrieNode());
    }
    if (StringUtils.isNotBlank(next)) {
      val length = next.length();
      for (var i = 0; i < length; i++) {
        find(word + next.substring(i, i + 1), next.substring(i + 1), result);
      }
    }
  }

  private List<Keyword> getKeywordList(long id) {
    return jdbcTemplate.query(GET_KEYWORD_SQL, new BeanPropertyRowMapper<>(Keyword.class), id, SIZE);
  }

  @Data
  public static class Keyword {
    private Long id;

    private String keyword;
  }

}
