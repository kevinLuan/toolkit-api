package cn.feiliu.taskflow.toolkit.contentfilter;

import lombok.Getter;

/**
 * Represents a tokenized keyword with its position in the original text.
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-10-06
 */
@Getter
public class KeywordTokenizer {
    private final String token;
    private final int startPosition;

    /**
     * Constructs a new KeywordTokenizer.
     *
     * @param token The keyword token.
     * @param startPosition The starting position of the token in the original text.
     */
    public KeywordTokenizer(String token, int startPosition) {
        this.token = token;
        this.startPosition = startPosition;
    }

    /**
     * Calculates the end position of the token in the original text.
     *
     * @return The end position of the token.
     */
    public int getEndPosition() {
        return startPosition + token.length();
    }

    @Override
    public String toString() {
        return String.format("{token: %s, startPosition: %d, endPosition: %d}", 
                             token, startPosition, getEndPosition());
    }
}
