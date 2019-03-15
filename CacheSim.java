
import java.util.Random;
import java.util.Scanner;

import java.util.LinkedList; 
import java.util.Queue; 

public class CacheSim{

    public static boolean debugZ = false;

    public static boolean cacheDebug = false;

    public static int cSize;

    public static int[] RAM = new int[1000];

    public static void main(String args[]){    
        Scanner sc = new Scanner(System.in);

        System.out.print("Select Distribution:" + "\n" 
            + "1 for Uniform" + "\n"
            + "2 for Zipf" + "\n"
            + "3 for Dependent" + "\n");

        int choice = sc.nextInt();

        System.out.println();

        System.out.print("Enter a cache size (integer value from 10-200): " +"\n");
        int size = sc.nextInt();

        System.out.println();

        cSize = size;


        if(choice == 1){
            unifSim();
        }else if (choice == 2){
            zipfSim();
        } else if (choice == 3){
            depSim();
        } 

        System.out.println("Select Cache Replacement:" + "\n" 
            + "1 for Random" +"\n" 
            + "2 for FIFO" + "\n" 
            + "3 for LRU" +"\n" 
            + "4 for LFU" + "\n");

        int crpChoice = sc.nextInt();
        if(crpChoice == 1){
            Rand();
        }else if(crpChoice == 2){
            FIFO();
        }else if(crpChoice == 3){
            LRU();
        }else if(crpChoice == 4){
            LFU();
        } else {
            System.out.println("Invalid replacement policy");
        }

    }


    //Fills RAM array with random numbers uniformly distributed between 1 and 1000
    public static void unifSim(){

        Random rn = new Random();

        for(int i = 0; i <1000; i++){
            RAM[i] = rn.nextInt(999)+1;
        }
    }

    //Fills RAM array with random numbers from a zipf distribution 1-1000 with alpha=1
    public static void zipfSim(){

        double denom = 0;

        for (double j=1; j<=1000; j++){
            denom += (1/j);
        }

        double[] probs = new double[1001];


        probs[0] = 0;
        for(double i = 1; i <=1000; i++){
            int index = (int) i;
            probs[index] = 1/(i*denom);
        }
        
        if(debugZ){
            System.out.println("Debugging:");
            System.out.println("Denominator: " + denom);
            double sum = 0;
            for(int k = 0; k<=1000; k++){
                sum += probs[k]; 
                System.out.println(probs[k] + "          " + sum + "           " + k);
            }
            System.out.println("Sum of probs:" + sum);
        }

        Random rand = new Random();

        for(int n = 0; n<1000; n++){
            double toCheck = rand.nextDouble();

            double checkAgainst = 0;

            for(int m = 1; m<=1000; m++){
                checkAgainst += probs[m];

                if(toCheck<=checkAgainst){
                    RAM[n] = m;

                    if(debugZ){
                        System.out.println("Added value " + m + " to RAM");
                        System.out.println("BREAKING");
                    }
                    break;
                }

                if(debugZ) System.out.println("checkAgainst: " + checkAgainst);

            }
        }

        if(debugZ){
            for(int i = 0; i<1000; i++){
                System.out.print(" " + RAM[i] + " ");
            }
        }
    }

    //Fills RAM array with values generated from provided RequestGenerator class which are not idependent
    public static void depSim(){
        RequestGenerator rg = new RequestGenerator();

        for(int i = 0; i<1000; i++){
            int toAdd = rg.generateRequest();
            RAM[i] = toAdd;
        }
    }

    //Random selection cache replacement
    public static void Rand(){
        double[] cache = new double[cSize]; //simulated cache
        Random rnd = new Random();

        double cHits = 0;
        double cMiss = 0;

        for(int i=0; i<10000000; i++){
            boolean hit = false;
            int toReq = rnd.nextInt(1000);
            double cacheCheck = RAM[toReq]; // request is drawn randomly from either Uniform or Zipf distribution
            for(int j = 0; j<cache.length; j++){//checks if data requested is in cache
                if(cache[j] == cacheCheck){
                    hit = true;
                    if(cacheDebug) System.out.println("CACHE HIT");
                    break;
                } else if (j == (cache.length - 1)){
                    if(cacheDebug) System.out.println("CACHE MISS");
                }
            }
            if(hit){
                if(i>100000) cHits++;
            } else {                        // Replacement -- if the data request turns out to be a cache miss,
                if(i>100000) cMiss++;       // a random index of the cache array is chosen and replaced by the requested
                int toRep = rnd.nextInt(cSize);  // data
                cache[toRep] = RAM[toReq];
            }
            if(cacheDebug) System.out.println("Cache Hits: " + cHits + "     Cache Misses: " + cMiss);
        }
        double hRate = cHits/(cHits + cMiss);

        System.out.println("Total Cache Hits: " + cHits + "     Total Cache Misses: " + cMiss);
        System.out.println("Hit Rate: " + hRate);
        
    }

    //First in First Out cache replacement
    public static void FIFO(){
        Queue<Integer> cache = new LinkedList<>(); //simulated cache
        Random rnd = new Random();

        double cHits = 0;
        double cMiss = 0;

        for(int i=0; i<10000000; i++){
            boolean hit = false;
            int toReq = rnd.nextInt(1000);
            double cacheCheck = RAM[toReq]; // request is drawn randomly from either Uniform or Zipf distribution
            for(double e : cache){//checks if data requested is in cache
                if(e == cacheCheck){
                    hit = true;
                    break;
                } 
            }
            if(hit){
                if(cacheDebug) System.out.println("CACHE HIT");
                if(i>100000) cHits++;
            } else { //in the case of a miss
                if(cacheDebug) System.out.println("CACHE MISS");
                if(i>100000) cMiss++;
                if(cache.size() < cSize){//if the cache is not full, adds element to cache
                    cache.add(RAM[toReq]);
                } else { //if cache is empty, replaces first element in cache
                    cache.remove();
                    cache.add(RAM[toReq]);
                }
            }
            if(cacheDebug) System.out.println("Cache Hits: " + cHits + "     Cache Misses: " + cMiss);
        } 
        double hRate = cHits/(cHits + cMiss);

        System.out.println("Total Cache Hits: " + cHits + "     Total Cache Misses: " + cMiss);
        System.out.println("Hit Rate: " + hRate);
    }

    //Least Recently Used cache replacment
    public static void LRU(){ 
        int[] cache = new int[cSize]; //simulated cache
        for(int t = 0; t<cache.length; t++){
            cache[t] = -1;
        }

        int[] metaData = new int[1001]; //simulated metadata, each space representing a piece of data from RAM
        Random rnd = new Random();

        double cHits = 0;
        double cMiss = 0;

        for(int i=0; i<10000000; i++){
            boolean hit = false;
            int toReq = rnd.nextInt(1000);
            int cacheCheck = RAM[toReq]; // request is drawn randomly from either Uniform or Zipf distribution
            for(int k=1; k<metaData.length; k++){
                metaData[k]++;
            }
            metaData[cacheCheck] = 0;
            for(int e = 0; e<cache.length; e++){//checks if data requested is in cache
                if(cache[e] == cacheCheck){
                    hit = true;
                    break;
                } 
            }
            if(hit){
                if(cacheDebug) System.out.println("CACHE HIT");
                if(i>100000) cHits++;
            } else { //in the case of a miss
                if(cacheDebug) System.out.println("CACHE MISS");
                if(i>100000) cMiss++;

                boolean cacheFull = true;
                for(int s = 0; s<cache.length; s++){//ensures cache is filled
                    if(cache[s] == -1){
                        cacheFull = false;
                        cache[s] = cacheCheck;
                        break;
                    }
                } 
                if(cacheFull==true){    
                    int LRUSlot = 0;
                    int LRUCountCheck = 0;
                    for (int l = 0; l<cache.length; l++){//checks cache for least recently used cache slot (highest count in metaData)
                        if(metaData[cache[l]] >= LRUCountCheck){
                                LRUCountCheck = metaData[cache[l]];
                                LRUSlot = l;//gets index value of least recently used cache slot
                            }
                            if(cacheDebug){
                                System.out.println("cache[l]: " + cache[l]);
                                System.out.println("metaData[cache[" + l + "]]: " + metaData[cache[l]]);
                            }
                        }
                        if(cacheDebug){ 
                            System.out.println("Least Recently Used Slot: " + LRUSlot);
                            System.out.println("REPLACING " + cache[LRUSlot] + " WITH " + cacheCheck);
                        }
                    cache[LRUSlot] = cacheCheck; //performs replacement
                }
            }
            if(cacheDebug) System.out.println("Cache Hits: " + cHits + "     Cache Misses: " + cMiss);
        }
        
        double hRate = cHits/(cHits + cMiss);

        System.out.println("Total Cache Hits: " + cHits + "     Total Cache Misses: " + cMiss);
        System.out.println("Hit Rate: " + hRate);
    }

    //Least Frequently Used cache replacement
    public static void LFU(){
        int[] cache = new int[cSize]; //simulated cache
        int[] metaData = new int[1001];
        Random rnd = new Random();
 
        double cHits = 0;
        double cMiss = 0;
 
        for(int i=0; i<10000000; i++){
            boolean hit = false;
            int toReq = rnd.nextInt(1000);
            int cacheCheck = RAM[toReq]; // request is drawn randomly from either Uniform or Zipf distribution
            metaData[cacheCheck] += 1;
 
            for(int j = 0; j<cache.length; j++){//checks if data requested is in cache
                if(cache[j] == cacheCheck){
                    hit = true;
                    if(cacheDebug) System.out.println("CACHE HIT");
                    break;
                } else if (j == (cache.length - 1)){
                    if(cacheDebug) System.out.println("CACHE MISS");
                }
            }
            if(hit){
                if(i>100000) cHits++;
            } else {                        // Replacement -- if the data request turns out to be a cache miss,
                if(i>100000) cMiss++;       // a random index of the cache array is chosen and replaced by the requested
                int lowestInC = 10000000;
                int lowestInCIndex = 0;
 
                for (int p = 0; p < cache.length; p ++){
                    if(metaData[cache[p]] < lowestInC){
                        lowestInC = metaData[cache[p]];
                        lowestInCIndex = p;
                    }
                }
                cache[lowestInCIndex] = RAM[toReq];
                 
            }
            if(cacheDebug) System.out.println("Cache Hits: " + cHits + "     Cache Misses: " + cMiss);
        }
        if(cacheDebug){
 
            for (int l = 0; l < 1001; l ++){
                System.out.println(l + ": " + metaData[l]);
            }
        }
        double hRate = cHits/(cHits + cMiss);
 
        System.out.println("Total Cache Hits: " + cHits + "     Total Cache Misses: " + cMiss);
        System.out.println("Hit Rate: " + hRate);
    }
}