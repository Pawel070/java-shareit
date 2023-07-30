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


//  Item findById(Long id);
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
    List<Item> searchAvailableItems(@Param("text") String text);

   void  deleteById(Long id);

}
