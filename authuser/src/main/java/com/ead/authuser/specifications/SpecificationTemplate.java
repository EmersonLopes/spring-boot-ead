package com.ead.authuser.specifications;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "userType", spec = Equal.class),
            @Spec(path = "userStatus", spec = Equal.class),
            @Spec(path = "email", spec = Like.class)
    })
    public interface UserSpec extends Specification<UserModel> {
    }

    public static Specification<UserModel> userCourseId(final UUID courseId) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            Join<UserModel, UserCourseModel> usersCourses = root.join("usersCourses");
            return criteriaBuilder.equal(usersCourses.get("courseId"), courseId);
        });
    }

}