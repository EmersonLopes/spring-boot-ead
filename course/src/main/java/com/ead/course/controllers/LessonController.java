package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@PathVariable("moduleId") UUID moduleId,
                                             @RequestBody @Valid LessonDto lessonDto) {

        Optional<ModuleModel> optionalModuleModel = moduleService.findById(moduleId);

        if (!optionalModuleModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(optionalModuleModel.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lessonModel));
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable("moduleId") UUID moduleId,
                                               @PathVariable("lessonId") UUID lessonId) {

        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);

        if (!optionalLessonModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }

        lessonService.delete(optionalLessonModel.get());

        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfuly");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable("moduleId") UUID moduleId,
                                               @PathVariable("lessonId") UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto) {
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);

        if (!optionalLessonModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }


        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setTitle(lessonDto.getTitle());
        lessonModel.setDescription(lessonDto.getDescription());
        lessonModel.setVideoUrl(lessonDto.getVideoUrl());

        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModel));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<List<LessonModel>> getAllLesson(@PathVariable("moduleId")UUID moduleId){
        return ResponseEntity.ok(lessonService.findAllByModule(moduleId));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(@PathVariable("moduleId") UUID moduleId,
                                               @PathVariable("lessonId") UUID lessonId){
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);

        if (!optionalLessonModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        return ResponseEntity.ok(optionalLessonModel.get());
    }
}
