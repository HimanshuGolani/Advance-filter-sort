package com.filtersort.service;

import com.filtersort.entity.DatasetRecord;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuerySpecificationService {

    private static final Logger log = LoggerFactory.getLogger(QuerySpecificationService.class);

    public Specification<DatasetRecord> parseSearchParams(MultiValueMap<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, List<String>> param : params.entrySet()) {
                String key = param.getKey();
                List<String> values = param.getValue();

                // Skip pagination, sorting, and operation params
                if (isSystemParam(key)) {
                    continue;
                }

                try {
                    Path<?> jsonPath = root.get("jsonData");

                    if (values.get(0).startsWith("in:")) {
                        String[] valuesArray = values.get(0).substring(3).split(",");
                        List<String> trimmedValues = Arrays.stream(valuesArray)
                                .map(String::trim)
                                .collect(Collectors.toList());

                        // For JSON fields, we need to use JSON functions (database specific)
                        predicates.add(createJsonInPredicate(criteriaBuilder, jsonPath, key, trimmedValues));
                        continue;
                    }

                    for (String value : values) {
                        if (value.startsWith("eq:")) {
                            predicates.add(createJsonEqualPredicate(criteriaBuilder, jsonPath, key, value.substring(3)));
                        } else if (value.startsWith("like:")) {
                            predicates.add(createJsonLikePredicate(criteriaBuilder, jsonPath, key, value.substring(5)));
                        } else if (value.startsWith("gt:")) {
                            predicates.add(createJsonGreaterThanPredicate(criteriaBuilder, jsonPath, key, value.substring(3)));
                        } else if (value.startsWith("gte:")) {
                            predicates.add(createJsonGreaterThanOrEqualPredicate(criteriaBuilder, jsonPath, key, value.substring(4)));
                        } else if (value.startsWith("lt:")) {
                            predicates.add(createJsonLessThanPredicate(criteriaBuilder, jsonPath, key, value.substring(3)));
                        } else if (value.startsWith("lte:")) {
                            predicates.add(createJsonLessThanOrEqualPredicate(criteriaBuilder, jsonPath, key, value.substring(4)));
                        }
                    }
                } catch (Exception e) {
                    log.error("Error creating predicate for {}:{}", key, values, e);
                }
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private boolean isSystemParam(String key) {
        return "page".equals(key) || "size".equals(key) || "sort".equals(key) ||
                "groupBy".equals(key) || "sortBy".equals(key) || "order".equals(key);
    }

    // JSON-specific predicate methods (simplified for H2, can be extended for PostgreSQL)
    private Predicate createJsonEqualPredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                               Path<?> jsonPath, String key, String value) {
        // This is a simplified version - in production, you'd use database-specific JSON functions
        return cb.equal(cb.function("JSON_EXTRACT", String.class, jsonPath, cb.literal("$." + key)), value);
    }

    private Predicate createJsonLikePredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                              Path<?> jsonPath, String key, String value) {
        return cb.like(cb.function("JSON_EXTRACT", String.class, jsonPath, cb.literal("$." + key)), "%" + value + "%");
    }

    private Predicate createJsonGreaterThanPredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                                     Path<?> jsonPath, String key, String value) {
        return cb.greaterThan(cb.function("JSON_EXTRACT", String.class, jsonPath, cb.literal("$." + key)), value);
    }

    private Predicate createJsonGreaterThanOrEqualPredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                                            Path<?> jsonPath, String key, String value) {
        return cb.greaterThanOrEqualTo(cb.function("JSON_EXTRACT", String.class, jsonPath, cb.literal("$." + key)), value);
    }

    private Predicate createJsonLessThanPredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                                  Path<?> jsonPath, String key, String value) {
        return cb.lessThan(cb.function("JSON_EXTRACT", String.class, jsonPath, cb.literal("$." + key)), value);
    }

    private Predicate createJsonLessThanOrEqualPredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                                         Path<?> jsonPath, String key, String value) {
        return cb.lessThanOrEqualTo(cb.function("JSON_EXTRACT", String.class, jsonPath, cb.literal("$." + key)), value);
    }

    private Predicate createJsonInPredicate(jakarta.persistence.criteria.CriteriaBuilder cb,
                                            Path<?> jsonPath, String key, List<String> values) {
        Path<String> jsonField = (Path<String>) cb.function("JSON_EXTRACT", String.class, jsonPath, cb.literal("$." + key));
        return jsonField.in(values);
    }
}