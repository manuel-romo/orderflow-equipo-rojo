package mx.edu.orderflow.orders; import static org.junit.jupiter.api.Assertions.*; import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
class OrderServiceTest { @Test void createsValidOrder(){var s=new OrderService(); var o=s.create("student-1",new BigDecimal("150.00")); assertEquals(OrderStatus.CREATED,o.status()); assertEquals("student-1",o.customerId());} @Test void rejectsNegativeTotal(){var s=new OrderService(); assertThrows(IllegalArgumentException.class,()->s.create("student-1",new BigDecimal("-1")));}


    @Test void cancelsOrder() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        assertEquals(OrderStatus.CANCELLED, s.cancel(o.id()).status());
    }

    @Test void cancelThrowsWhenAlreadyCancelled() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("100.00"));
        s.cancel(o.id());
        assertThrows(IllegalStateException.class, () -> s.cancel(o.id()));
    }

    @Test void cancelThrowsWhenOrderNotFound() {
        var s = new OrderService();
        assertThrows(NoSuchElementException.class, () -> s.cancel(999L));
    }

}
