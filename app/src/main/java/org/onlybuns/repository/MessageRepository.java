package org.onlybuns.repository;

import org.onlybuns.model.Message;
import org.onlybuns.model.Post;
import org.onlybuns.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  //  List<Message> findAllByReceiverUsername(String receiver);
  @Query("SELECT m FROM Message m WHERE m.receiverUsername = :receiver OR m.senderUsername = :receiver")
  List<Message> findAllChats(@Param("receiver") String receiver);

  @Query("SELECT m FROM Message m WHERE m.receiverUsername = :receiver")
  List<Message> findAllByReceiverUsername(@Param("receiver") String receiver);


  @Query("SELECT m FROM Message m WHERE (m.receiverUsername = :receiver AND m.senderUsername = :sender) OR (m.receiverUsername = :sender AND m.senderUsername = :receiver)   ORDER BY  m.dateTime DESC LIMIT 1")
    Message FindLastMessage(@Param("sender") String sender, @Param("receiver") String receiver);

  @Query("SELECT m FROM Message m WHERE m.receiverUsername = :receiver  ORDER BY  m.dateTime DESC LIMIT 1")
  Message FindLastGroupMessage (@Param("receiver") String receiver);


  @Query("SELECT m FROM Message m WHERE ( m.receiverUsername = :receiver AND m.senderUsername = :sender ) OR ( m.receiverUsername = :sender AND m.senderUsername = :receiver) ORDER BY  m.dateTime ASC")
    List<Message> FindPreviousMEssages(@Param("sender") String sender, @Param("receiver") String receiver);

    @Query("SELECT m FROM Message m WHERE m.receiverUsername = :receiver AND m.senderUsername = :sender")
    List<Message> findAllReceivedFromUser(@Param("sender") String sender, @Param("receiver") String receiver);


}
