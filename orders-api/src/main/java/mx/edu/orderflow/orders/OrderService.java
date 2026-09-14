package mx.edu.orderflow.orders;
import java.math.BigDecimal; import java.util.*; import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
@Service public class OrderService { private final Map<Long,Order> orders=new LinkedHashMap<>(); private final AtomicLong ids=new AtomicLong(1000);
 private static final BigDecimal STUDENT30_MIN_TOTAL = new BigDecimal("50.00");

 public synchronized Order create(String customerId, BigDecimal total){ if(customerId==null||customerId.isBlank()) throw new IllegalArgumentException("customerId required"); if(total==null||total.signum()<=0) throw new IllegalArgumentException("total must be positive"); long id=ids.incrementAndGet(); Order o=new Order(id,customerId,total,OrderStatus.CREATED); orders.put(id,o); return o;}
 public synchronized List<Order> list(){ return new ArrayList<>(orders.values()); }
 public synchronized Optional<Order> find(long id){ return Optional.ofNullable(orders.get(id)); }

 private Order requireOrder(long id) {
  Order o = orders.get(id);
  if (o == null) {
   throw new NoSuchElementException("order not found: " + id);
  }
  return o;
 }

 public synchronized Order cancel(long id) {
  Order o = requireOrder(id);
  if (o.status() == OrderStatus.CANCELLED) {
   throw new IllegalStateException("order already cancelled");
  }
  Order cancelled = new Order(o.id(), o.customerId(), o.total(), OrderStatus.CANCELLED);
  orders.put(id, cancelled);
  return cancelled;
 }

 public synchronized Order applyDiscount(long id, String couponCode) {
  Order o = requireOrder(id);
  if (o.status() != OrderStatus.CREATED) {
   throw new IllegalStateException("discounts only apply to CREATED orders");
  }
  BigDecimal rate = resolveDiscountRate(couponCode, o.total());
  BigDecimal discounted = o.total().multiply(BigDecimal.ONE.subtract(rate));
  Order updated = new Order(o.id(), o.customerId(), discounted, o.status());
  orders.put(id, updated);
  return updated;
 }

 private BigDecimal resolveDiscountRate(String couponCode, BigDecimal orderTotal) {
  if (couponCode == null || couponCode.isBlank()) {
   throw new IllegalArgumentException("couponCode required");
  }
  String code = couponCode.trim().toUpperCase();
  if (code.equals("STUDENT10")) {
   return new BigDecimal("0.10");
  }
  if (code.equals("STUDENT30")) {
   if (orderTotal.compareTo(STUDENT30_MIN_TOTAL) < 0) {
    throw new IllegalArgumentException("STUDENT30 requires a minimum total of " + STUDENT30_MIN_TOTAL);
   }
   return new BigDecimal("0.30");
  }
  throw new IllegalArgumentException("unknown coupon code: " + couponCode);
 }

 public synchronized List<Order> findByStatus(OrderStatus status) {
  if (status == null) {
   throw new IllegalArgumentException("status required");
  }
  List<Order> result = new ArrayList<>();
  for (Order o : orders.values()) {
   if (o.status() == status) {
    result.add(o);
   }
  }
  return result;
 }

}
