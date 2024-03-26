public class Main {


    public static void main(String[] args) {

        String str = "AAAABCSCVCSSSCVCSVSCSVSSSVCVCAccccccAAAAVCASSSSSSSSSSSSSS";
        System.out.println("Original 8-bits size in memory is " + str.length() * 8 + " bits");
        String compressed = Compressor.compress(str);
        System.out.println("The size of the compressed string is " + compressed.length() + " bits");
        String originalStr = Compressor.decompress(compressed);
        System.out.println("str before compressing =? str after compressing : "+str.equals(originalStr));
    }
}