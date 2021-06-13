package com.fsega.timetable.service;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fsega.timetable.exception.CsvException;
import com.fsega.timetable.model.external.*;
import com.fsega.timetable.model.internal.*;

import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final String tempFile = "temp.csv";
    private final List<String> errors = new ArrayList<>();

    private Map<String, Specialization> specializations = new HashMap<>();
    private Map<String, Subject> subjects = new HashMap<>();
    private Map<String, User> teachers = new HashMap<>();
    private MultiKeyMap<String, Semester> semesters = new MultiKeyMap<>();
    private List<Course> courses = new ArrayList<>();

    private final SpecializationService specializationService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final SemesterService semesterService;
    private final CourseService courseService;

    @Transactional
    public void createTimetable(LocalDate semesterStart, LocalDate semesterEnd,
                                byte[] content) throws IOException {
        cleanup();
        writeContentToTempFile(content);
        splitAndHandleCsvSections(semesterStart, semesterEnd);

        if (errors.size() > 0) {
            throw new CsvException("Errors while processing CSV file", errors);
        }
    }

    private void writeContentToTempFile(byte[] content) throws IOException {
        OutputStream os = new FileOutputStream(tempFile);
        os.write(Base64.getDecoder().decode(content));
        os.close();
    }

    private void splitAndHandleCsvSections(LocalDate semesterStart, LocalDate semesterEnd) throws IOException {
        FileReader fr = new FileReader(tempFile);
        BufferedReader br = new BufferedReader(fr);

        String currentSection = "";
        int lineNo = 0;
        String line;

        while ((line = br.readLine()) != null) {
            lineNo++;
            if (line.startsWith("#")) {
                currentSection = line;
            } else {
                switch (currentSection) {
                    case "# Specializations":
                        processSpecializationLine(line, lineNo);
                        break;
                    case "# Subjects":
                        processSubjectLine(line, lineNo);
                        break;
                    case "# Teachers":
                        processTeacherLine(line, lineNo);
                        break;
                    case "# Courses":
                        processCourseLine(line, lineNo, semesterStart, semesterEnd);
                        break;
                }
            }
        }
        fr.close();
    }

    private void processSpecializationLine(String line, int currentLine) {
        String[] s = line.split(",");

        if (s.length != 2) {
            errors.add(format("Line %d: Specialization line should contain Internal ID and Name", currentLine));
            return;
        }
        SpecializationDto dto = SpecializationDto.builder()
                .internalId(s[0])
                .name(s[1])
                .build();
        Specialization specialization = specializationService.createSpecialization(dto);
        specializations.put(specialization.getInternalId(), specialization);
    }

    private void processSubjectLine(String line, int currentLine) {
        String[] s = line.split(",");

        if (s.length != 2) {
            errors.add(format("Line %d: Subject line should contain Internal ID and Name", currentLine));
            return;
        }
        SubjectDto dto = SubjectDto.builder()
                .internalId(s[0])
                .name(s[1])
                .build();
        Subject subject = subjectService.createSubject(dto);
        subjects.put(subject.getInternalId(), subject);
    }

    private void processTeacherLine(String line, int currentLine) {
        String[] s = line.split(",");

        if (s.length != 1) {
            errors.add(format("Line %d: Teacher line should contain teacher username", currentLine));
            return;
        }
        Optional<User> opt = userService.createTeacher(line);

        if (opt.isEmpty()) {
            errors.add(format("Line %d: Teacher with username %s doesn't exist in LDAP", currentLine, line));
            return;
        }
        User teacher = opt.get();
        teachers.put(teacher.getUsername(), teacher);
    }

    private void processCourseLine(String line, int currentLine, LocalDate startDate, LocalDate endDate) {
        String[] s = line.split(",");

        if (s.length != 7) {
            errors.add(format("Line %d: Course line should contain specialization, study year, subject, " +
                    "teacher, date, start hour, end hour", currentLine));
            return;
        }

        Specialization spec = specializations.get(s[0]);
        if (spec == null) {
            errors.add(format("Line %d: Specialization with id %s was not found", currentLine, s[0]));
            return;
        }
        Subject subject = subjects.get(s[2]);
        if (subject == null) {
            errors.add(format("Line %d: Subject with id %s was not found", currentLine, s[2]));
            return;
        }
        User teacher = teachers.get(s[3]);
        if (teacher == null) {
            errors.add(format("Line %d: Teacher with username %s was not found", currentLine, s[3]));
            return;
        }
        String studyYear = s[1];
        CourseDto dto = CourseDto.builder()
                .date(LocalDate.parse(s[4]))
                .startHour(Integer.valueOf(s[5]))
                .endHour(Integer.valueOf(s[6]))
                .studyYear(Integer.valueOf(studyYear))
                .build();
        if (dto.getDate().isBefore(startDate) || dto.getDate().isAfter(endDate)) {
            errors.add(format("Line %d: The date must be during the semester", currentLine));
            return;
        }
        if (dto.getStartHour() >= dto.getEndHour()) {
            errors.add(format("Line %d: The start hour must be lower than the end hour", currentLine));
            return;
        }
        Semester sem = semesters.get(spec.getInternalId(), studyYear);
        if (sem == null) {
            sem = semesterService.createSemester(startDate, endDate, dto.getStudyYear(), spec);
            semesters.put(spec.getInternalId(), studyYear, sem);
        }
        courses.add(courseService.createCourse(dto, sem, teacher, subject));
    }

    private void cleanup() {
        errors.clear();
        specializations.clear();
        subjects.clear();
        teachers.clear();
        courses.clear();
        semesters.clear();
    }
}
