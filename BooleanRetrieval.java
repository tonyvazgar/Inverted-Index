import java.io.File;
import java.util.*;

/*
 * Inverted Index for information retrieval
 *
 * Created by Tony Vazgar on 8/22/18.
 * Copyright © 2018 Tony Vazgar. All rights reserved.
 * Contact: luis.vazquezga@udlap.mx
 */

public class BooleanRetrieval{


    /*
     * Metodo para indexar los archivos.
     */
     public static Map<String, LinkedList<Integer>> index(String[] files){

          Scanner scanner;
          File file;
          Set<String> diccionario = new HashSet<String>();                                //Here goes every word but its repited
          List diccionariOrdenado;                                                   //To clone the dictionary but with no repited words
          ArrayList<String[]> palabrasArchivos = new ArrayList<String[]>();                 //Here goes every word of each document to storage them and have not to read the files many times
          Map<String, LinkedList<Integer>> invertedIndex = new HashMap<String, LinkedList<Integer>>();         //The "dictionary" for the inverted index with its respective posting list

          ///////////////////////

          System.out.println("Leyendo el archivo........");
          //For every document in the array of documents we storage thw words to make it faster and not have to read every moment
          for(int i = 0; i < files.length; i++){
               try{
                    file = new File("./" + files[i] + ".txt");
                    scanner = new Scanner(file);
                    ArrayList<String> lecturas = new ArrayList<String>();
                    while (scanner.hasNext()){
                         String word = scanner.next().toLowerCase() ;
                         diccionario.add(word);   //Se agregan al diccionario global
                         lecturas.add(word);      //Se a palabras de cada archivo
                    }
                    int numberOfWords = lecturas.size();    //Para saber el numero de palabras para el texto en el que vamos
                    String[] palabras = new String[numberOfWords];    //Se hace arreglo para cada uno exacto
                    for (int j = 0; j < numberOfWords; j++) {
                         palabras[j] = lecturas.get(j);     //Se agregan
                    }
                    Arrays.sort(palabras);        //Se guardan en orden alfabetico
                    palabrasArchivos.add(palabras);         //se agrega el conjuto de palabras al repositorio global
               }catch (Exception e){
                    System.err.println("No existe ese documento :(");
               }
          }
          System.out.println("Lectura finalizada.");

          diccionariOrdenado = new ArrayList(diccionario);            //To delete duplicates and only have one word
          Collections.sort(diccionariOrdenado);

          ArrayList<Map<String,Integer>> mapDeMaps = new ArrayList<>();
          /*
           *For each word in a document and for each word in the dictionary
           * we create the linkedList to represent the posting lists
           */
          //
          for(int archivo = 0; archivo < palabrasArchivos.size(); archivo++){
               LinkedList<Integer> post = new LinkedList<Integer>();
               String[] conjuntoPalabras = palabrasArchivos.get(archivo);
               String palabra = "";
               Map<String, Integer> dictionary = new HashMap<String, Integer>();
               for(int p = 0; p < conjuntoPalabras.length;p++){
                    palabra = conjuntoPalabras[p];
                    if(diccionario.contains(palabra)){
                         dictionary.put(palabra, archivo+1);
                    }
               }
               mapDeMaps.add(dictionary);
          }

          for(Object w: diccionariOrdenado){      //Cada palabra en el diccionariOrdenado
               String word = (String) w;
               LinkedList<Integer> post = new LinkedList<Integer>();
               for(int i = 0; i < mapDeMaps.size(); i++){   //Cada diccionario (indica palabra = indice)
                    Map<String, Integer> cadaDictionary = mapDeMaps.get(i);

                    String[] palabras = cadaDictionary.keySet().toArray(new String[cadaDictionary.size()]);
                    Integer[] numDocs = cadaDictionary.values().toArray(new Integer[cadaDictionary.size()]);

                    for(int j = 0; j < palabras.length; j++){
                         if(word.equals(palabras[j])){
                              post.add(numDocs[i]);
                         }
                    }
               }
               invertedIndex.put(word, post);
          }
          return invertedIndex;
     }





     /*
      * Metodos para buscar, intersectar y booleanos
      */
     public static LinkedList<Integer> buscarPalabra(String palabra, Map<String, LinkedList<Integer>> invertedIndex) {
          LinkedList<Integer> posts = new LinkedList<>();
          Iterator word = invertedIndex.keySet().iterator();
          while (word.hasNext()){
               String p = (String)word.next();
               if(p.equals(palabra)){
                    posts = invertedIndex.get(p);
               }
          }
          return posts;
     }
     public static LinkedList<Integer> intersect(LinkedList<Integer> p1, LinkedList<Integer> p2){

          /*
           *   IMPLEMENTATION OF THE NEXT ALGORITHM:
           *
           *     INTERSECT(p1, p2)
           *        answer←⟨⟩
           *        while p1 ̸= NIL and p2 ̸= NIL
           *        do if docID(p1) = docID(p2)
           *             then ADD(answer, docID(p1))
           *                  p1 ← next(p1)
           *                  p2 ← next(p2)
           *             else if docID(p1) < docID(p2)
           *                  then p1 ← next(p1)
           *                  else p2 ← next(p2)
           *        return answer
           */

          LinkedList<Integer> answer = new LinkedList<>();
          LinkedList<Integer> pos1 = (LinkedList<Integer>) p1.clone();
          LinkedList<Integer> pos2 = (LinkedList<Integer>) p2.clone();

          while (!pos1.isEmpty() && !pos2.isEmpty()){
               if(pos1.getFirst() == pos2.getFirst()){
                    answer.add(pos1.getFirst());
                    pos1.removeFirst();
                    pos2.removeFirst();
               }else{
                    if (pos1.getFirst() < pos2.getFirst()){
                         pos1.removeFirst();
                    }else{
                         pos2.removeFirst();
                    }
               }
          }
          print("Las palabras buscadas están en el archivo: " + answer.toString());
          return answer;
     }
     public static List<Integer> not(LinkedList<Integer> p1, LinkedList<Integer> p2){
          LinkedList<Integer> answer = (LinkedList<Integer>) p1.clone();
          LinkedList<Integer> remove = (LinkedList<Integer>) p2.clone();

          for (Integer i: remove) {
               if(answer.contains(i)){
                    answer.remove(i);
               }
          }
          return answer;
     }
     public static LinkedList<Integer> or(LinkedList<Integer> p1, LinkedList<Integer> p2){
         LinkedList<Integer> answer = (LinkedList<Integer>) p1.clone();
         LinkedList<Integer> remove = (LinkedList<Integer>) p2.clone();

         for (Integer element: remove) {
             if(!answer.contains(element))
                answer.add(element);
         }
         Collections.sort(answer);
         return answer;
     }





     /*
      * Metodo para hacer la interfaz de usuario en consola
      */
     public static void menu(Map<String, LinkedList<Integer>> invertedIndex){
          boolean i = true;
          /*
           * word1 AND word2 AND NOT word3
           * word1 AND word2 OR word3
           * word1 OR word2 AND NOT word3
           */
          try {
               while (i) {
                    Scanner scanner = new Scanner(System.in);
                    print("________________________________________________");
                    print("Type the number of one of the options below:\n");
                    print("0) Print the inverted index.");
                    print("1) word1 AND word2 AND NOT word3");
                    print("2) word1 AND word2 OR word3");
                    print("3) word1 OR word2 AND NOT word3");
                    print("4) word1 AND word2");
                    print("5) word1 OR word2");

                    int option = scanner.nextInt();
                    switch (option) {
                         case 0:
                              imprimirIndex(invertedIndex);
                              print("");
                              break;
                         case 1:
                              print("word1 AND word2 AND NOT word3");
                              print("   word1: ");
                              String word1 = new Scanner(System.in).nextLine();
                              print("   word2: ");
                              String word2 = new Scanner(System.in).nextLine();
                              print("   word3: ");
                              String word3 = new Scanner(System.in).nextLine();
                              LinkedList<Integer> r1 = intersect(buscarPalabra(word1, invertedIndex), buscarPalabra(word2, invertedIndex));
                              print(word1 + " --> " + buscarPalabra(word1,invertedIndex).toString());
                              print(word2 + " --> " + buscarPalabra(word2,invertedIndex).toString());
                              print(word3 + " --> " + buscarPalabra(word3,invertedIndex).toString());
                              print("\nRESULT FOR THE QUERY:\n" + word1 + " AND " + word2 + " AND NOT " + word3 + " --> " + not(r1, buscarPalabra(word3, invertedIndex)).toString());
                              break;
                         case 2:
                              print("word1 AND word2 OR word3");
                              print("   word1: ");
                              String word21 = new Scanner(System.in).nextLine();
                              print("   word2: ");
                              String word22 = new Scanner(System.in).nextLine();
                              print("   word3: ");
                              String word23 = new Scanner(System.in).nextLine();
                              print(word21 + " --> " + buscarPalabra(word21,invertedIndex).toString());
                              print(word22 + " --> " + buscarPalabra(word22,invertedIndex).toString());
                              print(word23 + " --> " + buscarPalabra(word23,invertedIndex).toString());
                              LinkedList<Integer> and = intersect(buscarPalabra(word21, invertedIndex), buscarPalabra(word22, invertedIndex));
                              print("\nRESULT FOR THE QUERY:\n" + word21 + " AND " + word22 + " OR " + word23 + " --> " + or(and, buscarPalabra(word23, invertedIndex)).toString());
                              break;
                         case 3:
                              print("word1 OR word2 AND NOT word3");
                              print("   word1: ");
                              String word31 = new Scanner(System.in).nextLine();
                              print("   word2: ");
                              String word32 = new Scanner(System.in).nextLine();
                              print("   word3: ");
                              String word33 = new Scanner(System.in).nextLine();
                              LinkedList<Integer> or = or(buscarPalabra(word31,invertedIndex),buscarPalabra(word32,invertedIndex));
                              print(word31 + " --> " + buscarPalabra(word31,invertedIndex).toString());
                              print(word32 + " --> " + buscarPalabra(word32,invertedIndex).toString());
                              print(word33 + " --> " + buscarPalabra(word33,invertedIndex).toString());
                              print("\nRESULT FOR THE QUERY:\n" + word31 + " OR " + word32 + " AND NOT " + word33 + " --> " + not(or, buscarPalabra(word33, invertedIndex)).toString());
                              break;
                         case 4:
                              print("word1 AND word2");
                              print("   word1: ");
                              String word41 = new Scanner(System.in).nextLine();
                              print("   word2: ");
                              String word42 = new Scanner(System.in).nextLine();
                              print(word41 + " --> " + buscarPalabra(word41,invertedIndex).toString());
                              print(word42 + " --> " + buscarPalabra(word42,invertedIndex).toString());
                              print("\nRESULT FOR THE QUERY:\n" + word41 + " AND " + word42 + " --> " + intersect(buscarPalabra(word41, invertedIndex), buscarPalabra(word42, invertedIndex)).toString());
                              break;
                         case 5:
                              print("word1 OR word2");
                              print("   word1: ");
                              String word51 = new Scanner(System.in).nextLine();
                              print("   word2: ");
                              String word52 = new Scanner(System.in).nextLine();
                              print(word51 + " --> " + buscarPalabra(word51,invertedIndex).toString());
                              print(word52 + " --> " + buscarPalabra(word52,invertedIndex).toString());
                              print("\nRESULT FOR THE QUERY:\n" + word51 + " OR " + word52 + " --> " + or(buscarPalabra(word51, invertedIndex), buscarPalabra(word52, invertedIndex)).toString());
                              break;
                         default:
                              print("Only numbers bewteen 1 and 4");
                    }
               }
          }catch (InputMismatchException e){
               System.err.println("Only numbers with the option acepted");
          }

     }





     /*
      * Main y metodos para impresion
      */
     public static void main(String[] args) {

          Map<String, LinkedList<Integer>> invertedIndex;

          ////////////

          /*
           *To chance the documents for the desired ones only chance the names of the files[] array
           *
           */
          String files[] = {"1", "2", "3"};
          //String files[] = {"texto1", "texto2", "texto3"};
          invertedIndex = index(files);
          menu(invertedIndex);
     }
     private static void imprimirIndex(Map<String, LinkedList<Integer>> invertedIndex){
          print("********************************************");
          print("The inverted index for the documents are:\n");
          print("DICTIONARY             POSTINGS" );
          for (String word: invertedIndex.keySet()) {
               print(String.format("%-15s", word) + " -->    " + invertedIndex.get(word).toString());
          }
          print("********************************************");
     }
     private static void print(String string){
          System.out.println(string);
     }
}
