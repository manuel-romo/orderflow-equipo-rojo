package mx.edu.orderflow.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderServiceTest {
    @Test
    void createsValidOrder() {
        // Arrange
        var service = new OrderService();
        // Act
        var order = service.create("student-1", new BigDecimal("150.00"));
        // Assert
        assertEquals(OrderStatus.CREATED, order.status());
        assertEquals("student-1", order.customerId());
        assertEquals(0, order.total().compareTo(new BigDecimal("150.00")));
    }


    @Test
    void cancelsOrder() {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", new BigDecimal("100.00"));
        // Act
        var canceledOrder = service.cancel(order.id());
        // Assert
        assertEquals(OrderStatus.CANCELLED, canceledOrder.status());
    }

    @Test
    void cancelThrowsWhenAlreadyCancelled() {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", new BigDecimal("100.00"));
        service.cancel(order.id());
        // Act y Assert
        assertThrows(IllegalStateException.class, () -> service.cancel(order.id()));
    }

    @Test
    void cancelThrowsWhenOrderNotFound() {
        // Arrange
        var service = new OrderService();
        // Act y Assert
        assertThrows(NoSuchElementException.class, () -> service.cancel(999L));
    }

    @Test
    void discountThrowsWhenOrderNotCreated() {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", new BigDecimal("100.00"));
        service.cancel(order.id());
        // Act y Assert
        assertThrows(IllegalStateException.class, () -> service.applyDiscount(order.id(), "STUDENT10"));
    }

    @Test
    void findByStatusReturnsMatchingOrders() {
        // Arrange
        var service = new OrderService();
        var created = service.create("student-1", new BigDecimal("100.00"));
        var toCancel = service.create("student-2", new BigDecimal("100.00"));
        var cancelled = service.cancel(toCancel.id());
        // Act
        var createdOrders = service.findByStatus(OrderStatus.CREATED);
        var cancelledOrders = service.findByStatus(OrderStatus.CANCELLED);
        // Assert
        assertEquals(List.of(created), createdOrders);
        assertEquals(List.of(cancelled), cancelledOrders);
    }

    @Test
    void findByStatusThrowsWhenStatusNull() {
        // Arrange
        var service = new OrderService();
        // Act y Assert
        assertThrows(IllegalArgumentException.class, () -> service.findByStatus(null));
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
            "50.01, 35.01",
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

    @ParameterizedTest(name = "rechaza customerId [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void rejectsInvalidCustomerId(String customerId) {
        // Arrange
        var service = new OrderService();
        // Act y Assert
        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(customerId, new BigDecimal("100.00")));
        assertEquals("customerId required", ex.getMessage());
    }

    @ParameterizedTest(name = "rejects total [{0}]")
    @NullSource
    @ValueSource(strings = {"0", "0.00", "-0.01", "-1", "-100.00"})
    void rejectsNonPositiveTotal(BigDecimal total) {
        // Arrange
        var service = new OrderService();
        // Act y Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> service.create("student-1", total));
        assertEquals("total must be positive", ex.getMessage());
        assertTrue(service.list().isEmpty(), "a rejected order must not be stored");
    }

    @Test
    void assignsSequentialIds() {
        // Arrange
        var service = new OrderService();
        // Act
        var first = service.create("student-1", new BigDecimal("10.00"));
        var second = service.create("student-2", new BigDecimal("20.00"));
        // Assert
        assertEquals(1001L, first.id());
        assertEquals(1002L, second.id());
    }
    
    @Test
    void findReturnsOrderWhenExists() {
        // Arrange
        var service = new OrderService();
        var created = service.create("student-1", new BigDecimal("100.00"));
        // Act
        var found = service.find(created.id());
        // Assert
        assertTrue(found.isPresent());
        assertEquals("student-1", found.get().customerId());
    }

    @Test
    void findReturnsEmptyWhenMissing() {
        // Arrange
        var service = new OrderService();
        // Act
        var found = service.find(999L);
        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    void listReturnsDefensiveCopy() {
        // Arrange
        var service = new OrderService();
        service.create("student-1", new BigDecimal("100.00"));
        var orders = service.list();
        // Act
        orders.clear();
        // Assert
        assertEquals(1, service.list().size());
    }

    @Test
    void cancelPersistsCancelledOrderKeepingData() {
        // Arrange
        var service = new OrderService();
        var order = service.create("student-1", new BigDecimal("100.00"));
        // Act
        service.cancel(order.id());
        var stored = service.find(order.id()).orElseThrow();
        // Assert
        assertEquals(OrderStatus.CANCELLED, stored.status());
        assertEquals(order.customerId(), stored.customerId());
        assertEquals(order.total(), stored.total());
    }

    @Test
    void discountThrowsWhenOrderNotFound() {
        // Arrange
        var service = new OrderService();
        // Act y Assert
        var ex = assertThrows(NoSuchElementException.class, () -> service.applyDiscount(999L, "STUDENT10"));
        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    void findByStatusReturnsEmptyWhenNoOrderMatches() {
        // Arrange
        var service = new OrderService();
        service.create("student-1", new BigDecimal("100.00"));
        // Act
        var cancelled = service.findByStatus(OrderStatus.CANCELLED);
        // Assert
        assertTrue(cancelled.isEmpty());
    }
}
