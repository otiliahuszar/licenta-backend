package com.fsega.timetable.service;

import com.fsega.timetable.model.external.CourseMultipleEditDto;
import com.fsega.timetable.model.internal.Course;
import com.fsega.timetable.model.internal.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendUpcomingCourseEmail(User user, Course course) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setFrom("noreply@timetablemanager.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Timetable Manager - Your have an upcoming course");

        String sb = "<div>Hello " + user.getFullName() + ",</div>" +
                "<br/><div>You have an upcoming course: </div>" +
                buildCourseMessage(course) +
                "<br/><div><i>Regards,</i></div>" +
                "<div><i>Timetable Manager</i></div>";
        helper.setText(sb, true);

        javaMailSender.send(msg);
    }

    public void sendCourseUpdateEmail(User user, Course course) throws MessagingException {
        if (!user.isReceiveEmailNotificationsForUpdates()) {
            return;
        }
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setFrom("noreply@timetablemanager.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Timetable Manager - Your course was updated");

        String sb = "<div>Hello " + user.getFullName() + ",</div><br/>" +
                "<div>One of your courses was updated: </div>" +
                buildCourseMessage(course) +
                "<br/><div><i>Regards,</i></div>" +
                "<div><i>Timetable Manager</i></div>";
        helper.setText(sb, true);

        javaMailSender.send(msg);
    }

    public void sendCourseUpdateEmail(User user, Set<Course> courses, CourseMultipleEditDto dto) throws MessagingException {
        if (!user.isReceiveEmailNotificationsForUpdates()) {
            return;
        }
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        helper.setFrom("noreply@timetablemanager.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Timetable Manager - Your courses were updated");

        String sb = "<div>Hello " + user.getFullName() + ",</div><br/>" +
                "<div>Some of your courses were updated: </div>" +
                buildCoursesMessage(courses) +
                "<br/><div>The following info was changed: </div>" +
                buildUpdatesMessage(dto) +
                "<br/><div><i>Regards,</i></div>" +
                "<div><i>Timetable Manager</i></div>";
        helper.setText(sb, true);

        javaMailSender.send(msg);
    }

    private String buildCoursesMessage(Set<Course> courses) {
        StringBuilder sb = new StringBuilder();

        courses.forEach(c -> sb.append("<div><b>Teacher: </b>")
                .append(c.getTeacher().getFullName())
                .append(", <b>Subject: </b>")
                .append(c.getSubject().getName())
                .append(", <b>Date: </b>")
                .append(c.getDate().toString())
                .append(", <b>Hour: </b>")
                .append(c.getStartHour()).append(":00-")
                .append(c.getEndHour()).append(":00</div>")
        );

        return sb.toString();
    }

    private String buildUpdatesMessage(CourseMultipleEditDto dto) {
        StringBuilder sb = new StringBuilder();

        if (dto.isEditTitle()) {
            sb.append("<div><b>Title: </b>").append(dto.getTitle()).append("</div>");
        }
        if (dto.isEditDescription()) {
            sb.append("<div><b>Description: </b>").append(dto.getDescription()).append("</div>");
        }
        if (dto.isEditLocation()) {
            sb.append("<div><b>Location: </b>").append(dto.getLocation()).append("</div>");
        }
        if (dto.isEditResources()) {
            sb.append("<div><b>Resources: </b>").append(dto.getResources()).append("</div>");
        }
        return sb.toString();
    }

    private String buildCourseMessage(Course course) {
        StringBuilder sb = new StringBuilder();

        sb.append("<div><b>Teacher: </b>")
                .append(course.getTeacher().getFullName())
                .append("</div>");
        sb.append("<div><b>Subject: </b>")
                .append(course.getSubject().getName())
                .append("</div>");
        sb.append("<div><b>Date: </b>")
                .append(course.getDate().toString())
                .append("</div>");
        sb.append("<div><b>Hour: </b>")
                .append(course.getStartHour()).append(":00-")
                .append(course.getEndHour()).append(":00</div>");

        if (course.getTitle() != null) {
            sb.append("<div><b>Title: </b>")
                    .append(course.getTitle())
                    .append("</div>");
        }
        if (course.getDescription() != null) {
            sb.append("<div><b>Description: </b>")
                    .append(course.getDescription())
                    .append("</div>");
        }
        if (course.getLocation() != null) {
            sb.append("<div><b>Location: </b>")
                    .append(course.getLocation())
                    .append("</div>");
        }
        if (course.getResources() != null) {
            sb.append("<div><b>Resources: </b>")
                    .append(course.getResources())
                    .append("</div>");
        }
        return sb.toString();
    }
}
