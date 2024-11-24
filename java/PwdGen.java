import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;  

/**
 * A simple console program to generate a password by policies.
 * 
 * Arguments - [0]: Total Length [1]: Number Length [2]: Upper Length [3]: Lower Length [4]: Special Length
 * Length = 0 means disabling
 * 
 * Author: Wing Kui, Tsoi
 * Last Updated: 2024-11-24
 */
public class PwdGen {
	private static int[] policies = new int[] { 14, 1, 1, 1, 1 }; // same as arguments order
	private static int policyNums = 0;
	private static Random rand = new Random();

	public static void main(String[] args) {
		// input or default value; count number of policies
		System.out.println("Input: " + Arrays.toString(args));
		for (var i = 0; i < policies.length; i++) {
			// input
			var policy = args.length > i ? Integer.valueOf(args[i]) : policies[i];
			policies[i] = policy;
			if (i == 0 || policy < 0) continue;
			// policy on
			policyNums++; 
			// free space
			policies[0] -= policy; 
		}
		// validation
		var totalLength = policies[0];
		if (totalLength < 0) {
			System.out.println("Input invalid");
			return;
		}
		// upper bound
		for (var i = 1; i < policies.length; i++) {
			var minimumLength = policies[i];
			if (minimumLength < 0) continue;
			// free space
			var randomValue = rand.nextInt((totalLength += minimumLength) + 1); // free space + min + (length + 1)
			totalLength -= (policies[i] = --policyNums == 0 && totalLength > randomValue 
					? totalLength // ensure max space at last
					: randomValue > minimumLength 
					? randomValue // random value based on free space
					: minimumLength); // ensure min space
		}
		// generate password
		Random random = new SecureRandom();
		// use ints() method of random to get IntStream of number of the specified
		Stream<Character> pwdStream = Stream.empty();
		if (policies[1] >= 0)
			pwdStream = Stream.concat(pwdStream, random.ints(policies[1], 48, 57).mapToObj(data -> (char) data));
		if (policies[2] >= 0)
			pwdStream = Stream.concat(pwdStream, random.ints(policies[2], 'A', 'Z').mapToObj(data -> (char) data));
		if (policies[3] >= 0)
			pwdStream = Stream.concat(pwdStream, random.ints(policies[3], 'a', 'z').mapToObj(data -> (char) data));
		if (policies[4] >= 0)
			pwdStream = Stream.concat(pwdStream, random.ints(policies[4], 33, 45).mapToObj(data -> (char) data));
		List<Character> listOfChar = pwdStream.collect(Collectors.toList());
		Collections.shuffle(listOfChar);
		String pwd = listOfChar.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
		System.out.println("Password: " + pwd);
		System.out.println("Length: " + pwd.length());
	}
}
