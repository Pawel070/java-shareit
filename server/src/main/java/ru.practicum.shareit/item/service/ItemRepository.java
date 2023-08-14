package ru.practicum.shareit.item.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    //void deleteById(Long id);

    List<Item> findByOwner_Id(Long userId, Pageable pageable);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND ((LOWER(i.name) LIKE LOWER(concat('%',:text,'%'))) " +
            "OR (LOWER(i.description) LIKE LOWER(concat('%',:text,'%'))))")
    List<Item> searchAvailableItems(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequest_IdOrderByIdDesc(Long userId);

    List<Item> findAllByOwnerId(long ownerId, Pageable pageable);
}
