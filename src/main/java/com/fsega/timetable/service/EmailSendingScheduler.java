package com.fsega.timetable.service;

import com.fsega.timetable.model.internal.Course;
import com.fsega.timetable.model.internal.Semester;
import com.fsega.timetable.model.internal.User;
import com.fsega.timetable.repository.CourseRepository;
import com.fsega.timetable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmailSendingScheduler {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EmailService emailService;

    @Transactional
    @Scheduled(cron = "0 */1 * * * *")
    public void sendEmailNotifications() {
        userRepository.findAllByReceiveEmailNotificationsBeforeCourses(true)
                .forEach(this::sendEmails);
    }

    private void sendEmails(User user) {
        LocalDateTime expectedCourseDate = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES)
                .plusMinutes(user.getNotificationInterval());

        Set<Course> publicCourses = user.getPublicCourses().stream()
                .filter(c -> c.getFullStartDate().isEqual(expectedCourseDate))
                .collect(Collectors.toSet());

        Set<UUID> semesterIds = user.getSemesters().stream()
                .map(Semester::getId)
                .collect(Collectors.toSet());
        Set<Course> courses = courseRepository.findAllBySemester_IdIn(semesterIds).stream()
                .filter(c -> c.getFullStartDate().isEqual(expectedCourseDate))
                .collect(Collectors.toSet());
        try {
            for (Course c : courses) {
                emailService.sendUpcomingCourseEmail(user, c);
            }
            for (Course c : publicCourses) {
                emailService.sendUpcomingCourseEmail(user, c);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
