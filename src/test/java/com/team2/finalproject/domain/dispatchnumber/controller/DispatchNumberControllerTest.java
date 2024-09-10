package com.team2.finalproject.domain.dispatchnumber.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.finalproject.domain.dispatchnumber.model.dto.request.DispatchNumberSearchRequest;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.util.BaseIntegrationTest;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DispatchNumberControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        log.info("Initializing schema");
        initSchema();
        log.info("Schema initialized");

        log.info("Setting up test data");
        truncateTables();
        insertCenters(jdbcTemplate);
        insertDeliveryDestinations(jdbcTemplate);
        insertDispatchNumbers(jdbcTemplate);
        insertDispatches(jdbcTemplate);
        insertDispatchDetails(jdbcTemplate);
        insertSMs(jdbcTemplate);
        insertTransportOrders(jdbcTemplate);
        insertVehicles(jdbcTemplate);
        registerUsers();
        log.info("Test data setup completed");
    }

    @Test
    void testGetDispatchList() throws Exception {
        Long dispatchCodeId = 1L;
        String jwtToken = loginAsAdmin(); // 로그인 (setUp이 아닌 메서드 내 사용)
        log.info("Testing getDispatchList with dispatchCodeId: {}", dispatchCodeId);

        mockMvc.perform(get("/api/dispatch-number/{dispatchCodeId}/vehicle-control", dispatchCodeId)
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchCode").exists())
                .andExpect(jsonPath("$.dispatchName").exists())
                .andExpect(jsonPath("$.totalProgressionRate").isNumber())
                .andExpect(jsonPath("$.totalCompletedOrderNum").isNumber())
                .andExpect(jsonPath("$.totalOrderNum").isNumber())
                .andExpect(jsonPath("$.issueOrderNum").isNumber())
                .andExpect(jsonPath("$.startStopover").exists())
                .andExpect(jsonPath("$.startStopover.centerId").isNumber())
                .andExpect(jsonPath("$.startStopover.address").isString())
                .andExpect(jsonPath("$.startStopover.lat").isNumber())
                .andExpect(jsonPath("$.startStopover.lon").isNumber())
                .andExpect(jsonPath("$.startStopover.delayTime").isNumber())
                .andExpect(jsonPath("$.dispatchList").isArray())
                .andExpect(jsonPath("$.dispatchList[0].dispatchId").isNumber())
                .andExpect(jsonPath("$.dispatchList[0].dispatchStatus").isString())
                .andExpect(jsonPath("$.dispatchList[0].smName").isString())
                .andExpect(jsonPath("$.dispatchList[0].completedOrderNum").isNumber())
                .andExpect(jsonPath("$.dispatchList[0].orderNum").isNumber())
                .andExpect(jsonPath("$.dispatchList[0].progressionRate").isNumber())
                .andExpect(jsonPath("$.dispatchList[0].stopoverList").isArray())
                .andExpect(jsonPath("$.dispatchList[0].coordinates").isArray())
                .andExpect(jsonPath("$.issueList").isArray())
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("getDispatchList 성공");
    }

    @Test
    void testGetDispatchList_NotFound() throws Exception {
        Long nonExistentDispatchCodeId = 9999L; // 존재하지 않는 ID
        String jwtToken = loginAsAdmin();
        log.info("Testing getDispatchList with non-existent dispatchCodeId: {}", nonExistentDispatchCodeId);

        mockMvc.perform(get("/api/dispatch-number/{dispatchCodeId}/vehicle-control", nonExistentDispatchCodeId)
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // 404 Not Found 예상
                .andExpect(jsonPath("$.message").value("404 배차코드를 찾지 못했습니다."))
                .andExpect(jsonPath("$.statusMessage").value("배차코드를 찾지 못했습니다."))
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("getDispatchList 배차코드 not found 성공");
    }

    @Test
    void testSearchDispatches_DefaultSearch() throws Exception {
        String jwtToken = loginAsAdmin();
        DispatchNumberSearchRequest request = new DispatchNumberSearchRequest(
                DispatchNumberStatus.WAITING,
                true,
                LocalDate.now().minusDays(7),
                LocalDateTime.now().plusDays(7),
                null,
                null
        );

        mockMvc.perform(get("/api/dispatch-number")
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", request.status().toString())
                        .param("isManager", String.valueOf(request.isManager()))
                        .param("startDate", request.startDate().toString())
                        .param("endDateTime", request.endDateTime().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inProgress").isNumber())
                .andExpect(jsonPath("$.waiting").isNumber())
                .andExpect(jsonPath("$.completed").isNumber())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].dispatchNumberId").isNumber())
                .andExpect(jsonPath("$.results[0].progress").isNumber())
                .andExpect(jsonPath("$.results[0].dispatchCode").isString())
                .andExpect(jsonPath("$.results[0].dispatchName").isString())
                .andExpect(jsonPath("$.results[0].startDateTime").isString())
                .andExpect(jsonPath("$.results[0].totalOrder").isNumber())
                .andExpect(jsonPath("$.results[0].smNum").isNumber())
                .andExpect(jsonPath("$.results[0].manager").isString())
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("searchDispatches default search 성공");
    }

    @Test
    void testSearchDispatches_ByDispatchCode() throws Exception {
        String jwtToken = loginAsAdmin();
        DispatchNumberSearchRequest request = new DispatchNumberSearchRequest(
                DispatchNumberStatus.WAITING,
                true,
                LocalDate.now().minusDays(7),
                LocalDateTime.now(),
                "dispatchCode",
                "DN01-1"
        );

        mockMvc.perform(get("/api/dispatch-number")
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", request.status().toString())
                        .param("isManager", String.valueOf(request.isManager()))
                        .param("startDate", request.startDate().toString())
                        .param("endDateTime", request.endDateTime().toString())
                        .param("searchOption", request.searchOption())
                        .param("searchKeyword", request.searchKeyword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].dispatchCode").value("DN01-1"))
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("searchDispatches by dispatch code 성공");
    }

    @Test
    void testSearchDispatches_ByDispatchName() throws Exception {
        String jwtToken = loginAsAdmin();
        DispatchNumberSearchRequest request = new DispatchNumberSearchRequest(
                DispatchNumberStatus.WAITING,
                true,
                LocalDate.now().minusDays(7),
                LocalDateTime.now(),
                "dispatchName",
                "Dispatch Number 1 for Center 1"
        );

        mockMvc.perform(get("/api/dispatch-number")
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", request.status().toString())
                        .param("isManager", String.valueOf(request.isManager()))
                        .param("startDate", request.startDate().toString())
                        .param("endDateTime", request.endDateTime().toString())
                        .param("searchOption", request.searchOption())
                        .param("searchKeyword", request.searchKeyword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].dispatchName").value("Dispatch Number 1 for Center 1"))
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("searchDispatches by dispatch name test completed successfully");
    }

    @Test
    void testSearchDispatches_ByManager() throws Exception {
        String jwtToken = loginAsAdmin();
        DispatchNumberSearchRequest request = new DispatchNumberSearchRequest(
                DispatchNumberStatus.WAITING,
                true,
                LocalDate.now().minusDays(7),
                LocalDateTime.now(),
                "manager",
                "exampleAdmin"
        );

        mockMvc.perform(get("/api/dispatch-number")
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", request.status().toString())
                        .param("isManager", String.valueOf(request.isManager()))
                        .param("startDate", request.startDate().toString())
                        .param("endDateTime", request.endDateTime().toString())
                        .param("searchOption", request.searchOption())
                        .param("searchKeyword", request.searchKeyword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].manager").value("exampleAdmin"))
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("searchDispatches by manager test completed successfully");
    }

    @Test
    void testSearchDispatches_ByDriver() throws Exception {
        String jwtToken = loginAsAdmin();
        DispatchNumberSearchRequest request = new DispatchNumberSearchRequest(
                DispatchNumberStatus.WAITING,
                true,
                LocalDate.now().minusDays(7),
                LocalDateTime.now(),
                "driver",
                "SM Name 1"
        );

        mockMvc.perform(get("/api/dispatch-number")
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", request.status().toString())
                        .param("isManager", String.valueOf(request.isManager()))
                        .param("startDate", request.startDate().toString())
                        .param("endDateTime", request.endDateTime().toString())
                        .param("searchOption", request.searchOption())
                        .param("searchKeyword", request.searchKeyword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray())
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("searchDispatches by driver test completed successfully");
    }

    @Test
    void testSearchDispatches_InvalidSearchOption() throws Exception {
        String jwtToken = loginAsAdmin();
        DispatchNumberSearchRequest request = new DispatchNumberSearchRequest(
                DispatchNumberStatus.WAITING,
                true,
                LocalDate.now().minusDays(7),
                LocalDateTime.now(),
                "invalidOption",
                "someKeyword"
        );

        mockMvc.perform(get("/api/dispatch-number")
                        .cookie(new Cookie("accessToken", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", request.status().toString())
                        .param("isManager", String.valueOf(request.isManager()))
                        .param("startDate", request.startDate().toString())
                        .param("endDateTime", request.endDateTime().toString())
                        .param("searchOption", request.searchOption())
                        .param("searchKeyword", request.searchKeyword()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 wrong search option"))
                .andExpect(jsonPath("$.statusMessage").value("wrong search option"))
                .andDo(result -> log.info("Response: {}", result.getResponse().getContentAsString()));

        log.info("searchDispatches invalid search option test completed successfully");
    }
}