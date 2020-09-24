package com.company;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {
        int N=150, k=5;
        int minV=65, maxV=90;

        int kplus=k+1;
        char[][] list = new char[N][k+1];

        list = GenerateTestList(N, kplus, minV, maxV);
        printArray(list, N, kplus);

        char[][]InsertionSortedList, QuickSortedList, RadixSortedList;

        InsertionSortedList = InsertionSort(list, N, kplus);
        printArray(InsertionSortedList, N, kplus);

        QuickSortedList = QuickSort(ConvertToStringArray(N, kplus, list), N, kplus, 0, N-1);
        printArray(QuickSortedList, N, kplus);


        RadixSortedList = radixSort(list, N, k, minV, maxV);
        printArray(RadixSortedList, N, kplus);

        verificationTest(10, 3);

        timeTesting();
    }

    //================================
    //Sorting and Assignment Functions
    //================================

    //Radix Sorting algorithm
    /**Reference Code https://www.quora.com/How-can-I-sort-strings-using-Radix-Sort**/
    /**This link helped me understand how radix works and how I can implement it with this problem**/
    public static char[][] radixSort(char[][] list, int N, int k, int minV, int maxV) {
        //String array is so we can compare the strings using String arrays instead of character 2D arrays
        String[] stringArray = new String[N];
        stringArray = ConvertToStringArray(N, k, list);

        //count array contains how many strings contain the ascii code at the index
        //count has the amount of spaces that are in the range of characters you can have
        int[] count = new int[(maxV - minV) + 2];
        int cIndex;
        //cIndex is to count the character value i.e how far it is from minV

        //Temp string array will hold the values temporarily
        String[] temp = new String[N];

        //Do the sort algorithm for as many characters as there are,
        for(int index=k-1; index>=0; index--) {
            //Reset the count array
            Arrays.fill(count, 0);

            //For each character in the string array add up one spot in the count array
            //EXAMPLE: if A is the minV then if you find an A increment count[0] by 1
            for (String s : stringArray) {
                //Increment through stringArray list and count up characters in the count array
                cIndex = (s.length() - 1 < index) ? 0 : ((s.charAt(index) - minV) + 1);
                count[cIndex]++;
            }

            //Sum up count
            //It will hold the last index for the char at the string index
            //This will allow it to sort by "alphabetical" order based on the cIndex value
            for (int i = 1; i < count.length; i++) {
                count[i] += count[i - 1];
            }

            for (int i = N - 1; i >= 0; i--) {
                //cIndex is the character in the count array
                //Again example if A is the character at stringArray[i].charAt(index) and A is the
                //value for minV then that means cIndex would be 0
                cIndex = (stringArray[i].charAt(index) - minV) + 1;

                temp[count[cIndex] - 1] = stringArray[i];
                count[cIndex]--;
            }

            //Copy the temp array into stringArray
            //Then put it back into list and return it to main
            System.arraycopy(temp, 0, stringArray, 0, temp.length);
            list = ConvertToCharArray(N,k,stringArray);
        }

        return list;
    }

    //Quick Sorting algorithm
    /** Reference code at https://www.baeldung.com/java-quicksort **/
    public static char[][] QuickSort(String[] list, int N, int k, int low, int high) {

        if(low < high)
        {
            //index is the return value from the partition function this
            int index = partition(list, N, k, low, high);

            //Recursively call Quick sort with various partitions values sort different parts of the list incrementally
            QuickSort(list, N, k, low, index-1);
            QuickSort(list, N, k,index+1, high);
        }

        //Return the array using a convert function so it is usable by main
        return ConvertToCharArray(N, k, list);
    }

    public static int partition(String[] list, int N, int k, int low, int high) {
        //pivot is the last element passed in the list, used for comparison
        //i is the index value
        String pivot = list[high], temp;
        int i = (low-1);

        for(int x=low; x < high; x++)
        {
            //If statement checks list[x] <= pivot
            if(stringCompare(list[x], pivot) == 2 || stringCompare(list[x], pivot) == 0){
                i++;

                //Swap values in list[x] and list[i]
                temp = list[i];
                list[i] = list[x];
                list[x] = temp;
            }
        }

        //The i value will be changed from (low-1) if it went through the if statement
        //If the i value is equal to the high index value then that means the high value is the highest but if not
        //The values will be swapped
        temp = list[i+1];
        list[i+1] = list[high];
        list[high] = temp;

        return i+1;
    }

    //Insertion Sorting algorithm
    public static char[][] InsertionSort(char[][] list, int N, int k) {
        char[] temp;
        int loc, j;

        for(int i=0; i<N; i++)
        {
            //Store the current row/string into temp, and location, and array start value (j)
            temp = list[i];
            loc = i;

            //While loop to check if the array element to the left is less
                outerWhile:
                while (loc > 0 && temp[0] <= list[loc - 1][0]) {
                    //If the first letter is the same
                    if(temp[0] == list[loc-1][0]) {
                        //x will keep track of how far into the string we traverse
                        int x = 0;
                        while(x < k) {
                            if(temp[x] == list[loc-1][x]) // Both characters are equal
                                x++;
                            else if(temp[x]<list[loc-1][x]) { // Temp[x] is less than list[loc-1][x] example ( temp = "Bum" and list[loc-1] = "Bat" )
                                //This means you need to move the element one space forward
                                list[loc] = list[loc-1];
                                loc--;
                                break; //No need to run to the end of the loop you know temp is smaller than the list item
                            }
                            else { // Temp[x] is greater than list[loc-1][x] example (temp = "Bam" and list[loc-1] = "Bum")
                                //This means you must break out of the outerWhile loop because the value in temp is greater than the value in list
                                break outerWhile;
                            }
                        }

                    }
                    else {
                        //Move the element one space forward
                        list[loc] = list[loc - 1];
                        loc--;
                    }
                }

            //Move current element to stored position (temp)
            list[loc] = temp;

        }

        return list;
    }

    //Function to generate a 2d list of random characters with specified length of string (k), how many strings (N), and minimum and maximum values
    public static char[][] GenerateTestList (int N, int k, int minV, int maxV) {
        char[][] chars = new char[N][k+1];
        int j=0;

        //Generate Random number to get out of string of characters, cannot be above 255 or below 1
        int randomNum;

        for(int i=0; i<N; i++) {
            for(j=0; j<k-1; j++) {
                randomNum = ThreadLocalRandom.current().nextInt(minV, maxV + 1);
                chars[i][j] = (char) randomNum;
            }
            //This will add the Null Character to the end of the string
            chars[i][j+1]= (char) 0;
        }

        return chars;
    }

    //=============================================
    //Time Performance Testing
    //=============================================

    public static void timeTesting() {
        int N=1, k=6, minV=1, maxV=255;
        long beforeTime, afterTime, timeElapsed, convertedTime;
        int maxN=131072, maxK = 48;
        String time = "Time", doubl = "Doubling Ratio", prefix;
        double doubleRatio=0;
        long[] lastTime = new long[4];

        //Copy this bit below for next sorting algorithms

        System.out.printf("\nInsertion Sort\n=============\n%17s%27s%27s%27s\n", "k=6", "k=12", "k=24", "k=48");
        System.out.printf("%-10s%7s%20s%8s%20s%8s%20s%8s%20s\n", "N", time, doubl ,time, doubl ,time, doubl ,time, doubl);

        int x=0;
        for(int i=0; i<4; i++)
        {
            lastTime[i] = -1;
        }

        for (N=1; N <= maxN; N *= 2) {
            System.out.printf("%-10d", N);
            for (k=6; k <= maxK; k *= 2) {
                char[][] list;
                list = GenerateTestList(N, k, minV, maxV);

                beforeTime = getCpuTime();
                char[][]insertedSortList = InsertionSort(list, N, k);
                afterTime = getCpuTime();
                timeElapsed = afterTime - beforeTime;

                prefix = getSecondType(timeElapsed);
                convertedTime = convertNanoSeconds(timeElapsed);

                System.out.printf("%5d%-2s", convertedTime, prefix);

                doubleRatio = (double)timeElapsed/lastTime[x];
                lastTime[x] = timeElapsed;

                if(doubleRatio>0) {
                    System.out.printf("%20.3f|", doubleRatio);
                }
                else {
                    System.out.printf("%20s|", "NA");
                }
                x++;
            }
            x=0;
            System.out.print("\n");
        }

        //Quick sort

        System.out.printf("\nQuick Sort\n=============\n%17s%27s%27s%27s\n", "k=6", "k=12", "k=24", "k=48");
        System.out.printf("%-10s%7s%20s%8s%20s%8s%20s%8s%20s\n", "N", time, doubl ,time, doubl ,time, doubl ,time, doubl);

        x=0;
        for(int i=0; i<4; i++)
        {
            lastTime[i] = -1;
        }

        for (N=1; N <= maxN; N *= 2) {
            System.out.printf("%-10d", N);
            for (k=6; k <= maxK; k *= 2) {
                char[][] list;
                list = GenerateTestList(N, k, minV, maxV);

                beforeTime = getCpuTime();
                char[][]quickSortedList = QuickSort(ConvertToStringArray(N,k,list), N, k, maxV, minV);
                afterTime = getCpuTime();
                timeElapsed = afterTime - beforeTime;

                prefix = getSecondType(timeElapsed);
                convertedTime = convertNanoSeconds(timeElapsed);

                System.out.printf("%5d%-2s", convertedTime, prefix);

                doubleRatio = (double)timeElapsed/lastTime[x];
                lastTime[x] = timeElapsed;

                if(doubleRatio>0) {
                    System.out.printf("%20.3f|", doubleRatio);
                }
                else {
                    System.out.printf("%20s|", "NA");
                }
                x++;
            }
            x=0;
            System.out.print("\n");
        }

        //Radix Sort
        System.out.printf("\nRadix Sort\n=============\n%17s%27s%27s%27s\n", "k=6", "k=12", "k=24", "k=48");
        System.out.printf("%-10s%7s%20s%8s%20s%8s%20s%8s%20s\n", "N", time, doubl ,time, doubl ,time, doubl ,time, doubl);

        x=0;
        for(int i=0; i<4; i++)
        {
            lastTime[i] = -1;
        }

        for (N=2; N <= maxN; N *= 2) {
            System.out.printf("%-10d", N);
            for (k=6; k <= maxK; k *= 2) {
                char[][] list;
                list = GenerateTestList(N, k+1, minV, maxV);

                beforeTime = getCpuTime();
                char[][]radixSortList = radixSort(list, N, k, minV, maxV);
                afterTime = getCpuTime();
                timeElapsed = afterTime - beforeTime;

                prefix = getSecondType(timeElapsed);
                convertedTime = convertNanoSeconds(timeElapsed);

                System.out.printf("%5d%-2s", convertedTime, prefix);

                doubleRatio = (double)timeElapsed/lastTime[x];
                lastTime[x] = timeElapsed;

                if(doubleRatio>0) {
                    System.out.printf("%20.3f|", doubleRatio);
                }
                else {
                    System.out.printf("%20s|", "NA");
                }
                x++;
            }
            x=0;
            System.out.print("\n");
        }


    }

    //=======================================
    //Verification Test
    //=======================================
    public static void verificationTest(int N, int k) {
        int minV=65, maxV=90;
        char[][] list;
        //generate short narrow list (small k val)
        list = GenerateTestList(N, k+1, minV, maxV);

        //Create listsThat you will store the sorted lists in
        char[][] insertedSort, quickSort, radixSort;

        //print list before
        printArray(list, N, k+1);

        //sortList insertion
        System.out.print("Sorting with Insertion sort...\n");
        insertedSort = InsertionSort(list, N, k+1);
        //printSorted
        printArray(insertedSort, N, k);
        //isSorted
        System.out.print("Verifying...");
        if(isSorted(ConvertToStringArray(N, k, insertedSort))) {
            System.out.print("  Sorted!\n");
        }
        else {
            System.out.print("  ERROR:\nLIST NOT SORTED\n");
        }


        //sortList quick
        System.out.print("Sorting with Quick sort...\n");
        quickSort = QuickSort(ConvertToStringArray(N,k,list),N,k+1,0,N-1);
        //printSorted
        printArray(quickSort, N, k);
        //isSorted
        System.out.print("Verifying...");
        if(isSorted(ConvertToStringArray(N, k, quickSort))) {
            System.out.print("  Sorted!\n");
        }
        else {
            System.out.print("  ERROR:\nLIST NOT SORTED\n");
        }

        //sortList radix
        System.out.print("Sorting with Radix sort...\n");
        radixSort = radixSort(list, N, k, minV, maxV);
        //printSorted
        printArray(radixSort, N, k);
        //isSorted
        System.out.print("Verifying...");
        if(isSorted(ConvertToStringArray(N, k, radixSort))) {
            System.out.print("  Sorted!\n");
        }
        else {
            System.out.print("  ERROR:\nLIST NOT SORTED\n");
        }
    }

    private static boolean isSorted(String[] strList) {
        int N = strList.length;
        for(int i=0; i<N-1; i++) {
            if(stringCompare(strList[i], strList[i+1]) == 1) {
                return false;
            }
        }
        return true;
    }

    //=============================================
    //Utility Functions
    //=============================================

    public static String[] ConvertToStringArray(int N, int k, char[][] list){
        String[] updatedList = new String[N];

        for(int x=0; x<N; x++)
            updatedList[x] = String.valueOf(list[x]);

        return updatedList;
    }

    public static char[][] ConvertToCharArray(int N, int k, String[] list) {
        char[][] updatedList = new char[N][];

        for(int x=0; x<N; x++)
                updatedList[x] = list[x].toCharArray();

        return updatedList;
    }

    public static int stringCompare(String s1, String s2) {
        int l=s1.length();
        String str1 = String.valueOf(s1);
        String str2 = String.valueOf(s2);

        for (int i = 0; i < l; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if(str1_ch > str2_ch)
                return 1;   //First string is greater then the second

            else if(str1_ch < str2_ch)
                return 2;   //Second String is greater than the first

        }

        //Strings are identical
        return 0;
    }

    public static int stringCompare(char[] s1, char[] s2) {
        int l=s1.length;
        String str1 = String.valueOf(s1);
        String str2 = String.valueOf(s2);

        for (int i = 0; i < l; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if(str1_ch > str2_ch)
                return 1;   //First string is greater then the second

            else if(str1_ch < str2_ch)
                return 2;   //Second String is greater than the first

        }

        //Strings are identical
        return 0;
    }

    //Function to print the 2d Array of characters you pass through it
    public static void printArray(char[][] chars, int i, int j) {
        for(int x=0; x<i; x++)
        {
            for(int y=0; y<j; y++)
            {
                System.out.printf("%c", chars[x][y]);
            }
            System.out.print(" ");
        }
        System.out.print("\n");

    }

    //========================================
    //Time Functions
    //========================================

    //Function I created to help with formatting
    public static String getSecondType (long time) {
        if((time/1000000000) > 60)
            return "m ";
        if(time >= 1000000000)
            return "s ";
        else if(time >= 1000000)
            return "µs";
        else if(time >= 1000)
            return "ms";
        else
            return "ns";
    }

    //Function I created to help with formatting
    public static long convertNanoSeconds (long time) {
        long convertedTime=0;

        if(time >= 1000000000) {
            convertedTime = time / 1000000000;
            if(convertedTime>=60)   //Check if its a minute or more
                convertedTime = convertedTime/60;
        }
        else if(time >= 1000000)
            convertedTime = time / 1000000;
        else if(time >= 1000)
            convertedTime = time / 1000;
        else
            convertedTime = time;

        return convertedTime;
    }

    /* Get CPU time in nanoseconds since the program(thread) started. */
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( ) {

        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadCpuTime( ) : 0L;

    }

}
