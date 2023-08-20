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

    List<Booking> findByItemId(Long itemId, Sort sort);

    Booking findFirstByBooker_IdAndItem_Id(Long bookerId, Long itemId);

    List<Booking> findByItemIdIn(List<Long> itemsIds, Sort sort);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Long userId, Status status);

    //List<Booking> findAllByBookerIdAndItemId(Long bookerId, Long itemId, Status status, LocalDateTime end);
    //List<Booking> findByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId, Status status, LocalDateTime end);
    List<Booking> findByBookerIdAndItemIdAndStatusAndStartIsBefore(Long userId, Long itemId, Status status, LocalDateTime end);

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

//    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, Status status, LocalDateTime time);

//    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime time);

//    List<Booking> findAllByItem_IdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime now);

//    List<Booking> findAllByItem_IdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime now);

    List<Booking> findAllByItem_IdAndStartBefore(Long itemId, LocalDateTime now);

    List<Booking> findAllByItem_IdAndEndAfter(Long itemId, LocalDateTime now);


    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.end < ?3 order by b.start asc")
    List<Booking> findPastOwnerBookings(Long itemId, Long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.start > ?3 " +
            "order by b.start asc")
    List<Booking> findFutureOwnerBookings(Long itemId, Long ownerId, LocalDateTime now);


}
