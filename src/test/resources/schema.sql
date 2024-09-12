-- ENGINE=InnoDB 구문 제거
-- 인덱스 정의를 해당 테이블 생성 구문 내부로 이동
-- LineString -> GEOMETRY

-- 테이블 삭제
DROP TABLE IF EXISTS center;
DROP TABLE IF EXISTS delivery_destination;
DROP TABLE IF EXISTS dispatch;
DROP TABLE IF EXISTS dispatch_detail;
DROP TABLE IF EXISTS dispatch_number;
DROP TABLE IF EXISTS sm;
DROP TABLE IF EXISTS transport_order;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS vehicle;

CREATE TABLE center (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        latitude FLOAT(53) NOT NULL,
                        longitude FLOAT(53) NOT NULL,
                        zip_code VARCHAR(7) NOT NULL,
                        center_code VARCHAR(50) NOT NULL,
                        center_name VARCHAR(50) NOT NULL,
                        road_address VARCHAR(100) NOT NULL,
                        lot_number_address VARCHAR(100) NOT NULL,
                        detail_address VARCHAR(50) NOT NULL,
                        phone_number VARCHAR(20) NOT NULL,
                        manager_name VARCHAR(30) NOT NULL,
                        restricted_wing_body VARCHAR(20),
                        restricted_box VARCHAR(20),
                        restricted_cargo VARCHAR(20),
                        delay_time INTEGER NOT NULL,
                        comment VARCHAR(100),
                        create_at DATETIME(6),
                        update_at DATETIME(6),
                        PRIMARY KEY (id)
);

CREATE TABLE delivery_destination (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      latitude FLOAT(53) NOT NULL,
                                      longitude FLOAT(53) NOT NULL,
                                      zip_code VARCHAR(7) NOT NULL,
                                      center_id BIGINT,
                                      destination_name VARCHAR(100) NOT NULL,
                                      road_address VARCHAR(50) NOT NULL,
                                      lot_number_address VARCHAR(50) NOT NULL,
                                      detail_address VARCHAR(50) NOT NULL,
                                      phone_number VARCHAR(20) NOT NULL,
                                      manager_name VARCHAR(30) NOT NULL,
                                      restricted_wing_body VARCHAR(20),
                                      restricted_box VARCHAR(20),
                                      restricted_cargo VARCHAR(20),
                                      delay_time INTEGER NOT NULL,
                                      comment VARCHAR(100),
                                      create_at DATETIME(6),
                                      update_at DATETIME(6),
                                      PRIMARY KEY (id),
                                      INDEX delivery_destination_center_id_index (center_id)
);

CREATE TABLE dispatch (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          dispatch_number_id BIGINT,
                          sm_id BIGINT,
                          sm_name VARCHAR(30) NOT NULL,
                          completed_order_count INTEGER NOT NULL,
                          delivery_order_count INTEGER NOT NULL,
                          destination_count INTEGER NOT NULL,
                          loading_rate FLOAT(53) NOT NULL,
                          total_volume FLOAT(53) NOT NULL,
                          total_weight FLOAT(53) NOT NULL,
                          total_distance FLOAT(53) NOT NULL,
                          departure_place_code VARCHAR(4) NOT NULL,
                          departure_place_name VARCHAR(50) NOT NULL,
                          departure_time DATETIME(6) NOT NULL,
                          arrival_time DATETIME(6),
                          path GEOMETRY NOT NULL,
                          delivery_status ENUM ('MOVING','WORK_COMPLETED','WORK_WAITING','WORK_START','DELIVERY_DELAY','TRANSPORTATION_COMPLETED','CANCELED','RESTING') NOT NULL,
                          issue VARCHAR(300) NOT NULL,
                          total_time TIME(6) NOT NULL,
                          break_start_time TIME(6) NOT NULL,
                          break_end_time TIME(6) NOT NULL,
                          resting_stopover INTEGER NOT NULL,
                          create_at DATETIME(6),
                          update_at DATETIME(6),
                          PRIMARY KEY (id),
                          INDEX dispatch_dispatch_number_id_index (dispatch_number_id),
                          INDEX dispatch_sm_id_index (sm_id)
);

CREATE TABLE dispatch_detail (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 dispatch_id BIGINT,
                                 transport_order_id BIGINT,
                                 destination_id BIGINT,
                                 destination_latitude FLOAT(53) NOT NULL,
                                 destination_longitude FLOAT(53) NOT NULL,
                                 distance FLOAT(53) NOT NULL,
                                 expectation_operation_start_time DATETIME(6) NOT NULL,
                                 expectation_operation_end_time DATETIME(6) NOT NULL,
                                 loading_completion_time DATETIME(6),
                                 operation_start_time DATETIME(6) NOT NULL,
                                 operation_end_time DATETIME(6) NOT NULL,
                                 transportation_start_time DATETIME(6),
                                 destination_type ENUM ('CENTER','CUSTOMER_DESTINATION','DELIVERY_DESTINATION') NOT NULL,
                                 dispatch_detail_status ENUM ('TRANSPORTATION_START', 'MOVING', 'WORK_COMPLETED', 'WORK_WAITING', 'WORK_START', 'TRANSPORTATION_COMPLETED', 'DELIVERY_DELAY', 'CANCELED', 'RESTING') NOT NULL,
                                 ett INTEGER NOT NULL,
                                 is_resting BIT NOT NULL,
                                 delayed_time INTEGER ,
                                 create_at DATETIME(6),
                                 update_at DATETIME(6),
                                 PRIMARY KEY (id),
                                 INDEX dispatch_detail_dispatch_id_index (dispatch_id),
                                 INDEX dispatch_detail_transport_order_id_index (transport_order_id)
);

CREATE TABLE dispatch_number (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 center_id BIGINT,
                                 manager_id BIGINT,
                                 dispatch_number VARCHAR(20) NOT NULL,
                                 dispatch_name VARCHAR(50) NOT NULL,
                                 status ENUM ('COMPLETED','IN_TRANSIT','WAITING') NOT NULL,
                                 loading_start_time DATETIME(6) NOT NULL,
                                 create_at DATETIME(6),
                                 update_at DATETIME(6),
                                 PRIMARY KEY (id),
                                 UNIQUE (dispatch_number),
                                 INDEX dispatch_number_center_id_index (center_id),
                                 INDEX dispatch_number_users_id_index (manager_id)
);

CREATE TABLE sm (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    center_id BIGINT,
                    logistics_code VARCHAR(20) NOT NULL,
                    sm_name VARCHAR(30) NOT NULL,
                    join_date DATE NOT NULL,
                    work_start_time TIME(6) NOT NULL,
                    break_start_time TIME(6) NOT NULL,
                    break_time TIME(6) NOT NULL,
                    address VARCHAR(100) NOT NULL,
                    contract_type ENUM ('JIIP','DELIVERY') NOT NULL,
                    completed_num_of_month INTEGER NOT NULL,
                    contract_num_of_month INTEGER NOT NULL,
                    create_at DATETIME(6),
                    update_at DATETIME(6),
                    PRIMARY KEY (id),
                    INDEX sm_center_id_index (center_id)
);

CREATE TABLE transport_order (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 shipment_number VARCHAR(100) NOT NULL,
                                 order_number VARCHAR(100),
                                 order_date DATE NOT NULL,
                                 order_type VARCHAR(10),
                                 delivery_type VARCHAR(10) NOT NULL,
                                 product_code VARCHAR(100),
                                 product_name VARCHAR(100) NOT NULL,
                                 product_count INTEGER NOT NULL,
                                 volume FLOAT(53) NOT NULL,
                                 weight FLOAT(53) NOT NULL,
                                 customer_name VARCHAR(30) NOT NULL,
                                 customer_phone_number VARCHAR(255),
                                 customer_notes VARCHAR(100),
                                 zip_code VARCHAR(7) NOT NULL,
                                 road_address VARCHAR(50) NOT NULL,
                                 lot_number_address VARCHAR(50) NOT NULL,
                                 detail_address VARCHAR(50) NOT NULL,
                                 requested_arrival_time TIME(6) NOT NULL,
                                 requested_work_date DATE NOT NULL,
                                 estimated_work_time TIME(6),
                                 is_pending BIT NOT NULL,
                                 center_id BIGINT,
                                 sm_name VARCHAR(30) NOT NULL,
                                 create_at DATETIME(6),
                                 update_at DATETIME(6),
                                 PRIMARY KEY (id),
                                 INDEX transport_order_center_id_index (center_id)
);

CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(30) NOT NULL,
                       username VARCHAR(30) NOT NULL,
                       encrypted_password VARCHAR(80) NOT NULL,
                       phone_number VARCHAR(20) NOT NULL,
                       role ENUM ('SUPER_ADMIN', 'ADMIN','DRIVER') NOT NULL,
                       center_id BIGINT,
                       sm_id BIGINT,
                       create_at DATETIME(6),
                       update_at DATETIME(6),
                       PRIMARY KEY (id),
                       INDEX users_center_id_index (center_id),
                       INDEX users_sm_id_index (sm_id)
);

CREATE TABLE vehicle (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         vehicle_number VARCHAR(255) NOT NULL,
                         vehicle_type ENUM ('WING_BODY','BOX','CARGO') NOT NULL,
                         vehicle_ton FLOAT(53) NOT NULL,
                         manufacture_year INTEGER NOT NULL,
                         max_load_volume FLOAT(53) NOT NULL,
                         max_load_weight FLOAT(53) NOT NULL,
                         fuel_efficiency FLOAT(53) NOT NULL,
                         height FLOAT(53) NOT NULL,
                         width FLOAT(53) NOT NULL,
                         length FLOAT(53) NOT NULL,
                         usable_height FLOAT(53) NOT NULL,
                         usable_width FLOAT(53) NOT NULL,
                         usable_length FLOAT(53) NOT NULL,
                         ownership_type BIT NOT NULL,
                         center_id BIGINT,
                         sm_id BIGINT,
                         created_by VARCHAR(255) NOT NULL,
                         updated_by VARCHAR(255) NOT NULL,
                         create_at DATETIME(6),
                         update_at DATETIME(6),
                         PRIMARY KEY (id),
                         INDEX vehicle_center_id_index (center_id),
                         INDEX vehicle_sm_id_index (sm_id)
);