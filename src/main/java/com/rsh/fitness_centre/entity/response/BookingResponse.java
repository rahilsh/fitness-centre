package com.rsh.fitness_centre.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO representing booking details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Booking response details")
public class BookingResponse {

    @Schema(description = "Booking ID", example = "1")
    private Long id;

    @Schema(description = "Client User ID", example = "1")
    private Long userId;

    @Schema(description = "Client User Name", example = "John Doe")
    private String userName;

    @Schema(description = "Booked Activity Slot ID", example = "1")
    private Long slotId;

    @Schema(description = "Type of activity booked", example = "YOGA")
    private String activity;

    @Schema(description = "Activity start time", example = "9")
    private int startTime;

    @Schema(description = "Activity end time", example = "10")
    private int endTime;

    @Schema(description = "Booking status", example = "BOOKED")
    private String status;

    @Schema(description = "Timestamp when the booking was created")
    private LocalDateTime bookedAt;

    public static BookingResponse fromEntity(com.rsh.fitness_centre.entity.Booking booking) {
        if (booking == null) return null;
        return BookingResponse.builder()
            .id(booking.getId())
            .userId(booking.getUser() != null ? booking.getUser().getId() : null)
            .userName(booking.getUser() != null ? booking.getUser().getName() : null)
            .slotId(booking.getSlot() != null ? booking.getSlot().getId() : null)
            .activity(booking.getSlot() != null ? booking.getSlot().getActivity().name() : null)
            .startTime(booking.getSlot() != null ? booking.getSlot().getStartTime() : 0)
            .endTime(booking.getSlot() != null ? booking.getSlot().getEndTime() : 0)
            .status(booking.getStatus() != null ? booking.getStatus().name() : null)
            .bookedAt(booking.getBookedAt())
            .build();
    }
}
