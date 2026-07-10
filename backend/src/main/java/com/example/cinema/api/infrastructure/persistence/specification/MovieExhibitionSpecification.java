package com.example.cinema.api.infrastructure.persistence.specification;

import com.example.cinema.api.application.dto.movie.MovieListingFilterDTO;
import com.example.cinema.api.domain.movie.MovieCategoryFilter;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.domain.movie.MovieSession;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MovieExhibitionSpecification {

    public static Specification<MovieExhibition> withFilters(Long cinemaId, MovieListingFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("cinema").get("id"), cinemaId));
            predicates.add(cb.isTrue(root.get("active")));
            predicates.add(cb.isTrue(root.get("movie").get("inTheaters")));

            if (filter != null) {
                if (filter.getFormat() != null) {
                    predicates.add(cb.equal(root.get("format"), filter.getFormat()));
                }

                if (filter.getAudio() != null) {
                    predicates.add(cb.equal(root.get("audio"), filter.getAudio()));
                }

                if (MovieCategoryFilter.PRE_ESTREIA.equals(filter.getCategory())) {
                    predicates.add(cb.isTrue(root.get("movie").get("preRelease")));
                } else if (MovieCategoryFilter.HOJE.equals(filter.getCategory())) {
                    Subquery<Long> sessionSubquery = query.subquery(Long.class);
                    Root<MovieSession> sessionRoot = sessionSubquery.from(MovieSession.class);
                    sessionSubquery.select(sessionRoot.get("id"));
                    sessionSubquery.where(
                            cb.equal(sessionRoot.get("movieExhibition"), root),
                            cb.equal(sessionRoot.get("showDate"), LocalDate.now()),
                            cb.greaterThanOrEqualTo(sessionRoot.get("startTime"), LocalTime.now()),
                            cb.isFalse(sessionRoot.get("canceled"))
                    );
                    predicates.add(cb.exists(sessionSubquery));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
