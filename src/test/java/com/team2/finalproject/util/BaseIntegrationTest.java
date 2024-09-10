package com.team2.finalproject.util;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Disabled
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    // schema.sql 파일을 실행하는 메서드
    @PostConstruct
    protected void initSchema() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
        }
    }

    // 테이블 비우기
    protected void truncateTables() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_TYPE='TABLE'",
                String.class
        );
        for (String tableName : tableNames) {
            if (!tableName.equalsIgnoreCase("GEOMETRY_COLUMNS") && !tableName.equalsIgnoreCase("SPATIAL_REF_SYS")) {
                try {
                    jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
                } catch (Exception e) {
                    // Log the exception or handle it as needed
                    System.out.println("Could not truncate table: " + tableName);
                }
            }
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    // 사용자 등록
    protected void registerUsers() {
        registerSuperAdmin("exampleSuperAdmin", "superAdmin", "password", "010-1234-5678");
        registerAdmin(1, "exampleAdmin", "exampleAdmin", "password", "010-0000-0000");
        registerDriver(1, 1, "exampleDriver", "exampleDriver", "password", "010-8282-8282");
    }

    private void registerSuperAdmin(String name, String username, String password, String phoneNumber) {
        String encryptedPassword = passwordEncoder.encode(password);
        jdbcTemplate.update(
                "INSERT INTO users (name, username, encrypted_password, phone_number, role, create_at, update_at) " +
                        "VALUES (?, ?, ?, ?, 'SUPER_ADMIN', NOW(), NOW())",
                name, username, encryptedPassword, phoneNumber
        );
    }

    private void registerAdmin(long centerId, String name, String username, String password, String phoneNumber) {
        String encryptedPassword = passwordEncoder.encode(password);
        jdbcTemplate.update(
                "INSERT INTO users (name, username, encrypted_password, phone_number, role, center_id, create_at, update_at) " +
                        "VALUES (?, ?, ?, ?, 'ADMIN', ?, NOW(), NOW())",
                name, username, encryptedPassword, phoneNumber, centerId
        );
    }

    private void registerDriver(long centerId, long smId, String name, String username, String password, String phoneNumber) {
        String encryptedPassword = passwordEncoder.encode(password);
        jdbcTemplate.update(
                "INSERT INTO users (name, username, encrypted_password, phone_number, role, center_id, sm_id, create_at, update_at) " +
                        "VALUES (?, ?, ?, ?, 'DRIVER', ?, ?, NOW(), NOW())",
                name, username, encryptedPassword, phoneNumber, centerId, smId
        );
    }

    // 로그인 메서드
    protected String loginAsSuperAdmin() throws Exception {
        return login("superAdmin", "password");
    }

    protected String loginAsAdmin() throws Exception {
        return login("exampleAdmin", "password");
    }

    protected String loginAsDriver() throws Exception {
        return login("exampleDriver", "password");
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        Cookie[] cookies = result.getResponse().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new RuntimeException("Access token not found in response cookies");
    }

    // center 데이터 삽입
    protected void insertCenters(JdbcTemplate jdbcTemplate) {
        for (int i = 1; i <= 5; i++) {
            String centerCode = String.format("C%03d", i);
            double latitude = 33.0 + i * 1.5; // 한국 내 위도
            double longitude = 126.0 + i * 1.5; // 한국 내 경도
            jdbcTemplate.update(
                    "INSERT INTO center (latitude, longitude, zip_code, center_code, center_name, road_address, " +
                            "lot_number_address, detail_address, phone_number, manager_name, restricted_wing_body, " +
                            "restricted_box, restricted_cargo, delay_time, comment, create_at, update_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                    latitude, longitude, "123-45" + i, centerCode, "Center " + i,
                    "Road Address " + i, "Lot Number Address " + i, "Detail Address " + i,
                    "010-1234-56" + i, "Manager " + i, "None", "None", "None", 10 * i,
                    "Comment for Center " + i
            );
        }
    }

    // DeliveryDestination 데이터 삽입
    protected void insertDeliveryDestinations(JdbcTemplate jdbcTemplate) {
        for (int centerId = 1; centerId <= 5; centerId++) {
            for (int j = 1; j <= 5; j++) {
                double latitude = 33.0 + centerId * 1.5 + j * 0.01;
                double longitude = 126.0 + centerId * 1.5 + j * 0.01;
                String zipCode = String.format("123-%03d", (centerId * 10 + j) % 1000);
                jdbcTemplate.update(
                        "INSERT INTO delivery_destination (latitude, longitude, zip_code, center_id, destination_name, road_address, " +
                                "lot_number_address, detail_address, phone_number, manager_name, restricted_wing_body, " +
                                "restricted_box, restricted_cargo, delay_time, comment, create_at, update_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                        latitude, longitude, zipCode, centerId,
                        "Delivery Destination " + j + " for Center " + centerId,
                        "Road Address " + j, "Lot Number Address " + j, "Detail Address " + j,
                        "010-5678-90" + j, "Manager " + j, "None", "None", "None", 5 * j,
                        "Comment for Delivery Destination " + j
                );
            }
        }
    }

    // DispatchNumber 데이터 삽입
    protected void insertDispatchNumbers(JdbcTemplate jdbcTemplate) {
        for (int centerId = 1; centerId <= 5; centerId++) {
            for (int j = 1; j <= 5; j++) {
                String dispatchNumber = String.format("DN%02d-%d", centerId, j);
                jdbcTemplate.update(
                        "INSERT INTO dispatch_number (center_id, manager_id, dispatch_number, dispatch_name, status, " +
                                "loading_start_time, create_at, update_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW(), NOW())",
                        centerId, 2, dispatchNumber, "Dispatch Number " + j + " for Center " + centerId, "WAITING"
                );
            }
        }
    }

    // Dispatch 데이터 삽입
    protected void insertDispatches(JdbcTemplate jdbcTemplate) {
        for (int dispatchNumberId = 1; dispatchNumberId <= 25; dispatchNumberId++) {
            for (int j = 1; j <= 5; j++) {
                String lineString = String.format("LINESTRING(%d %d, %d %d)", j, j, j + 1, j + 1);
                jdbcTemplate.update(
                        "INSERT INTO dispatch (dispatch_number_id, sm_id, sm_name, completed_order_count, delivery_order_count, " +
                                "destination_count, loading_rate, total_volume, total_weight, total_distance, departure_place_code, " +
                                "departure_place_name, departure_time, path, delivery_status, issue, total_time, break_start_time, break_end_time, resting_stopover, create_at, update_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ST_GeomFromText(?), ?, ?, ?, '00:00:00', '00:00:00', 0, NOW(), NOW())",
                        dispatchNumberId, 1, "SM Name " + dispatchNumberId, j, j * 2, 5, 100.0 / j,
                        1000.0 * j, 100.0 * j, 50.0 * j, "DP01", "Departure Place", lineString, "IN_TRANSIT", "No issues", "00:30:00"
                );
            }
        }
    }

    // DispatchDetail 데이터 삽입
    protected void insertDispatchDetails(JdbcTemplate jdbcTemplate) {
        for (int dispatchId = 1; dispatchId <= 125; dispatchId++) {
            for (int j = 1; j <= 5; j++) {
                jdbcTemplate.update(
                        "INSERT INTO dispatch_detail (dispatch_id, transport_order_id, destination_id, destination_latitude, " +
                                "destination_longitude, distance, expectation_operation_start_time, expectation_operation_end_time, " +
                                "operation_start_time, operation_end_time, destination_type, dispatch_detail_status, ett, is_resting, " +
                                "create_at, update_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), NOW(), NOW(), ?, ?, ?, ?, NOW(), NOW())",
                        dispatchId, j, j, 37.0 + j * 0.01, 127.0 + j * 0.01, 10.0 * j, "DELIVERY_DESTINATION", "MOVING", 30, false
                );
            }
        }
    }

    // SM 데이터 삽입
    protected void insertSMs(JdbcTemplate jdbcTemplate) {
        for (int centerId = 1; centerId <= 5; centerId++) {
            jdbcTemplate.update(
                    "INSERT INTO sm (center_id, logistics_code, sm_name, join_date, work_start_time, break_start_time, " +
                            "break_time, address, contract_type, completed_num_of_month, contract_num_of_month, create_at, update_at) " +
                            "VALUES (?, ?, ?, NOW(), '08:00:00', '12:00:00', '01:00:00', ?, ?, ?, ?, NOW(), NOW())",
                    centerId, "LC" + centerId, "SM Name " + centerId, "Address " + centerId,
                    "JIIP", 0, 12
            );
        }
    }

    // TransportOrder 데이터 삽입
    protected void insertTransportOrders(JdbcTemplate jdbcTemplate) {
        for (int centerId = 1; centerId <= 5; centerId++) {
            for (int j = 1; j <= 10; j++) {
                jdbcTemplate.update(
                        "INSERT INTO transport_order (shipment_number, order_date, delivery_type, product_code, product_name, " +
                                "product_count, volume, weight, customer_name, zip_code, road_address, lot_number_address, detail_address, " +
                                "requested_arrival_time, requested_work_date, is_pending, center_id, sm_name, create_at, update_at) " +
                                "VALUES (?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '12:00:00', NOW(), ?, ?, ?, NOW(), NOW())",
                        "TO" + j, "DELIVERY", "PC" + j, "Product " + j, 10, 1.0, 1.0, "Customer " + j, "123-456", "Road Address",
                        "Lot Number", "Detail Address", false, centerId, "SM Name " + centerId
                );
            }
        }
    }

    // Vehicle 데이터 삽입
    protected void insertVehicles(JdbcTemplate jdbcTemplate) {
        String[] vehicleTypes = {"WING_BODY", "BOX", "CARGO"};
        for (int centerId = 1; centerId <= 5; centerId++) {
            for (int j = 1; j <= 5; j++) {
                String vehicleType = vehicleTypes[j % 3];  // 3가지 타입을 순환하며 선택
                jdbcTemplate.update(
                        "INSERT INTO vehicle (vehicle_number, vehicle_type, vehicle_ton, manufacture_year, max_load_volume, " +
                                "max_load_weight, fuel_efficiency, height, width, length, usable_height, usable_width, usable_length, " +
                                "ownership_type, center_id, sm_id, created_by, updated_by, create_at, update_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                        "VN" + j, vehicleType, 1.5 * j, 2020, 10.0 * j, 5.0 * j, 15.0, 2.5, 2.0, 6.0, 2.4, 1.8, 5.8, true, centerId, j + centerId - 1, "Admin", "Admin"
                );
            }
        }
    }
}