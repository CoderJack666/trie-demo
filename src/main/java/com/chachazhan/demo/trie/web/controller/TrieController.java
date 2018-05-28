package com.chachazhan.demo.trie.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.chachazhan.demo.trie.service.TrieService;

/**
 * @author jack
 * @date 2018/5/28
 * @time 17:12
 */
@RestController
@RequestMapping("/trie")
public class TrieController {

  private final TrieService trieService;

  @Autowired
  public TrieController(TrieService trieService) {
    this.trieService = trieService;
  }

  @GetMapping("/maxlength")
  public Object getMaxLengthSubWord(@RequestParam("word") String word) {
    return trieService.findMaxLengthSubWord(word);
  }

}
