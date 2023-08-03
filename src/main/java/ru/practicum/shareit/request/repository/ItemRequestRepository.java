package ru.practicum.shareit.request.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.request.model.ItemRequest;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

//    ItemRequest findById(long id);

    List<ItemRequest> findAllByRequesterId(long userId, Sort sort);

    List<ItemRequest> findAllByRequesterIdNot(long userId, Pageable page);

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    @Query("SELECT r " +
            "FROM ItemRequest r " +
            "WHERE r.requester.id <> :userId " +
            "ORDER BY r.created DESC ")
    List<ItemRequest> findRequestsWithoutOwner(@Param("userId") Long userId, Pageable pageable);
}

