package io.github.notnakura.doorlightblocker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.google.gson.JsonParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LangFilesTest {
	private static final Path LANG_DIR = Path.of("src/main/resources/assets/door_light_blocker/lang");

	@ParameterizedTest
	@ValueSource(strings = {"es_ar", "es_cl", "es_ec", "es_es", "es_mx", "es_uy", "es_ve"})
	void translationHasSameKeysAsEnglish(String locale) throws IOException {
		assertEquals(keys("en_us"), keys(locale));
	}

	private static Set<String> keys(String locale) throws IOException {
		try (Reader reader = Files.newBufferedReader(LANG_DIR.resolve(locale + ".json"))) {
			return JsonParser.parseReader(reader).getAsJsonObject().keySet();
		}
	}
}
