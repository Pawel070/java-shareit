package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query(value = "update Booking b set b.status = :status where b.id = :id")
    void approvedBooking(Status status, Long id);


    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Long userId, Status status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long userId, Status status);

    @Query("SELECT (COUNT(b) > 0) FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :userId AND b.end < :now")
    boolean isItemWasUsedByUser(@Param("itemId") Long itemId, @Param("userId") Long userId, @Param("now") LocalDateTime now);

    Booking findFirstByItem_IdAndItem_Owner_IdAndStartIsBefore(Long itemId, Long userId, LocalDateTime end, Sort sort);

    List<Booking> findFirstByItem_IdInAndItem_Owner_IdAndStartIsBefore(List<Long> itemsId, Long userId, LocalDateTime end, Sort sort);

    Booking findFirstByItem_IdAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
            Long itemId, Long userId, LocalDateTime start, Status status1, Status status2, Sort sort);

    List<Booking> findFirstByItem_IdInAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
            List<Long> itemsId, Long userId, LocalDateTime start, Status status1, Status status2, Sort sort);

}
