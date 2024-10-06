/*
 * Copyright 2024 Taskflow, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private final int    startPosition;

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
        return String.format("{token: %s, startPosition: %d, endPosition: %d}", token, startPosition, getEndPosition());
    }
}
