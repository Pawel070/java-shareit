package ru.practicum.shareit.item.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(Long userId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND ((LOWER(i.name) LIKE LOWER(:text)) " +
            "OR (LOWER(i.description) LIKE LOWER (:text)))")
    List<Item> searchAvailableItems(@Param("text") String text);

    void deleteById(Long id);

}
