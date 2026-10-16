package mx.edu.orderflow.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class OrderServiceTest {
    @Test
    void createsValidOrder() {
        // Arrange
        var s = new OrderService();
        // Act
        var o = s.create("student-1", new BigDecimal("150.00"));
        // Assert
        assertEquals(OrderStatus.CREATED, o.status());
        assertEquals("student-1", o.customerId());
    }

    @Test
    void rejectsNegativeTotal() {
        var s = new OrderService();
        assertThrows(IllegalArgumentException.class, () -> s.create("student-1", new BigDecimal("-1")));
    }


    @Test
    void cancelsOrder() {
        // Arrange
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        // Act
        var canceledO = s.cancel(o.id());
        // Assert
        assertEquals(OrderStatus.CANCELLED, canceledO.status());
    }

    @Test
    void cancelThrowsWhenAlreadyCancelled() {
        // Arrange
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        s.cancel(o.id());
        // Act y Assert
        assertThrows(IllegalStateException.class, () -> s.cancel(o.id()));
    }

    @Test
    void cancelThrowsWhenOrderNotFound() {
        // Arrange
        var s = new OrderService();
        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> s.cancel(999L));
    }

    @Test
    void discountThrowsWhenOrderNotCreated() {
        // Arrange
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        s.cancel(o.id());
        // Act y Assert
        assertThrows(IllegalStateException.class, () -> s.applyDiscount(o.id(), "STUDENT10"));
    }

    @Test
    void findByStatusReturnsMatchingOrders() {
        // Arrange
        var s = new OrderService();
        s.create("student-1", new BigDecimal("100.00"));
        var cancelled = s.create("student-2", new BigDecimal("100.00"));
        s.cancel(cancelled.id());
        // Act
        var createdOrders = s.findByStatus(OrderStatus.CREATED);
        var cancelledOrders = s.findByStatus(OrderStatus.CANCELLED);
        // Assert
        assertEquals(1, createdOrders.size());
        assertEquals(1, cancelledOrders.size());
    }

    @Test
    void findByStatusThrowsWhenStatusNull() {
        // Arrange
        var s = new OrderService();
        // Act y Assert
        assertThrows(IllegalArgumentException.class, () -> s.findByStatus(null));
    }

    @ParameterizedTest(name = "{0} sobre {1} -> {2}")
    @CsvSource({
            "STUDENT10, 70.00, 63.00",
            "STUDENT30, 70.00, 49.00",
            "SENIOR50,  70.00, 35.00"
    })
    void appliesDiscount(String couponCode, BigDecimal total, BigDecimal expectedTotal) {
        // Arrange
        var service = new OrderService();
        var order = service.create("c-1", total);
        // Act
        var updated = service.applyDiscount(order.id(), couponCode);
        // Assert
        assertEquals(0, updated.total().compareTo(expectedTotal));
    }

    @ParameterizedTest(name = "STUDENT30 sobre {0} -> {1}")
    @CsvSource({
            "50.00, 35.00",
            "50.01, 35.007",
            "51.00, 35.70",
            "60.00, 42.00"
    })
    void appliesStudent30DiscountAtOrAboveMinimum(BigDecimal total, BigDecimal expectedTotal) {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", total);
        // Act
        var updated = service.applyDiscount(order.id(), "STUDENT30");
        // Assert
        assertEquals(0, updated.total().compareTo(expectedTotal));
    }

    @ParameterizedTest(name = "STUDENT30 rechaza total {0}")
    @ValueSource(strings = {"0.01", "30.00", "49.99"})
    void rejectsStudent30BelowMinimumTotal(BigDecimal total) {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", total);
        // Act y Assert
        assertThrows(IllegalArgumentException.class, () -> service.applyDiscount(order.id(), "STUDENT30"));
    }

    @ParameterizedTest(name = "cupón [{0}] se normaliza")
    @ValueSource(strings = {"STUDENT10", "student10", "  Student10  "})
    void normalizesCouponCode(String couponCode) {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", new BigDecimal("100.00"));
        // Act
        var updated = service.applyDiscount(order.id(), couponCode);
        // Assert
        assertEquals(0, updated.total().compareTo(new BigDecimal("90.00")));
    }

    @ParameterizedTest(name = "rechaza cupón [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "BOGUS", "STUDENT"})
    void rejectsInvalidCoupon(String couponCode) {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", new BigDecimal("100.00"));
        // Act y Assert
        assertThrows(IllegalArgumentException.class, () -> service.applyDiscount(order.id(), couponCode));
    }
}
