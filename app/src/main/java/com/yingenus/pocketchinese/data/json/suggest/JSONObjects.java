package com.yingenus.pocketchinese.data.json.suggest;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public  class JSONObjects {

    public static class DirInfo {
        List<FileInfo> files= new ArrayList<>();

        public List<FileInfo> getFiles() {
            return files;
        }
    }

    public static class FileInfo implements Serializable {
        String name;
        String version;
        String file_name;
        String image;
        int words_count;
        List<String> tags = new ArrayList<>();

        public String getFileName() {
            return file_name;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public int getWordsSize() {
            return words_count;
        }

        public String getImage() {
            return image;
        }

        public List<String> getTags() {
            return tags;
        }
    }

    public static class WordList{
        String name;
        String version;
        int items;
        String image;
        String description;

        List<WordsGroup> words = new ArrayList<>();

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public int getItems() {
            return items;
        }

        public List<WordsGroup> getWords() {
            return words;
        }

        public String getDescription() {
            return description;
        }

        public String getImage() {
            return image;
        }
    }

    public static class WordsGroup{
        @SerializedName("group_name")
        String name;
        @SerializedName("group_words")
        List<Word> words = new ArrayList<>();

        public String getName() {
            return name;
        }

        public List<Word> getWords() {
            return words;
        }
    }

    public static class Word{
        @SerializedName("word_chinese")
        String word;
        @SerializedName("word_pinyin")
        String pinyin;
        @SerializedName("word_translation")
        String translation;
        @SerializedName("word_examples")
        List<Example> examples = new ArrayList<>();

        public Word(){

        }
        public Word(String word, String pinyin,String translation,List<Example> examples){
            this.word = word;
            this.pinyin = pinyin;
            this.translation = translation;
            this.examples = examples;
        }

        public String getTranslation() {
            return translation;
        }

        public String getPinyin() {
            return pinyin;
        }

        public List<Example> getExamples() {
            return examples;
        }

        public String getWord() {
            return word;
        }

        public void setPinyin(String pinyin) {
            this.pinyin = pinyin;
        }
    }

    public static class Example{
        @SerializedName("example_chinese")
        String chinese;
        @SerializedName("example_pinyin")
        String pinyin;
        @SerializedName("example_translation")
        String translation;

        public String getChinese() {
            return chinese;
        }

        public String getPinyin() {
            return pinyin;
        }

        public String getTranslation() {
            return translation;
        }
    }
}
