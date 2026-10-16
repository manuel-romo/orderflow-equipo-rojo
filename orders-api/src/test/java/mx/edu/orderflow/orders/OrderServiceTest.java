package mx.edu.orderflow.orders;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;


class OrderServiceTest {
    @Test
    void createsValidOrder() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("150.00"));
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
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        assertEquals(OrderStatus.CANCELLED, s.cancel(o.id()).status());
    }

    @Test
    void cancelThrowsWhenAlreadyCancelled() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        s.cancel(o.id());
        assertThrows(IllegalStateException.class, () -> s.cancel(o.id()));
    }

    @Test
    void cancelThrowsWhenOrderNotFound() {
        var s = new OrderService();
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
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        s.cancel(o.id());
        assertThrows(IllegalStateException.class, () -> s.applyDiscount(o.id(), "STUDENT10"));
    }

    @Test
    void findByStatusReturnsMatchingOrders() {
        var s = new OrderService();
        var created = s.create("student-1", new BigDecimal("100.00"));
        var cancelled = s.create("student-2", new BigDecimal("100.00"));
        s.cancel(cancelled.id());
        assertEquals(1, s.findByStatus(OrderStatus.CREATED).size());
        assertEquals(1, s.findByStatus(OrderStatus.CANCELLED).size());
    }

    @Test
    void findByStatusThrowsWhenStatusNull() {
        var s = new OrderService();
        assertThrows(IllegalArgumentException.class, () -> s.findByStatus(null));
    }
    //Propuesta de test
    /**
     * Verifica que no se pueda crear un pedido con total de cero o negativo.
     * Cada valor del arreglo es un caso: debe lanzar IllegalArgumentException
     * y el pedido rechazado no debe quedar guardado en el servicio.
     */
    
    @ParameterizedTest(name = "Total = {0} -> el pedido es rechazado")
    @ValueSource(strings = {"0", "0.00", "-0.01", "-1", "-100.00"})
    void createRejectsZeroAndNegativeTotals(String total) {
        var s = new OrderService();
        assertThrows(IllegalArgumentException.class, () -> s.create("student-1", new BigDecimal(total)));
        assertTrue(s.list().isEmpty(), "una orden rechazada no debe guardarse");
    }
}
