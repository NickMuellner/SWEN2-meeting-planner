package org.example.meetingplanner.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.config.JpaManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;

import java.util.List;

public class MeetingRepositoryJpaImpl implements MeetingRepository {
    private static final Logger log = LogManager.getLogger(MeetingRepositoryJpaImpl.class);

    @Override
    public List<Meeting> findAll() {
        log.debug("Finding all Meetings using JPA Criteria API");
        try (EntityManager em = JpaManager.getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Meeting> cq = cb.createQuery(Meeting.class);
            Root<Meeting> root = cq.from(Meeting.class);

            // Use LEFT JOIN FETCH to eagerly load tour logs
            root.fetch("meeting_notes", JoinType.LEFT);

            // Add DISTINCT to avoid duplicate Tour entities due to JOIN
            cq.select(root).distinct(true);

            List<Meeting> meetings = em.createQuery(cq).getResultList();
            log.debug("Found {} meetings", meetings.size());
            return meetings;
        } catch (Exception e) {
            log.error("Error finding all meetings", e);
            throw new RuntimeException("Failed to find meetings", e);
        }
    }

    @Override
    public Meeting findById(int id) {
        log.debug("Finding meeting by id: {}", id);
        try (EntityManager em = JpaManager.getEntityManager()) {
            Meeting meeting = em.find(Meeting.class, id);
            if (meeting != null) {
                // Initialize the logs collection
                meeting.getNotes().size();
            }
            return meeting;
        } catch (Exception e) {
            log.error("Error finding meeting by id: {}", id, e);
            throw new RuntimeException("Failed to find meeting by id", e);
        }
    }

    @Override
    public void save(Meeting meeting) {
        log.debug("Saving meeting: {}", meeting.getTitle());
        EntityManager em = JpaManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try (em) {
            tx.begin();
            if (meeting.getId() == 0) {
                em.persist(meeting);
                log.debug("Persisted new meeting with id: {}", meeting.getId());
            } else {
                em.merge(meeting);
                log.debug("Updated existing meeting with id: {}", meeting.getId());
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Error saving meeting", e);
            throw new RuntimeException("Failed to save meeting", e);
        }
    }

    @Override
    public void delete(Meeting meeting) {
        log.debug("Deleting meeting: {}", meeting.getTitle());
        EntityManager em = JpaManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try (em) {
            tx.begin();
            Meeting managedMeeting = em.find(Meeting.class, meeting.getId());
            if (managedMeeting != null) {
                em.remove(managedMeeting);
                log.debug("Deleted meeting with id: {}", meeting.getId());
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Error deleting meeting", e);
            throw new RuntimeException("Failed to delete meeting", e);
        }
    }

    @Override
    public void saveMeetingNote(MeetingNote meetingNote) {
        log.debug("Saving meeting note for meeting id: {}", meetingNote.getMeeting().getId());
        EntityManager em = JpaManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try (em) {
            tx.begin();
            if (meetingNote.getId() == 0) {
                em.persist(meetingNote);
                log.debug("Persisted new meeting note with id: {}", meetingNote.getId());
            } else {
                em.merge(meetingNote);
                log.debug("Updated existing meeting note with id: {}", meetingNote.getId());
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Error saving meeting note", e);
            throw new RuntimeException("Failed to save meeting note", e);
        }
    }

    @Override
    public void deleteMeetingNote(MeetingNote meetingNote) {
        log.debug("Deleting meeting note with id: {}", meetingNote.getId());
        EntityManager em = JpaManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try (em) {
            tx.begin();
            MeetingNote managedMeetingNote = em.find(MeetingNote.class, meetingNote.getId());
            if (managedMeetingNote != null) {
                em.remove(managedMeetingNote);
                log.debug("Deleted meeting note with id: {}", meetingNote.getId());
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            log.error("Error deleting meeting note", e);
            throw new RuntimeException("Failed to delete meeting note", e);
        }
    }
}