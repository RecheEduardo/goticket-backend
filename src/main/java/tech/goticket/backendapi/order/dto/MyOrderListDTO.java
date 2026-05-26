package tech.goticket.backendapi.order.dto;

import java.util.List;

public record MyOrderListDTO (int page,
                              int pageSize,
                              int totalPages,
                              long totalElements,
                              List<MyOrderListItemDTO> myOrderListItemDTOList) {
}
