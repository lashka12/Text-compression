import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Compressor {


    public static String compress(String content) {

        Map<Integer, String> charsBinaryMap = createCharacterToBinaryMap(content);
        String binaryRepresentation = encode(content, charsBinaryMap);
        Map<String, String> map = getConversionMap(charsBinaryMap);
        String encodedMap = encodeMap(map);
        return encodedMap + binaryRepresentation;

    }

    public static String decompress(String compressed) {

        int mapSize = Integer.parseInt(compressed.substring(0, 8), 2);
        int valueLength = Integer.parseInt(compressed.substring(8, 11), 2) + 1;
        Map<String, String> map = decodeMap(compressed.substring(0, 11 + mapSize * (8 + valueLength)));
        Map<Integer, String> charsBinaryMap = convertMap(map);
        String contentBinary = compressed.substring(11 + mapSize * (8 + valueLength));
        return decode(contentBinary, charsBinaryMap);

    }

    private static Map<String, String> getConversionMap(Map<Integer, String> charsBinaryMap) {

        Map<String, String> map = new HashMap<>();

        for (Map.Entry<Integer, String> entry : charsBinaryMap.entrySet()) {
            map.put(String.format("%8s", Integer.toBinaryString(entry.getKey()))
                    .replace(' ', '0'), entry.getValue());
        }
        return map;
    }

    private static Map<Integer, String> convertMap(Map<String, String> map) {
        Map<Integer, String> convertedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            convertedMap.put(Integer.parseInt(entry.getKey(), 2), entry.getValue());
        }
        return convertedMap;
    }


    private static int countUniqueChars(String s) {
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : s.toCharArray()) {
            uniqueChars.add(c);
        }
        return uniqueChars.size();
    }


    private static String encodeMap(Map<String, String> map) {

        int mapSize = map.size();
        String mapSizeBinary = String.format("%8s", Integer.toBinaryString(mapSize)).replace(' ', '0');
        int valueLength = map.entrySet().iterator().next().getValue().length();

        String valueLengthBinary = String.format("%3s", Integer.toBinaryString(valueLength - 1)).replace(' ', '0');
        StringBuilder encodedStream = new StringBuilder();
        encodedStream.append(mapSizeBinary).append(valueLengthBinary);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            encodedStream.append(key).append(value);
        }

        return encodedStream.toString();
    }

    private static Map<String, String> decodeMap(String encodedStream) {

        Map<String, String> decodedMap = new HashMap<>();

        // Extract value length from the encoded stream
        int valueLength = Integer.parseInt(encodedStream.substring(8, 11), 2) + 1;

        // Extract key-value pairs from the encoded stream
        for (int i = 11; i < encodedStream.length(); i += (8 + valueLength)) {
            String key = encodedStream.substring(i, i + 8);
            String value = encodedStream.substring(i + 8, i + 8 + valueLength);
            decodedMap.put(key, value);
        }

        return decodedMap;
    }


    private static Map<Integer, String> createCharacterToBinaryMap(String str) {

        Map<Integer, String> codesMap = new HashMap<>();
        int uniqueChars = countUniqueChars(str);
        int length = calculateMinBits(uniqueChars);
        String zerosString = constructZerosString(length);

        for (char c : str.toCharArray()) {
            if (!codesMap.containsKey(getAsciiValue(c))) {
                codesMap.put(getAsciiValue(c), zerosString);
                zerosString = incrementBinary(zerosString);
            }

        }
        return codesMap;

    }

    private static int calculateMinBits(int length) {
        return (int) Math.ceil(Math.log(length) / Math.log(2));
    }

    private static String constructZerosString(int length) {
        return "0".repeat(Math.max(0, length));
    }

    private static String encode(String str, Map<Integer, String> codesMap) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append(codesMap.get(getAsciiValue(c)));
        }
        return sb.toString();
    }

    private static String decode(String bits, Map<Integer, String> codesMap) {
        StringBuilder sb = new StringBuilder();
        int minBits = codesMap.values().iterator().next().length();
        for (int i = 0; i < bits.length(); i += minBits) {
            String code = bits.substring(i, i + minBits);
            for (Map.Entry<Integer, String> entry : codesMap.entrySet()) {
                if (entry.getValue().equals(code)) {
                    sb.append((char) entry.getKey().intValue());
                    break;
                }
            }
        }
        return sb.toString();
    }


    private static String incrementBinary(String binaryString) {

        char[] binaryChars = binaryString.toCharArray();
        // Start from the rightmost bit
        for (int i = binaryChars.length - 1; i >= 0; i--) {
            if (binaryChars[i] == '0') {
                binaryChars[i] = '1'; // Flip the bit to '1' and exit the loop
                break;
            } else {
                binaryChars[i] = '0'; // If the bit is '1', set it to '0' and continue to the next bit
            }
        }

        return String.valueOf(binaryChars);
    }


    private static int getAsciiValue(char letter) {
        return (int) letter;
    }


}
