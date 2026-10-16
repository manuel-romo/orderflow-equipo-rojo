package mx.edu.orderflow.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

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
    void appliesStudent10Discount() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        var updated = s.applyDiscount(o.id(), "STUDENT10");
        assertEquals(0, updated.total().compareTo(new BigDecimal("90.00")));
    }

    @Test
    void appliesStudent30DiscountWhenMinimumMet() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("60.00"));
        var updated = s.applyDiscount(o.id(), "STUDENT30");
        assertEquals(0, updated.total().compareTo(new BigDecimal("42.00")));
    }

    @Test
    void rejectsStudent30BelowMinimumTotal() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("30.00"));
        assertThrows(IllegalArgumentException.class, () -> s.applyDiscount(o.id(), "STUDENT30"));
    }

    @Test
    void rejectsUnknownOrBlankCoupon() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> s.applyDiscount(o.id(), "BOGUS"));
        assertThrows(IllegalArgumentException.class, () -> s.applyDiscount(o.id(), "  "));
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

}
