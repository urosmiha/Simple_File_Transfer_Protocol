public class HelperFunctions {

    public HelperFunctions() {}

    public Type changeType(String type, Type c_type) {
        switch (type) {
            case "A":
                System.out.println("ASCII");
                return Type.ASCII;
            case "B":
                System.out.println("BINARY");
                return Type.BINARY;
            case "C":
                System.out.println("CONTINUOUS");
                return Type.CONTINUOUS;
            default:
                return c_type;
        }
    }
}
