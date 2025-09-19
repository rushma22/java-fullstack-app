
package model;

public class Util {

    public static String generateCode() {
        int r = (int) (Math.random() * 1000000);
        return String.format("%06d", r);

    }
    
    public static boolean isEmailValid(String email){
        return email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~@\\-]+@[a-zA-Z0-9.\\-]+$");
    }
    public static boolean isPasswordValid(String pass){
        return pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }
    
    public static boolean isCodeValid(String code){
        return code.matches("^\\d{4,5}$"); // digit with minimu 4 maximum 5
    }
    
     public static boolean isInteger(String value){
        return value.matches("^\\d+$"); // digit with minimu 4 maximum 5
    }
     public static boolean isDouble(String value){
       return value.matches("^\\d+(\\.\\d{2})?$"); // digit with minimu 4 maximum 5
    }
      public static boolean isMobileValid(String mobile) {
        return mobile.matches("^07[0145678][0-9]{7}$");
    }
}
