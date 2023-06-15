package org.example;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * title        : 자동완성 Redis 데이터 셋 생성기
 * author       : sim
 * date         : 2023-06-08
 * description  :
 */
public class AutoCompleteGenerator {

    private final String filePath;

    private final String key;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String WORD_SUFFIX = "*";

    private static final String SEARCH_COUNT_SUFFIX = "-search-count";

    public AutoCompleteGenerator(RedisTemplate redisTemplate, String filePath, String key){
        this.redisTemplate = redisTemplate;
        this.filePath = filePath;
        this.key = key;
    }

    public void start() {
        reset();
        List<String> wordList = getWordListByTextFile();

        for(String word : wordList){
            int wordLen = word.length();
            for(int i = 1; i <=wordLen; i++) {
                String cutWord = word.substring(0,i);
                putWordToRedis(cutWord);
            }
            putWordToRedis(word+WORD_SUFFIX);
            putWordSearchCountToRedis(word);
        }
        getWordToRedis();
    }

    public List<String> getWordListByTextFile(){
        BufferedReader reader = null;
        ArrayList<String> wordList = new ArrayList<>();
        try{
            reader = new BufferedReader(new FileReader(filePath));

            String str;
            while ((str = reader.readLine()) != null) {
                wordList.add(str);
            }
            return wordList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(reader != null){
                try{
                    reader.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void putWordToRedis(String word) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, word, 0);
    }

    private void getWordToRedis(){
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        System.out.println(zSetOperations.range(key, 0, -1));
    }

    private void reset(){
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.removeRange(key,0,-1);
        zSetOperations.removeRange(key+SEARCH_COUNT_SUFFIX,0,-1);
    }
    private void putWordSearchCountToRedis(String word){
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key+SEARCH_COUNT_SUFFIX, word, 0);
    }
}
