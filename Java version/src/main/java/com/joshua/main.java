package com.joshua;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import opennlp.tools.stemmer.PorterStemmer;

public class main {
   public static void main(String[] args) throws IOException {
      Path path = getUserPath();
      List<String> files = findFiles(path, "json");

      HashMap<String, Integer> stopWords = loadStopWords();

      List<ParticipantCount> participantsCount = new ArrayList<ParticipantCount>();
      HashMap<String, Integer> countOfWords = new HashMap<String, Integer>();
      HashMap<DateTime, Integer> timeCount = new HashMap<DateTime, Integer>();
      

      for (String currFile : files) {
         countInFile(currFile, participantsCount, countOfWords, timeCount, stopWords);
      }

      printResults(participantsCount, countOfWords);
   }

   private static HashMap<String, Integer> loadStopWords() throws FileNotFoundException {
      HashMap<String, Integer> stopWords = new HashMap<String, Integer>();
      
      //TODO change to detect the file in resources
      File file = new File("C:\\Users\\smeej\\OneDrive\\Documents\\Coding\\Java\\json_experiment\\JSON-mini-data-analysis\\stopWords.txt"); // Change to match dir of files
      try (Scanner scn = new Scanner(file)) {
         while (scn.hasNextLine()) {
            stopWords.put(scn.nextLine(), 1);
         }
      }

      return stopWords;
   }

   private static Path getUserPath() {
      String str = "";
      try (Scanner sc = new Scanner(System.in).useDelimiter("\n")) {
         System.out.print("Enter a path to the files: ");
         str = sc.nextLine(); // reads string
      }

      return Paths.get(str);
   }

   private static void printResults(List<ParticipantCount> participantsCount, HashMap<String, Integer> countOfWords) {

      Set<Entry<String, Integer>> s = countOfWords.entrySet();
      List<Entry<String, Integer>> l = new ArrayList<Entry<String, Integer>>(s);
      Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
         @Override
         public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
            return (arg0.getValue().compareTo(arg1.getValue()));
         }

      });

      for (Map.Entry<String, Integer> entry : l) {
         System.out.println(entry.getKey() + ": " + entry.getValue());
      }

      Collections.sort(participantsCount, new Comparator<ParticipantCount>() {
         @Override
         public int compare(ParticipantCount lhs, ParticipantCount rhs) {
            return (lhs.getMessageCount() > rhs.getMessageCount()) ? -1
                  : (lhs.getMessageCount() < rhs.getMessageCount()) ? 1 : 0;
         }
      });

      for (ParticipantCount curr : participantsCount) {
         System.out.printf("Name: %s,\tMessages: %d,\tWords: %d,\tCharacters: %d\n", curr.getName(),
               curr.getMessageCount(), curr.getWordCount(), curr.getCharCount());
      }
   }

   private static void countInFile(String fileLocation, List<ParticipantCount> participantsCount,
         HashMap<String, Integer> countOfWords, HashMap<DateTime, Integer> timeCount, HashMap<String, Integer> stopWords) {
      try {
         Gson gson = new Gson();
         Reader reader = Files.newBufferedReader(Paths.get(fileLocation));

         Conversation conversation = gson.fromJson(reader, Conversation.class);

         Message[] messages = conversation.getMessages();

         for (Message curr : messages) {
            if (curr.getContent() != null) {
               countMessage(curr, participantsCount, countOfWords, timeCount, stopWords);
            }
         }
         reader.close();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private static void countMessage(Message message, List<ParticipantCount> participantsCount,
         HashMap<String, Integer> countOfWords, HashMap<DateTime, Integer> timeCount, HashMap<String, Integer> stopWords) {
      String sender = message.getSenderName();

      boolean found = false;

      int i;

      for (i = 0; i < participantsCount.size() && found == false; i++) {
         if (participantsCount.get(i).getName().equals(sender)) {
            addMessageStats(message.getContent(), participantsCount.get(i), countOfWords, stopWords);
            found = true;
         }
      }

      if (found == false) {
         addNewParticipant(participantsCount, sender);
         addMessageStats(message.getContent(), participantsCount.get(i), countOfWords, stopWords);
      }

      addMessageTime(message, timeCount);

   }

   private static void addMessageTime(Message message, HashMap<DateTime, Integer> timeCount) {
      DateTime messageTime = new DateTime(1970, 1, 1, 10, 0, 0);
      System.out.println(message.getTimestamp());
      // messageTime.plusMillis(message.getTimestamp());

      // DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss M z");

      // System.out.println(messageTime.toString(fmt));

   }

   private static void addNewParticipant(List<ParticipantCount> participantsCount, String sender) {
      participantsCount.add(new ParticipantCount(sender, 0, 0, 0));
   }

   private static void addMessageStats(String content, ParticipantCount participantCount,
         HashMap<String, Integer> countOfWords, HashMap<String, Integer> stopWords) {
      participantCount.addMessageCount(1);
      participantCount.addWordCount(countWords(content, countOfWords, stopWords));
      participantCount.addCharCount(content.length());
   }

   private static int countWords(String content, HashMap<String, Integer> countOfWords,
         HashMap<String, Integer> stopWords) {
      String[] words = content.split(" ");

      int count = 0;

      for (String curr : words) {
         curr = curr.toLowerCase();
         PorterStemmer stemmer = new PorterStemmer();
         curr = stemmer.stem(curr);

         if (stopWords.containsKey(curr) == false) {
            int newValue = 1;

            if (countOfWords.containsKey(curr)) {
               newValue += countOfWords.get(curr);
            }

            countOfWords.put(curr, newValue);
         }

         count++;
      }
      return count;
   }

   public static List<String> findFiles(Path path, String fileExtension) throws IOException {

      if (!Files.isDirectory(path)) {
         throw new IllegalArgumentException("Path must be a directory!");
      }

      List<String> result;

      try (Stream<Path> walk = Files.walk(path)) {
         result = walk.filter(p -> !Files.isDirectory(p))
               // this is a path, not string,
               // this only test if path end with a certain path
               // .filter(p -> p.endsWith(fileExtension))
               // convert path to string first
               .map(p -> p.toString().toLowerCase()).filter(f -> f.endsWith(fileExtension))
               .collect(Collectors.toList());
      }

      return result;
   }

}