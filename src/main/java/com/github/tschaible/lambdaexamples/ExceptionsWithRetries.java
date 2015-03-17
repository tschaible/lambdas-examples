/**
 * 
 */
package com.github.tschaible.lambdaexamples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Example class showing use of lambdas for a retry type scenario
 * 
 * In this example, we'll define arbitrary ways of creating lists of [x] numbers
 * (via lambdas). The methods may fill with a runtime exception.
 * 
 * There will be separate function (executeWithRetries) which isolates 
 * the try/catch/retry functionality
 * 
 * @author tschaible
 */
public class ExceptionsWithRetries {

	public static void main(String[] args) {
		ExceptionsWithRetries app = new ExceptionsWithRetries();

		// for the first 10, let's use a random generator that fails 10% of the
		// time
		IntStream
				.range(1, 11)
				.forEach(
						count -> {
							List<Integer> result = app.executeWithRetries(
									50,
									10,
									listSize -> {
										System.out.println(String
												.format("Generating a random list #%2$d of size [%1$d]",
														listSize, count));
										List<Integer> numbers = new ArrayList<Integer>(
												listSize);
										for (int i = 0; i < listSize; i++) {
											// fail 10% of the time
											if (Math.random() < .10) {
												throw new RuntimeException(
														"Could not generate list");
											}
											// otherwise add a random number
											numbers.add((int) (Math.random() * 100));
										}

										return numbers;
									});
							System.out.println(String.format(
									"List #%1$d is %2$s", count,
									Arrays.toString(result.toArray())));
						});

		// for the next 10, let's just use a fixed list
		IntStream
				.range(11, 21)
				.forEach(
						count -> {
							List<Integer> result = app.executeWithRetries(
									50,
									10,
									listSize -> {
										System.out.println(String
												.format("Generating a fixed list #%2$d of size [%1$d]",
														listSize, count));
										
										return Collections.nCopies(listSize, 1);
									});
							System.out.println(String.format(
									"List #%1$d is %2$s", count,
									Arrays.toString(result.toArray())));
						});
		
		// for the next 10, let's do Fibonacci 
		IntStream
				.range(21, 31)
				.forEach(
						count -> {
							List<Integer> result = app.executeWithRetries(
									50,
									10,
									listSize -> {
										System.out.println(String
												.format("Generating a Fibonacci list #%2$d of size [%1$d]",
														listSize, count));
										List<Integer> numbers = new ArrayList<Integer>(
												listSize);
										for (int i = 0; i < listSize; i++) {
											if ( i == 0 ) {
												numbers.add(0);
											} else if ( i == 1) {
												numbers.add(1);
											} else {
												numbers.add(numbers.get(i-1)+numbers.get(i-2));
											}
										}
										return numbers;
									});
									
							System.out.println(String.format(
									"List #%1$d is %2$s", count,
									Arrays.toString(result.toArray())));
						});
	}

	public List<Integer> executeWithRetries(int retryCount, int size,
			Function<Integer, List<Integer>> func) {
		for (int i = 0; i < retryCount; i++) {
			try {
				return func.apply(size);
			} catch (RuntimeException e) {
				System.err.println(String.format(
						"Caught exception, retrying [%1$d] of [%2$d]", i + 1,
						retryCount));
			}
		}
		throw new RuntimeException(String.format(
				"Could not get result after [%1$d] retries", retryCount));
	}
}
