package ru.practicum.shareit.item.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);
/*
    @Query(" SELECT i FROM Item i WHERE lower(i.name) LIKE lower(concat('%', :search, '%')) OR lower(i.description) " +
            "LIKE lower(concat('%', :search, '%')) AND i.available = TRUE")
    List<Item> getItemsBySearchQuery(@Param("search") String text, Pageable pageable);
 */

     @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', ?1, '%')) "
            + "or lower(item.description) like lower(concat('%', ?1, '%')) ")
        List<Item> searchItemsByText(String text, Pageable pageable);
}
