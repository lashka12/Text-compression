With 8 bits, we can represent 256 different characters! That's plenty for the English alphabet, numbers and symbols.
Each character is represented by a unique combination of 8 bits, which can be either 0 or 1. But why 8 bits?
Let's Break It Down: Think of each bit as a switch that can be either on (1) or off (0). With 8 switches, how many different combinations can we make?
We're looking for 2^8, which equals 256. That's just what we need to cover all characters!
To represent the string "ABCCBBCCAABCABBAACBA" in memory using ASCII encoding, each character would be stored as its corresponding 8-bit binary representation. Here's how it would look:
'A': 01000001
'B': 01000010
'C': 01000011
So, "ABCCBBCCAABCABBAACBA" would be stored as the following sequence of bits:
01000001 01000010 01000011 01000011 01000010 01000010 01000011 01000011 01000001 01000001 01000010 01000010 01000001 01000001 01000011 01000010 01000010 01000001 01000001 01000001
Total bits = Number of characters × Bits per character 
20 characters × 8 bits per character = 160 bits
Therefore, a total of 160 bits are used to store the string "ABCCBBCCAABCABBAACBA" in memory using ASCII encoding.

But Do We Always Need 8 Bits?
We've established that ASCII encoding typically uses 8 bits (1 byte) to represent characters. But here's the thing: do we always need all 8 bits to represent every character, especially if our data contains a limited set of characters?
let's calculate the number of bits needed to represent the characters in "ABCCBBCCAABCABBAACBA".
There are 3 unique characters in "ABCCBBCCAABCABBAACBA"
static int countUniqueChars(String s) {
    Set<Character> uniqueChars = new HashSet<>();
    for (char c : s.toCharArray()) {
        uniqueChars.add(c);
    }
    return uniqueChars.size();
}
countUniqueChars("ABCCBBCCAABCABBAACBA") => 3
Now, using the formula:
bits = ⌈log2(number of unique characters)⌉
bits = ⌈log2(3)⌉
bits = ⌈1.58496250072⌉ = 2
static int calculateMinBits(int length) {
    return (int) Math.ceil(Math.log(length) / Math.log(2));
}
calculateMinBits(3) => 2
So, we need at least 2 bits to represent the unique characters in "ABCCBBCCAABCABBAACBA".
Let's create a function to create the first 2-bits code according to the length:
static String constructZerosString(int length) {
    return "0".repeat(Math.max(0, length));
}
constructZerosString(2) => "00"
We need one more function to increment the binary number in order to represent the following chars:
static String incrementBinary(String binaryString) {

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
Let's test it out:
incrementBinary("00") => "01"
We will assign the initial code to the first letter, and then increment it before assigning it to the following letters. 
We'll utilize the functions we've written before to generate a map. This map will contain the ASCII value of each letter as a key and the corresponding new code as its value.
static Map<Integer, String> createCharacterToBinaryMap(String str) {

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
createCharacterToBinaryMap("ABCCBBCCAABCABBAACBA") ⇒ {65=00, 66=01, 67=10}
Encoding:
After establishing the mapping, our next step is to create a function responsible for encoding the string based on this mapping
static String encode(String str, Map<Integer, String> codesMap) {
    StringBuilder sb = new StringBuilder();
    for (char c : str.toCharArray()) {
        sb.append(codesMap.get(getAsciiValue(c)));
    }
    return sb.toString();
}
encode("ABCCBBCCAABCABBAACBA", charsBinaryMap) => "000101101010011011001010101001100101011001010011010101010"
The new bits representation size = 40 bits ( 20 Characters * 2 bits )
We successfully compressed the representation of the string from its original 160 bits to 40 bits using our encoding scheme. By assigning shorter bit sequences to frequently occurring characters and utilizing efficient encoding techniques, we were able to achieve a substantial reduction in the overall size of the encoded string.
Decoding:
Let's create the decoding function, which will translate a string of bits back into its original characters using a provided map of binary codes. Here's how it works: the function iterates through the bit string, matches each substring of bits to its corresponding character code in the map, appends the decoded characters to a StringBuilder, and finally returns the decoded string.
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
decode("000101101010011011001010101001100101011001010011010101010", charsBinaryMap) => "ABCCBBCCAABCABBAACBA"

Are we done?
So, we've successfully compressed our string using a clever encoding technique, assigning shorter bit sequences to frequently occurring characters. But before we pat ourselves on the back and call it a day, there's one crucial aspect we need to address: preserving the map.
Why do we need to save the map?
While the compressed string might look tidy and efficient, it's useless without the map that holds the key-value pairs of original ASCII values and their corresponding compressed representations. Without the map, we have no way of decoding the compressed string back to its original form.
So how much will it cost ?
map = {65=00, 66=01, 67=10}
Since we can only handle 1/0 in memory Let's translate the ASCII keys to 8 bits :
static Map<String, String> getConversionMap(Map<Integer, String> charsBinaryMap) {
    
    Map<String, String> map = new HashMap<>();

    for (Map.Entry<Integer, String> entry : charsBinaryMap.entrySet()) {
        map.put(String.format("%8s", Integer.toBinaryString(entry.getKey()))
                .replace(' ', '0'), entry.getValue());
    }
    return map;
}
getConversionMap(map) => {01000001=00, 01000010=01, 01000011=10}
One way of encoding the map is by concatenating key-value as a stream :
01000001 00 01000010 01 01000011 10
But we should add some metadata to it in order to be able to decode it later :
Map Size: We'll use 8 bits to represent the size of the map, allowing for a maximum of 256 entries.
Value Length: We'll use 3 bits to represent the length of the value, allowing for values from 1 to 8.
Here's the refactored encoding process:
| Map Size (8 bits) | Value Length (3 bits) | Key-Value Pairs |
Let's encode the map {01000001=00, 01000010=01, 01000011=10} into a stream of bits using this format:
Map size: 3 (binary: 00000011)
Value length: 2 (binary: 010)
Key-value pairs : 
01000001 (key) + 00 (value) 
01000010 (key) + 01 (value) 
01000011 (key) + 10 (value)
Now, let's concatenate everything together:
public static String encodeMap(Map<String, String> map) {
    
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
map = {01000001=00, 01000010=01, 01000011=10}
encodeMap(map) => "00000011010010000010001000010010100001110"
00000011 010 01000001 00 01000010 01 01000011 10

The size of the encoded map is 41 bits



We will also need a function to decode the encoded stream of bits back into the original map, using the metadata included in the encoding:
public static Map<String, String> decodeMap(String encodedStream) {
    
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
String mapAsBits = "00000011001010000111001000010010100000100"
decodeMap(mapAsBits) => {01000011=10, 01000010=01, 01000001=00}
With the ability to encode both the content and the map, you now have a complete encoding and decoding system in place. This allows us to compress and decompress any content :
the compressed representation = encoded map bits + encoded content bits
The compress method takes a string content as input and compresses it using the previous methods:
Generating the map: Each character in the input content is mapped to its corresponding binary representation.
Content Encoding: The input content is encoded using the map, replacing each character with its binary representation.
Map Encoding: The conversion map is encoded into a stream of bits to compress it.
Concatenation: The encoded map and the encoded content are concatenated together to produce the final compressed string.
static String compress(String content) {

    Map<Integer, String> charsBinaryMap = createCharacterToBinaryMap(content);
    String binaryRepresentation = encode(content, charsBinaryMap);
    Map<String, String> map = getConversionMap(charsBinaryMap);
    String encodedMap = encodeMap(map);
    return encodedMap + binaryRepresentation;

}
String str = "ABCCBBCCAABCABBAACBA";
String compressedStr = compress(str);
System.out.println("The compressed string is " + compressedStr);
The compressed string is 000000110010100001110010000100101000001000001101001011010000001100001010000100100

The size of the compressed string is 81 bits
Conclusion :
The reduction in size from 160 bits to 81 bits using the compression algorithm reflects its effectiveness in encoding recurring patterns or characters more efficiently. This optimization demonstrates the potential for significant savings in storage space or bandwidth usage, particularly in scenarios with large volumes of data.
However, it's crucial to analyze the characteristics of the data before applying the compression algorithm. Not all strings may benefit equally from this method, and careful consideration is necessary to determine its suitability.
The compression method is particularly well-suited for strings with recurring patterns or characters, as these elements can be encoded more efficiently, resulting in substantial size reductions without sacrificing information.

Here's a real sample text that contains recurring patterns and characters :
String str = 

"The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.";

System.out.println("8-bits size in memory is "+ str.length() * 8);
String compressed = compress(str);
System.out.println("The size of the compressed string is " + compressed.length()); 
Original 8-bits size in memory is 3600
The size of the compressed string is 2651

Process finished with exit code 0
Original size = 3600 bits
Compressed size = 2651 bits

Compression ratio (%) = ((3600 - 2651) / 3600) * 100 ≈ 26.39%
This means that the compressed data is approximately 26.39% smaller than the original data, which indicates a significant reduction in size achieved by the compression algorithm.
