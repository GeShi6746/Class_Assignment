
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/*
 * This program finds the longest gap between two consecutive prime numbers.
 * The program first generates N prime numbers, N >= 2, obtained from the user.
 * The N prime numbers are kept in ascending order. The program then iterates 
 * through the list of N prime numbers and find the longest gap between two
 * consecutive prime numbers in the list. It displays the size N, the two prime
 * prime numbers P1 and P2, both P1 and P2 in the list of N prime numbers and
 * the gap between them.
 */

/**
 * @author Kasi Periyasamy
 * created on Oct 05, 2018
 */
public class FindLongestGapInPrimes {
    private ArrayList<Integer> primes; // the list of prime numbers
    
    // The following constructor initializes the list of prime numbers.
    public FindLongestGapInPrimes() {
        primes = new ArrayList<Integer>();
    }
    
    /**
     * This method asserts whether or not an integer passed as input is a 
     * prime number. An integer n is prime if and only if it is divisible 
     * by 1 and by itself; no other integer in the range of 2 .. (n/2) should
     * divide n.
     * @param n the integer to be tested.
     * @return boolean result confirming whether n is a prime number.
     */
    
    private boolean isPrime (int n) {
        // first prime number starts at 2.
        if (n <= 1) return false; 
        if (n == 2) return true;
        int divisor = 2;
        while (divisor <= (n / 2)) {
            if (n % divisor == 0) 
                return false;
            divisor++;
        }
        return true;
    }
    
    /**
     * This method generates the first n prime numbers, n >= 2. If n <= 2, the
     * method does nothing.The generated prime numbers are all stored in the
     * list "primes".
     * @param n the number of prime numbers to be generated.
     */
    public void generatePrimes(int n) {
        int next = 2; // the next integer to be tested; the first prime is 2.
        int count = 0;
        if (n >= 2) {
            while (count < n) {
                if (isPrime(next)){
                    primes.add(next);
                    count++;
                }
                next++;
            }
            System.out.print (" The generated prime numbers are : ");
            Iterator<Integer> iter = primes.iterator();
            while (iter.hasNext())
                System.out.print (" " + iter.next());
            System.out.println();
        }
    }
    
    /**
     * This method finds the longest gap between two consecutive prime numbers
     * from the list "primes". It displays the answer but does not store it
     * anywhere.
     */
    
    public void findLongestGap() {
        int first = 0, second = 0, gap = 0;
        int one = 0, two = 0; // used to store temporary results
        if (primes == null || primes.isEmpty())
            System.out.println (" No prime numbers generated yet");
        else if (primes.size() < 2)
            System.out.println (" The list contains less than two prime numbers");
        else {
            Iterator<Integer> iter = primes.iterator();
            first = iter.next();
            do {
                second = iter.next();
                if (second - first >= gap) { // found a new pair
                    gap = second - first;
                    one = first;
                    two = second;
                }
                first = second; // for the next iteration
            } while (iter.hasNext());
            System.out.println (" The two consutive prime numbers that has the" +
                    " longest between them are " + one + " and " + two);
            System.out.println (" The gap is " + gap + "\n");
        }
    }
    
    /**
     * This method reads an integer N from the user which represents the
     * number of primes to be generated.
     */

    public void getNFromUser () {
        Scanner scan = new Scanner(System.in);
        int N = 0;
        char ch = ' ';
        boolean terminate = false;
        while (!terminate) {
            System.out.print (" Input an integer greater than or equal to 2 : ");
            N = scan.nextInt();
            System.out.println();
            if (N < 2)
                System.out.println (" Input integer must be greater than or equal to 2");
            else {
                generatePrimes(N);
                findLongestGap();
            }
            System.out.print( " Do you want to continue [Y or N]?: ");
            ch = scan.next().charAt(0);
            if (ch == 'Y' || ch == 'y') 
                // reset the primes array
                primes.clear();
            else terminate = true;   
        }
    }
    
    // This is the main method
    public static void main (String args[]) {
        FindLongestGapInPrimes flp = new FindLongestGapInPrimes();
        flp.getNFromUser();
    }
            
}
