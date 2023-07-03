package application;

public class SessionHandler {
    private static SessionHandler instance;
    private static String currentUser;

    private SessionHandler() {}

    public static synchronized SessionHandler getInstance() {
        if (instance == null) {
            instance = new SessionHandler();
        }
        return instance;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
    	System.out.println(currentUser);
        SessionHandler.currentUser = currentUser;
    }

    public void clearSession() {
        currentUser = null;
    }
}