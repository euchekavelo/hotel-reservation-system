package ru.mephi.bookingservice.adapter.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import ru.mephi.bookingservice.dto.external.request.RoomReservationRequestDto;
import ru.mephi.bookingservice.dto.external.request.ShortRoomReservationRequestDto;
import ru.mephi.bookingservice.dto.external.response.RoomReservationResponseDto;
import ru.mephi.bookingservice.exception.ExternalServiceException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class HotelManagementServiceApiAdapter {

    private final WebClient webClient;

    @Value("${hotel-management-service.api.timeout}")
    private int timeout;

    @Value("${hotel-management-service.api.retries}")
    private int retries;

    @Autowired
    public HotelManagementServiceApiAdapter(WebClient.Builder webClientBuilder,
                                            @Value("${hotel-management-service.api.base-url}") String baseUrl) {

        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public long confirmRoomAvailability(long roomId, long bookingId, LocalDate startDate, LocalDate endDate, UUID requestId) {
        RoomReservationRequestDto roomReservationRequestDto = new RoomReservationRequestDto();
        roomReservationRequestDto.setBookingId(bookingId);
        roomReservationRequestDto.setStartDate(startDate);
        roomReservationRequestDto.setEndDate(endDate);
        roomReservationRequestDto.setRequestId(requestId);

        try {
            RoomReservationResponseDto roomReservationResponseDto = webClient.post()
                    .uri("/rooms/{roomId}/confirm-availability", roomId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(roomReservationRequestDto)
                    .retrieve()
                    .bodyToMono(RoomReservationResponseDto.class)
                    .timeout(Duration.ofMillis(timeout))
                    .retryWhen(Retry.backoff(retries, Duration.ofMillis(500)).maxBackoff(Duration.ofMillis(3000)))
                    .block();

            return roomReservationResponseDto.getId();
        } catch (Exception ex) {
            throw new ExternalServiceException("Произошла ошибка при попытке создать резерв номера: " + ex.getMessage());
        }
    }

    public void removeRoomReservation(long roomId, long reservationId) {
        ShortRoomReservationRequestDto shortRoomReservationRequestDto = new ShortRoomReservationRequestDto();
        shortRoomReservationRequestDto.setReservationId(reservationId);

        try {
            webClient.post()
                    .uri("/rooms/{roomId}/release", roomId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(shortRoomReservationRequestDto)
                    .retrieve()
                    .bodyToMono(RoomReservationResponseDto.class)
                    .timeout(Duration.ofMillis(timeout))
                    .retryWhen(Retry.backoff(retries, Duration.ofMillis(500)))
                    .block();
        } catch (Exception ex) {
            throw new ExternalServiceException("Произошла ошибка при попытке снять резерв с номера: " + ex.getMessage());
        }
    }

    public void deleteRoomReservationByBookingId(long bookingId) {
        try {
            webClient.delete()
                    .uri("/room-reservations/by-booking/{bookingId}", bookingId)
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofMillis(timeout))
                    .retryWhen(Retry.backoff(retries, Duration.ofMillis(500)))
                    .block();
        } catch (Exception ex) {
            throw new ExternalServiceException("Произошла ошибка при попытке удаления резерва комнаты: " + ex.getMessage());
        }
    }
}
