package power;

import java.util.HashSet;

public class PowerSequence {

	public static void main(String[] args)
	{
		if (args[0].compareTo("-1") == 0)
		{
			performTest();
		}
		else
			System.out.println("Number of multiplications " + multiplications(Integer.parseInt(args[0])));
	}
	
	
	
	public static int multiplications (int target)
	{
		if (target == 1)
			return 0;
		if (target == 2)
			return 1;
		
		//This set contains the multiples we've already seen
		HashSet<Integer> seen = new HashSet<Integer>();
		//Contains the original number
		seen.add(1);
		
		//This set contains the last iteration of data 
		//all the numbers that can be reached by iterationNum
		HashSet<Integer> current = new HashSet<Integer>();
		//Add squared
		current.add(2);
		
		int iterationNum = 1;

		while(true)
		{
			iterationNum++;
			//This is just a HashSet, but it raises an exception if you hit the target
			HashSetTrigger<Integer> next = new HashSetTrigger<Integer>(target);

			try
			{
				for(Integer n : current)
				{
					//We can double every element with just one multiplication
					Integer nextInt = n+n;
					next.addElement(nextInt);
					
					//Now go through everything we've seen and add elements that are 
					//multiples of what we've added
					//
					//For example we've calculated x^5 and x^10.  Since 10 is a multiple
					//of 5 we can add 15 by just one multiplication
					for (Integer s : seen)
					{
						if (n%s == 0)
						{
							Integer sum = s+n;
							next.addElement(sum);
						}
					}
				}
				//Add all the current elements to seen and update current to next
				seen.addAll(current);
				current = next;
				
			}
			//We've found it!
			catch (TargetFoundException e)
			{
				return iterationNum;
			}
		}
	}
	
	
	/* Perform test cases
	 * 
	 * Through induction we can prove that if these iterations are correct, then 
	 * subsequent depths should also be correct.
	 * 
	 * Power   Number of Multiplications
			3       2 [x*x*x]
			4       2 [(x*x)^2]
			
			5       3 [(x^2)^2 * x]
			6       3 [(x^3)^2]
			8       3 [(x^4)^2]
			
			7       4 [(x^6)*x]
			9       4 [(x^8)*x]
			10      4 [(x^5)^2]
			12      4 [(x^3)^2]
			16      4 [(x^4)^2]
			
			14      5 [(x^7)^2]
			18      5 [(x^9)^2]
			20      5 [(x^10)^2]
			24      5 [(x^12)^2]
			32      5 [(x^16)^2]
			13      5 [(x^12)*x]
			11      5 [(x^10)*x]
			17      5 [(x^16)*x]
			15      5 [(x^5)*(x^10)]
			
			19      6 [(x^18)*x]
			21      6 [(x^20)*x]
			25      6 [(x^24)*x)]
			26      6 [(x^13)^2]
			27      6 ((x^18)*(x^9)]
			
			23      7 [(x^11)^2*x]
	 */
	
	private static void performTest()
	{
		System.out.println("Performing Tests");
		//These are test cases.  We can reach each power using 4 multiplications
		int test_4[] = {7, 9, 10, 12, 16};

		//These are test cases.  We can reach each power using 5 multiplications
		int test_5[] = {14, 18, 20, 32, 13, 11, 17, 15, 24};

		//These are test cases.  We can reach each power using 6 multiplications
		int test_6[] = {19, 21, 25, 26, 27};
		
		//Perform all the test using the arrays
		for (int x : test_4)
		{
			if (multiplications(x) != 4)
				System.out.println("ERROR with this: " + x);
		}
		
		for (int x : test_5)
		{
			if (multiplications(x) != 5)
				System.out.println("ERROR with this: " + x);
		}
		
		for (int x : test_6)
		{
			if (multiplications(x) != 6)
				System.out.println("ERROR with this: " + x);
		}
		//Random test with 23, requires 7 multiplications (x^11)^2*x == 5+1+1
		if(multiplications(23) != 7)
		{
			System.out.println("ERROR with this: 23");
		}
		System.out.println("Done!");

	}
	
}
