package ru.mephi.bookingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.bookingservice.adapter.api.HotelManagementServiceApiAdapter;
import ru.mephi.bookingservice.dto.request.BookingRequestDto;
import ru.mephi.bookingservice.exception.ExternalServiceException;
import ru.mephi.bookingservice.model.User;
import ru.mephi.bookingservice.model.enums.Role;
import ru.mephi.bookingservice.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookingTestController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @MockitoBean
    private HotelManagementServiceApiAdapter hotelManagementServiceApiAdapter;

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.token-expiration-milliseconds}")
    private Long tokenExpirationMills;

    private static User userWithUserRole;

    @BeforeAll
    static void beforeAll() {
        userWithUserRole = new User();
        userWithUserRole.setRole(Role.USER);
        userWithUserRole.setUsername("test1");
        userWithUserRole.setPassword("test1");
        userWithUserRole.setBookingList(new ArrayList<>());
    }

    @Test
    void createBookingSuccess() throws Exception {
        User savedUser = userService.createUser(userWithUserRole);
        String token = createToken(savedUser.getId().toString(), savedUser.getRole().name());

        Mockito.when(hotelManagementServiceApiAdapter.confirmRoomAvailability(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.any(UUID.class))).thenReturn(1L);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setAutoSelect(false);
        bookingRequestDto.setRoomId(1L);
        bookingRequestDto.setStartDate(LocalDate.now().plusYears(1));
        bookingRequestDto.setEndDate(LocalDate.now().plusYears(2));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("status").value("CONFIRMED"));
    }

    @Test
    void createBookingFailure() throws Exception {
        User savedUser = userService.createUser(userWithUserRole);
        String token = createToken(savedUser.getId().toString(), savedUser.getRole().name());

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setAutoSelect(false);
        bookingRequestDto.setRoomId(1L);
        bookingRequestDto.setStartDate(LocalDate.now().plusYears(3));
        bookingRequestDto.setEndDate(LocalDate.now().plusYears(2));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createBookingTestWithCompensation() throws Exception {
        User savedUser = userService.createUser(userWithUserRole);
        String token = createToken(savedUser.getId().toString(), savedUser.getRole().name());
        LocalDate startDate = LocalDate.now().plusYears(1);
        LocalDate endDate = LocalDate.now().plusYears(2);

        Mockito.when(hotelManagementServiceApiAdapter.confirmRoomAvailability(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(ExternalServiceException.class);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setAutoSelect(false);
        bookingRequestDto.setRoomId(2L);
        bookingRequestDto.setStartDate(startDate);
        bookingRequestDto.setEndDate(endDate);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value("CANCELLED"));
    }

    private String createToken(String subject, String scope) {
        HashMap<String, Object> tokenAttributes = new HashMap<>();
        tokenAttributes.put("scope", scope);

        long currentSeconds = System.currentTimeMillis();
        Claims finalClaims = Jwts.claims(tokenAttributes);
        finalClaims.setSubject(subject);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(finalClaims)
                .setIssuedAt(new Date(currentSeconds))
                .setExpiration(new Date(currentSeconds + tokenExpirationMills))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
