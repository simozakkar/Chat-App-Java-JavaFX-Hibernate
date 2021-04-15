package org.chatapp.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class MSGId implements Serializable {

    private String emitter;

    private String destination;

    private LocalDateTime time;

    public MSGId(String emitter, String destination, LocalDateTime time) {
        this.emitter = emitter;
        this.destination = destination;
        this.time = time;
    }

    public MSGId() {}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MSGId msgId = (MSGId) o;
        return Objects.equals(emitter, msgId.emitter) && Objects.equals(destination, msgId.destination) && Objects.equals(time, msgId.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emitter, destination, time);
    }
}
