package tech.goticket.backendapi.order.util;

import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class OrderUtils {
    public static Map<Long, Integer> countByAllotment(Order order) {
        Map<Long, Integer> counts = new HashMap<>();
        for (OrderItem item : order.getItems()) {
            counts.merge(item.getBatchAllotment().getAllotmentId(), 1, Integer::sum);
        }
        return counts;
    }
}
