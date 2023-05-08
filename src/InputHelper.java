import java.util.Optional;
import java.util.Scanner;

/**
 * Assists in requesting, parsing, and validating user input. 
 */
public class InputHelper {
	
	
	/**
	 * Prompts the user for input, validates the input, and returns the parsed result if it passes validation.
	 * Executes the provided Runnable if the input is invalid.
	 *
	 * @param scanner          The Scanner object to read user input.
	 * @param prompt           The prompt to display to the user.
	 * @param handleParseError A Runnable to be executed if the input is unable to be parsed.
	 * @param parser           A Function to parse the input.
	 * @param rules            Any rules which the input will be validated against.
	 * @param <T>              The type of the parsed input.
	 * @return The parsed and validated input.
	 */
	@SafeVarargs
	public static <T> T requestValidInput(Scanner scanner, String prompt, InputErrorHandler handleParseError,
	                                      TryParse<T> parser, Rule<T>... rules) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim();
			Optional<T> resultOpt = parser.parse(input);
			Optional<InputErrorHandler> handleError = Optional.of(handleParseError);
			
			if (resultOpt.isPresent() && (handleError = getRuleErr(resultOpt.get(), rules)).isEmpty())
				return resultOpt.get();
			handleError.get().handle(input);
		}
	}
	
	/**
	 * Returns and optional InputErrorHandler if any of the given rules are not satisfied by the input.
	 *
	 * @param input    The input to be validated against the given rules.
	 * @param rules     The rules by which the input will be validated against.
	 * @param <T>       The type of the input.
	 * @return An optional InputErrorHandler if any rules are not satisfied, or an empty optional if all rules are
	 *          satisfied.
	 */
	@SafeVarargs
	private static <T> Optional<InputErrorHandler> getRuleErr(T input, Rule<T>... rules) {
		for (Rule<T> rule : rules)
			if (!rule.getPredicate().test(input))
				return Optional.of(rule.getOnErr());
		return Optional.empty();
	}
}