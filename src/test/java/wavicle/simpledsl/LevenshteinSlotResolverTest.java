package wavicle.simpledsl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;

/**
 * Tests {@link LevenshteinSlotResolver}
 * 
 * @author Shashank Araokar
 *
 */
public class LevenshteinSlotResolverTest {

	/**
	 * Verifies that by default, upto 20% distance is allowed.
	 * 
	 * - Calfonia is recognized as 'California'
	 * 
	 * - Cafonia is not recognized (too much distance)
	 */
	@Test
	public void default_test() {
		LevenshteinSlotResolver resolver = new LevenshteinSlotResolver(() -> {
			return Collections.singletonMap("California", new HashSet<>(Arrays.asList("Cali", "CA")));
		});

		assertEquals("California", resolver.resolve("Calfonia"));
		assertEquals("California", resolver.resolve("calFonia"));
		assertEquals(null, resolver.resolve("Cafonia"));
	}
}
