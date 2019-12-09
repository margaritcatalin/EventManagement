package application.util;

public class ValidationUtil {

    public static boolean validateEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean validatePassword(String password, String confirmPassword) {
        if(password.isEmpty() || confirmPassword.isEmpty()){
            return false;
        }
        return password.equals(confirmPassword);
    }
}
