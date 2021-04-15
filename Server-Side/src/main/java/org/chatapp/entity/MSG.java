package org.chatapp.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "msgsWaiting")
@IdClass(MSGId.class)
public class MSG {
    @Id
    private String emitter;
    @Id
    private String destination;
    @Id
    private LocalDateTime time;

    private String content;

    public MSG(String emitter, String destination, LocalDateTime time, String content) {
        this.emitter = emitter;
        this.destination = destination;
        this.time = time;
        this.content = content;
    }

    public MSG() {}

    public String getEmitter() {
        return emitter;
    }

    public void setEmitter(String emitter) {
        this.emitter = emitter;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDateTime() {
        return time;
    }

    public void setDateTime(LocalDateTime time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
