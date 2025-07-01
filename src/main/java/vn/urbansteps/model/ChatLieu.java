package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ChatLieu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_chat_lieu", nullable = false)
    private String tenChatLieu;
}
