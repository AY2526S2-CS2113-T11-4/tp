package dextro.parser;

import java.util.HashMap;
import java.util.Map;
import dextro.exception.ParseException;


public class ArgumentTokenizer {
    private final Map<String, String> tokenMap = new HashMap<>();

    public ArgumentTokenizer(String args, String... prefixes) throws ParseException {
        int cursor = 0;

        while (cursor < args.length()) {

            String matchedPrefix = null;

            // Match prefix ONLY if preceded by whitespace or at start
            for (String prefix : prefixes) {
                if (args.startsWith(prefix, cursor) &&
                        (cursor == 0 || Character.isWhitespace(args.charAt(cursor - 1)))) {
                    matchedPrefix = prefix;
                    break;
                }
            }

            if (matchedPrefix == null) {
                cursor++;
                continue;
            }

            if (tokenMap.containsKey(matchedPrefix)) {
                throw new ParseException("Duplicate prefix: \"" + matchedPrefix + "\" at index: " + cursor);
            }

            int valueStart = cursor + matchedPrefix.length();
            int nextPrefixIndex = args.length();

            // Find next VALID prefix (same whitespace rule)
            for (int i = valueStart; i < args.length(); i++) {
                for (String prefix : prefixes) {
                    if (args.startsWith(prefix, i) &&
                            Character.isWhitespace(args.charAt(i - 1))) {
                        nextPrefixIndex = i;
                        break;
                    }
                }
                if (nextPrefixIndex != args.length()) {
                    break;
                }
            }

            String value = args.substring(valueStart, nextPrefixIndex).trim();
            tokenMap.put(matchedPrefix, value);

            cursor = nextPrefixIndex;
        }
    }

    public String getValue(String prefix) {
        return tokenMap.get(prefix);
    }
}
