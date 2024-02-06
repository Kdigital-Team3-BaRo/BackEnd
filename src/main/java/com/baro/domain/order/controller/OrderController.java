package com.baro.domain.order.controller;

import com.baro.domain.order.domain.Order;
import com.baro.domain.order.repository.DTO.OrderCocktailDTO;
import com.baro.domain.order.repository.DTO.OrderStoreDataDTO;
import com.baro.domain.order.service.MakeCocktailService;
import com.baro.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = {"Authorization", "Content-Type"})
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MakeCocktailService makeCocktailService;

    @PostMapping("/orderCocktail")
    public ResponseEntity order_cocktail_service(@RequestBody OrderCocktailDTO orderCocktailDTO){
        log.info("order_cocktail_start");

        OrderStoreDataDTO orderData = orderService.order_cocktail_service(orderCocktailDTO);
        Map<String, Object> response = new HashMap<>();

        if(orderData != null){
            response.put("status", "success");
            response.put("message", "Order placed successfully");
            response.put("data", orderData);

            return ResponseEntity.ok(response);
        }
        else{
            response.put("status", "error");
            response.put("message", "Failed to place order. Please check your input.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @GetMapping("/makeCocktail/{orderCode}/{speed}")
    public ResponseEntity make_cocktail_service(@PathVariable String orderCode ,  @PathVariable String speed) {
        log.info("칵테일 제조를 위한 Data 전송을 시작합니다. order -> {}", orderCode);
        Map<String, Object> response = new HashMap<>();
        /**
         * 처리사항
         * order Code check x
         * machine setting check x
         * order 레시피 check x
         * make Gcode x
         * if -> next customer -> check message
         * else -> order Completed
         */

        //order code check
        if (!orderService.orderCode_check_service(orderCode)) {
            //order checking 문제발생
            response.put("status", "error");
            response.put("message", "Failed to place order. Please check your input.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        // order data find
        Optional<Order> orderOptional = orderService.order_data_find_service(orderCode);

        if (orderOptional.isPresent()) {
            // order 데이터가 존재할 경우
            Order order = orderOptional.get();
            StringBuilder gcodeBuilder = makeCocktailService.order_make_cocktail_service(order, speed);

            // StringBuilder를 String으로 변환
            String gcode = gcodeBuilder.toString();
            String userPhone = order.getUserPhoneNumber();

            // 처리 완료 후 응답
            response.put("status", "success");
            response.put("message", "Order placed successfully.");
            response.put("gcode", gcode); // gcode를 JSON 응답에 추가
        } else {
            // order 데이터가 없는 경우
            response.put("status", "error");
            response.put("message", "Order data not found for orderCode: " + orderCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);

    }
}