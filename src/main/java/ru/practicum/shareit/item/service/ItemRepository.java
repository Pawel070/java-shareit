package ru.practicum.shareit.item.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);
/*
    @Query(" SELECT i FROM Item i WHERE lower(i.name) LIKE lower(concat('%', :search, '%')) OR lower(i.description) " +
            "LIKE lower(concat('%', :search, '%')) AND i.available = TRUE")
    List<Item> getItemsBySearchQuery(@Param("search") String text, Pageable pageable);


     @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', ?1, '%')) "
            + "or lower(item.description) like lower(concat('%', ?1, '%')) ")
        List<Item> searchItemsByText(String text, Pageable pageable);

 */

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND ((LOWER(i.name) LIKE LOWER(:text)) " +
            "OR (LOWER(i.description) LIKE LOWER (:text)))")
    List<Item> searchItemsByText(@Param("text") String text);

}
