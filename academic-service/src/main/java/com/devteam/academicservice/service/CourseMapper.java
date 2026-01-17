package com.devteam.academicservice.service;

import com.devteam.academicservice.dto.request.CreateCourseDTO;
import com.devteam.academicservice.dto.request.UpdateCourseRequestDTO;
import com.devteam.academicservice.model.Course;
import com.devteam.academicservice.model.Member;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@Service
public class CourseMapper {

    public void mergeCourse(Course course, UpdateCourseRequestDTO requestDTO) {
        if (StringUtils.isNotBlank(requestDTO.getName()) && !requestDTO.getName().equals(course.getName())) course.setName(requestDTO.getName());
        if (StringUtils.isNotBlank(requestDTO.getDescription()) && !requestDTO.getDescription().equals(course.getDescription())) course.setDescription(requestDTO.getDescription());

    }

    public Course toCourse(CreateCourseDTO courseDTO, Member teacher) {
        return Course.builder()
                .name(courseDTO.getName())
                .description(courseDTO.getDescription())
                .teacherId(teacher.getMemberId())
                .teacherFirstname(teacher.getFirstname())
                .teacherLastname(teacher.getLastname())
                .subTeacherId(courseDTO.getSubTeacherId())
                .subTeacherName(courseDTO.getSubTeacherName())
                .members(List.of(teacher))
                .build();
    }

}
