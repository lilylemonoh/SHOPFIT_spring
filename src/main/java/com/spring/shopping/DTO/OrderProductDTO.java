    package com.spring.shopping.DTO;

    import lombok.*;

    import java.util.List;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class OrderProductDTO {
        private Long orderProductId;
        private Long orderId;
        private Long productId;
        private Long quantity;
    }
