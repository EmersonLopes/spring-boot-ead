package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
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
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CourseService courseService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@PathVariable("courseId") UUID courseId, @RequestBody @Valid ModuleDto moduleDto) {

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(moduleModel));
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable("courseId") UUID courseId, @PathVariable("moduleId") UUID moduleId) {

        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        if (!optionalModuleModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }

        moduleService.delete(optionalModuleModel.get());

        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfuly");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateCourse(@PathVariable("courseId") UUID courseId,
                                               @PathVariable("moduleId") UUID moduleId,
                                               @RequestBody @Valid ModuleDto moduleDto) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        if (!optionalModuleModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }


        var moduleModel = new ModuleModel();
        moduleModel.setTitle(moduleModel.getTitle());
        moduleModel.setDescription(moduleModel.getDescription());


        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(moduleModel));
    }

    @GetMapping("courses/{courseId}/modules")
    public ResponseEntity<List<ModuleModel>> getAllModules(@PathVariable("courseId")UUID courseId){
        return ResponseEntity.ok(moduleService.findAllByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(@PathVariable("courseId") UUID courseId,
                                               @PathVariable("moduleId") UUID moduleId){
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        if (!optionalModuleModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        return ResponseEntity.ok(optionalModuleModel.get());
    }
}
