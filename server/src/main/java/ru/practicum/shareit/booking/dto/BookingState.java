package ru.practicum.shareit.booking.dto;

public enum BookingState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING,
    // Неподдерживаемый статус
    UNSUPPORTED;

    public static BookingState from(String stateText) {
        for (BookingState state : BookingState.values()) {
            if (state.name().equalsIgnoreCase(stateText)) {
                return state;
            }
        }
        return BookingState.UNSUPPORTED;
    }
}
