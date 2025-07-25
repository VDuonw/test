
package com.springboot.transport.controller.receptionist;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.transport.entity.Account;
import com.springboot.transport.entity.OrderRequest;
import com.springboot.transport.entity.Receptionist;
import com.springboot.transport.entity.Survey;
import com.springboot.transport.entity.Surveyor;
import com.springboot.transport.repository.ContractRepository;
import com.springboot.transport.repository.ReceptionistRepository;
import com.springboot.transport.repository.SurveyorRepository;
import com.springboot.transport.service.AccountService;
import com.springboot.transport.service.ContractService;
import com.springboot.transport.service.OrderRequestService;
import com.springboot.transport.service.SurveyService;
import com.springboot.transport.service.SurveyorService;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    @Autowired
    private OrderRequestService orderRequestService;
    @Autowired
    private ContractService contractService;

            @Autowired
    private SurveyorService surveyorService;

    @Autowired
    private ReceptionistRepository receptionistRepository;

    @Autowired
    private SurveyorRepository surveyorRepository;

       @Autowired
    private AccountService accountService;

       @Autowired
    private SurveyService surveyService;



    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("orders", orderRequestService.getPendingOrders());
        return "receptionist/dashboard";
    }

    @GetMapping("/create-contract")
    public String showCreateContractForm(@RequestParam("orderId") Integer orderId, Model model) {
        OrderRequest order = orderRequestService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "receptionist/create-contract";
    }

    @PostMapping("/create-contract")
    public String createContract(@RequestParam("orderId") Integer orderId,
            @RequestParam("terms") String terms,
            @RequestParam(value = "preferredPickupTime", required = false) String preferredPickupTime,
            @RequestParam(value = "specialNotes", required = false) String specialNotes) {

        OrderRequest order = orderRequestService.getOrderById(orderId);

        if (preferredPickupTime != null && !preferredPickupTime.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date parsedDate = dateFormat.parse(preferredPickupTime);
                java.time.LocalDateTime localDateTime = parsedDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
                Date date = java.util.Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
                order.setPreferredPickupTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (specialNotes != null) {
            order.setSpecialNotes(specialNotes);
        }

        orderRequestService.updateOrder(order);
        contractService.createContractFromOrder(order, terms);
        orderRequestService.updateOrderStatus(order, "confirmed");

        return "redirect:/receptionist/create-contract?orderId=" + orderId + "&success=true";
    }

    @GetMapping("/view-order")
    public String viewOrderRequest(@RequestParam("orderId") Integer orderId, Model model) {
        OrderRequest order = orderRequestService.getOrderById(orderId);
        if (order == null) {
           
            model.addAttribute("errorMessage", "Order with ID " + orderId + " not found.");
            return "redirect:/receptionist/dashboard"; // Or a specific error page
        }
        model.addAttribute("order", order);
        return "receptionist/view-order";
    }

     @GetMapping("/surveys")
    public String viewSurveyRequest(@RequestParam("surveyId") Integer orderId, Model model) {
        Survey surveys = surveyService.getSurveyById(orderId);
        if (surveys == null) {
           
            model.addAttribute("errorMessage", "Survey with ID " + orderId + " not found.");
            return "redirect:/receptionist/list-surveys"; // Or a specific error page
        }
       
        model.addAttribute("surveys", surveys);
        return "receptionist/surveys";
    }


    @GetMapping("/list-surveys")
    public String listSurveys(Model model) {
        try {
            Long receptionistId = accountService.getCurrentUserRealId();
            List<Survey> surveys = surveyService.getSurveysByReceptionist(receptionistId.intValue());
            model.addAttribute("surveys", surveys);
            return "receptionist/list-surveys";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi lấy danh sách khảo sát: " + e.getMessage());
            return "receptionist/dashboard";
        }
    }

    @GetMapping("/create-survey-form")
    public String showCreateSurveyForm(@RequestParam("orderId") Integer orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            OrderRequest order = orderRequestService.getOrderById(orderId);
            List<Surveyor> availableSurveyors = surveyorService.getAvailableSurveyors();

            if (availableSurveyors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên khảo sát khả dụng để tạo khảo sát.");
                return "redirect:/receptionist/dashboard";
            }

            model.addAttribute("order", order);
            model.addAttribute("availableSurveyors", availableSurveyors);
            model.addAttribute("survey", new Survey());
            return "receptionist/create-survey-form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/receptionist/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi chuẩn bị form khảo sát: " + e.getMessage());
            return "redirect:/receptionist/dashboard";
        }
    }

   
    @PostMapping("/create-survey") 
    public String createSurvey(@RequestParam("orderId") Integer orderId,
                               @RequestParam("surveyorId") Integer surveyorId,
                               @RequestParam(value = "surveyResults", required = false) String surveyResults,
                               @RequestParam(value = "surveyDate", required = false) String surveyDateStr,
                               RedirectAttributes redirectAttributes) {
        try {
            OrderRequest order = orderRequestService.getOrderById(orderId);
            Surveyor selectedSurveyor = surveyorRepository.findAllActiveSurveyors()
                .stream()
                .filter(surveyor -> surveyor.getSurveyorId().equals(surveyorId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên khảo sát với ID: " + surveyorId));
            Long accountId = accountService.getCurrentUserRealId();
            Receptionist receptionist = receptionistRepository.findById(accountId.intValue())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin lễ tân"));

            Survey survey = new Survey();
            survey.setOrder(order);
            survey.setSurveyor(selectedSurveyor);
            survey.setReceptionist(receptionist);
            survey.setStatus("pending");

            // Xử lý surveyDate từ form
            if (surveyDateStr != null && !surveyDateStr.isEmpty()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ngày từ input type="date"
                    survey.setSurveyDate(dateFormat.parse(surveyDateStr));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("errorMessage", "Định dạng ngày khảo sát không hợp lệ.");
                    return "redirect:/receptionist/create-survey-form?orderId=" + orderId; // Quay lại form với lỗi
                }
            } else {
                survey.setSurveyDate(new Date()); // Mặc định là ngày hiện tại nếu người dùng không nhập
            }

            survey.setSurveyResults(surveyResults); // Kết quả khảo sát ban đầu có thể null
            survey.setCreatedAt(new Date());
            survey.setUpdatedAt(new Date());

            surveyService.saveSurvey(survey);

            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu khảo sát cho đơn hàng " + order.getOrderCode() + " đã được tạo thành công!");
            return "redirect:/receptionist/list-surveys";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/receptionist/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi tạo khảo sát: " + e.getMessage());
            return "redirect:/receptionist/dashboard";
        }
    }


@GetMapping("/edit-survey")
public String showEditSurveyForm(@RequestParam("surveyId") Integer surveyId, Model model) {
    try {
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
            model.addAttribute("errorMessage", "Không tìm thấy khảo sát.");
            return "redirect:/receptionist/list-surveys";
        }
        model.addAttribute("survey", survey);
        return "receptionist/edit-survey"; // Tạo file này
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("errorMessage", "Lỗi khi lấy khảo sát: " + e.getMessage());
        return "redirect:/receptionist/list-surveys";
    }
}

    @PostMapping("/edit-survey")
public String editSurvey(@ModelAttribute("survey") Survey updatedSurvey, RedirectAttributes redirectAttributes) {
    try {
        Survey existingSurvey = surveyService.getSurveyById(updatedSurvey.getSurveyId());
      
        if (existingSurvey == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khảo sát.");
            return "redirect:/receptionist/list-surveys";
        }

        // Cập nhật thông tin
        existingSurvey.setSurveyDate(updatedSurvey.getSurveyDate());
        existingSurvey.setSurveyResults(updatedSurvey.getSurveyResults());
        existingSurvey.setStatus(updatedSurvey.getStatus());

        surveyService.saveSurvey(existingSurvey); // hoặc update
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khảo sát thành công!");
    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật khảo sát: " + e.getMessage());
    }

    return "redirect:/receptionist/list-surveys";
}
   
    
}