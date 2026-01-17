package com.devteam.academicservice.service;

import com.devteam.academicservice.dto.request.CreateCourseDTO;
import com.devteam.academicservice.dto.request.UpdateCourseRequestDTO;
import com.devteam.academicservice.dto.response.CourseDetailsResponseDTO;
import com.devteam.academicservice.dto.response.CourseResponse;
import com.devteam.academicservice.exception.BusinessException;
import com.devteam.academicservice.exception.ErrorCode;
import com.devteam.academicservice.model.Course;
import com.devteam.academicservice.model.Member;
import com.devteam.academicservice.repository.CourseRepository;
import com.devteam.academicservice.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.devteam.academicservice.security.SecurityUtils.getCurrentUser;

@Service
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository repository;

    public CourseService(CourseRepository repository, CourseMapper courseMapper) {
        this.repository = repository;
        this.courseMapper = courseMapper;
    }

    public List<Course> getAllCourses() {
        return this.repository.findAll();
    }

    public void createCourse(CreateCourseDTO course) {
        checkCourseName(course.getName());

        UserPrincipal currentUser = getCurrentUser();

        Member teacher = Member.builder()
                .memberId(currentUser.getUserId())
                .firstname(currentUser.getFirstname())
                .lastname(currentUser.getLastname())
                .build();

        Course savedCourse = courseMapper.toCourse(course, teacher);

        this.repository.save(savedCourse);
    }

    public List<CourseResponse> getAllCoursesById() {
        UserPrincipal currentUser = getCurrentUser();
        String userId = currentUser.getUserId();
        List<CourseResponse> courseResponseList = this.repository.findAllByMemberId(userId)
                .stream()
                .map(course -> CourseResponse.builder()
                    .id(course.getId())
                    .name(course.getName())
                    .teacherId(course.getTeacherId())
                    .teacherFirstname(course.getTeacherFirstname())
                        .teacherLastname(course.getTeacherLastname())
                    .subTeacherId(course.getSubTeacherId())
                    .subTeacherName(course.getSubTeacherName()).build()
                )
                .toList();

        if (courseResponseList.isEmpty()) throw new BusinessException(ErrorCode.COURSE_NOTFOUND, userId);

        return courseResponseList;
    }

    public CourseDetailsResponseDTO getCourseById(String courseId) {
        Course course = this.repository.findById(courseId).orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOTFOUND, courseId));
        checkIsUserMember(course);
        return CourseDetailsResponseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .invitedCode(course.getInvitedCode())
                .teacherId(course.getTeacherId())
                .teacherFirstname(course.getTeacherFirstname())
                .teacherLastname(course.getTeacherLastname())
                .subTeacherId(course.getSubTeacherId())
                .subTeacherName(course.getSubTeacherName())
                .members(course.getMembers())
                .createdDate(course.getCreatedDate())
                .lastModifiedDate(course.getLastModifiedDate())
                .build();
    }

    private void checkIsUserMember(Course course) {

        UserPrincipal currentUser = getCurrentUser();
        String userId = currentUser.getUserId();

        boolean isMember = course.getMembers().stream().anyMatch(member -> member.getMemberId().equals(userId));

        if (!isMember) throw new BusinessException(ErrorCode.INVALID_MEMBER, course.getName());

    }

    public void updateCourseById(String courseId, UpdateCourseRequestDTO requestDTO) {
        Course course = this.repository.findById(courseId).orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOTFOUND, courseId));
        this.courseMapper.mergeCourse(course, requestDTO);
        this.repository.save(course);
    }

    public void addMember(String invitedCode) {
        Course course = this.repository.findByInvitedCode(invitedCode).orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOTFOUND, invitedCode));

        UserPrincipal currentUser = getCurrentUser();

        System.out.println("Current user: "+currentUser);

        Member student = Member.builder()
                .memberId(currentUser.getUserId())
                .firstname(currentUser.getFirstname())
                .lastname(currentUser.getLastname())
                .build();

        course.getMembers().add(student);
        this.repository.save(course);
    }

    public void leaveCourse(String courseId) {
        UserPrincipal currentUser = getCurrentUser();
        removeMember(courseId, currentUser.getUserId());
    }

    public void removeMember(String courseId, String memberId) {
        Course course = this.repository.findById(courseId).orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOTFOUND, courseId));
        course.getMembers().removeIf(member -> member.getMemberId().equals(memberId));
        this.repository.save(course);
    }



    public void deleteCourseById(String courseId) {
        if (!isCourseExists(courseId)) throw new BusinessException(ErrorCode.COURSE_NOTFOUND, courseId);
        this.repository.deleteById(courseId);
    }

    private boolean isCourseExists(String courseId) {
        return this.repository.existsById(courseId);
    }

    private void checkCourseName(String name) {
        final boolean exist = this.repository.existsByNameIgnoreCase(name);
        if (exist) throw new BusinessException(ErrorCode.COURSE_ALREADY_EXISTS, name);
    }

}
