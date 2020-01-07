package application.util;

public class SessionInfo {
    public static String currentUserEmail = "Anonymous";
    public static String selectedElementCode="NONE";
    public static String selectedUserEmail="NONE";
    public static String selectedNotification="NONE";
    public static String selectedInvitation="NONE";

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static String getSelectedElementCode() {
        return selectedElementCode;
    }

    public static String getSelectedUserEmail() {
        return selectedUserEmail;
    }

    public static String getSelectedNotification() {
        return selectedNotification;
    }

    public static String getSelectedInvitation() {
        return selectedInvitation;
    }
}
