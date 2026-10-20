package mx.edu.orderflow.notifications;

public class NotificationApplication {
    public static void main(String[] args) {
        String orderId = args.length > 0 ? args[0] : "1001";
        String customerId = args.length > 1 ? args[1] : "c-1";

        var message = new NotificationService().confirmation(orderId, customerId);
        System.out.println(message.message());
    }
}