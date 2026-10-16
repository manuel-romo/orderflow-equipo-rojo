package mx.edu.orderflow.notifications;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NotificationServiceTest {

    @ParameterizedTest(name = "rechaza orderId [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void rejectsBlankOrderId(String orderId) {
        // Arrange
        var service = new NotificationService();
        // Act y Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> service.confirmation(orderId, "c-1"));
        assertEquals("orderId required", ex.getMessage());
    }

    @ParameterizedTest(name = "orden {0} / cliente {1}")
    @CsvSource({
            "1001, c-1",
            "A-77, c-2",
            "5432, c-9"
    })
    void buildsConfirmationMessage(String orderId, String customerId) {
        // Arrange
        var service = new NotificationService();
        // Act
        var message = service.confirmation(orderId, customerId);
        // Assert
        assertEquals("Order " + orderId + " confirmed", message.message());
        assertEquals(orderId, message.orderId());
        assertEquals(customerId, message.customerId());
    }


}
