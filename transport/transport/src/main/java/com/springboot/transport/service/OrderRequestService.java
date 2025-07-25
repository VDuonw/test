package com.springboot.transport.service;
import com.springboot.transport.entity.OrderRequest;
import com.springboot.transport.entity.Surveyor;
import com.springboot.transport.repository.CustomerServiceRepository;
import com.springboot.transport.repository.OrderRequestRepository;
import com.springboot.transport.repository.SurveyorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderRequestService {

    @Autowired
    private OrderRequestRepository orderRequestRepository;
    
    @Autowired
    private CustomerServiceRepository customerServiceRepository;

     @Autowired
    private SurveyorRepository surveyorRepository;

    @Autowired
    private SurveyService surveyService;

   

    public List<OrderRequest> getPendingOrders() {
        // Lấy tất cả các đơn hàng đang chờ
        List<OrderRequest> pendingOrders = orderRequestRepository.findByStatus("pending");

        // Duyệt qua từng đơn hàng và gán giá trị cho hasSurveyActive
        return pendingOrders.stream().map(order -> {
            try {
                // Lấy serviceId của đơn hàng
                Integer serviceId = order.getServiceId();

                // Kiểm tra xem serviceId có null không
                if (serviceId != null && serviceId > 0) {
                    // Kiểm tra xem dịch vụ có yêu cầu khảo sát và trạng thái khảo sát là 'active' không
                    boolean requiresSurvey = customerServiceRepository.findById(serviceId)
                            .map((com.springboot.transport.entity.CustomerService service) -> service.getRequiresSurvey() == true)
                            .orElse(false);
                    order.setHasSurveyActive(requiresSurvey);
                } else {
                    order.setHasSurveyActive(false); // Nếu không có serviceId hoặc serviceId không hợp lệ
                }
            } catch (Exception e) {
                e.printStackTrace();
                order.setHasSurveyActive(false); // Nếu có lỗi, mặc định không yêu cầu khảo sát
            }
            return order;
        }).collect(Collectors.toList());
    }

    public OrderRequest getOrderById(long orderId) {
        return orderRequestRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));
    }


    

    public Surveyor getSurveyorById(Integer surveyorId) {
        return surveyorRepository.findById(surveyorId) // Giả sử Surveyor ID là Long
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên khảo sát với ID: " + surveyorId));
    }

public boolean hasSurveyActive(Integer orderId) {
        OrderRequest order = getOrderById(orderId);
        Integer serviceId = order.getServiceId();
        return customerServiceRepository.findById(serviceId)
                .map(service -> service.getRequiresSurvey() == true)
                .orElse(false);
    }

    public List<Surveyor> getAvailableSurveyors() {
        return surveyorRepository.findAllActiveSurveyors();
    }

    public void updateOrder(OrderRequest order) {
        orderRequestRepository.save(order);
    }

    public void updateOrderStatus(OrderRequest order, String status) {
        List<String> validStatuses = Arrays.asList("pending", "reviewing", "confirmed", "rejected");
        if (!validStatuses.contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Must be one of: " + validStatuses);
        }
        order.setStatus(status);
        orderRequestRepository.save(order);
    }

    public List<OrderRequest> getPendingOrdersWithoutSurvey() {
        // Lấy tất cả các đơn hàng đang chờ
        List<OrderRequest> pendingOrders = orderRequestRepository.findByStatus("pending");
        
        // Lọc ra các đơn hàng chưa có survey và cập nhật hasSurveyActive
        return pendingOrders.stream()
            .filter(order -> {
                try {
                    return !surveyService.existsSurveyForOrder(order.getOrderId());
                } catch (Exception e) {
                    e.printStackTrace();
                    return true; // Nếu có lỗi, giả sử là chưa có survey
                }
            })
            .map(order -> {
                Integer serviceId = order.getServiceId();
                if (serviceId != null) {
                    boolean requiresSurvey = customerServiceRepository.findById(serviceId)
                            .map(service -> service.getRequiresSurvey() == true)
                            .orElse(false);
                    order.setHasSurveyActive(requiresSurvey);
                } else {
                    order.setHasSurveyActive(false);
                }
                return order;
            })
            .collect(Collectors.toList());
    }
}