package ru.practicum.shareit.item.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem_Id(Long itemId, Sort sort);

    List<Comment> findAllByItem_Id(Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.item.id IN :itemsId ORDER BY c.id ASC ")
    List<Comment> findAllByItemsId(@Param("itemsId") List<Long> itemsId);
}
